package com.naskoni.voting.controller;

import com.naskoni.voting.dto.VoteResponseDto;
import com.naskoni.voting.enumeration.VoteType;
import com.naskoni.voting.exception.InvalidPathVariableException;
import com.naskoni.voting.repository.VoteRepository;
import com.naskoni.voting.service.VoteService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

/**
 * The endpoints for which this controller is responsible are expected to be called concurrently.
 * The chosen DB lock strategy is OPTIMISTIC_FORCE_INCREMENT (see {@link VoteRepository}), that's
 * why the controller methods will retry in case of {@link StaleObjectStateException}. The {@link
 * Retryable} properties are defined in application.properties and can be easily tuned. When the
 * value of maxAttempts is exceeded the {@link ErrorHandler} will be triggered and HTTP Status 503
 * SERVICE_UNAVAILABLE will be returned.
 *
 * @author Atanas Atanasov
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class VoteController {

  @Autowired private VoteService voteService;

  @PostMapping("/{postId}/votes/{voteValue}")
  @Retryable(
      value = StaleObjectStateException.class,
      backoff = @Backoff(delayExpression = "${voting.post.backoff}"),
      maxAttemptsExpression = "${voting.post.maxAttempts}")
  public VoteResponseDto addVote(@PathVariable Long postId, @PathVariable String voteValue) {
    try {
      VoteType voteType = VoteType.valueOf(voteValue.toUpperCase());
      log.info(String.format("Submitted %s vote for blog post with id: %d", voteValue, postId));
      VoteResponseDto dto = voteService.addVote(postId, voteType);
      log.info(
          String.format(
              "Successfuly updated votes count for blog post with id: %d. New value is: %d",
              postId, dto.getVotesCount()));
      return dto;
    } catch (IllegalArgumentException e) {
      log.error(e.getMessage(), e);
      throw new InvalidPathVariableException(
          String.format(
              "Invalid vote type: %s. Expected string value: 'positive' or 'negative'", voteValue));
    }
  }

  @GetMapping("/{postId}/votes")
  @Retryable(
      value = StaleObjectStateException.class,
      backoff = @Backoff(delayExpression = "${voting.get.backoff}"),
      maxAttemptsExpression = "${voting.get.maxAttempts}")
  public VoteResponseDto getVotes(@PathVariable Long postId) throws InterruptedException {
    log.info(String.format("Requested votes count for blog post with id: %d", postId));
    VoteResponseDto dto = voteService.getVotes(postId);
    log.info(
        String.format(
            "Successfuly returned votes count for blog post with id: %d. The value is: %d",
            postId, dto.getVotesCount()));
    return dto;
  }
}

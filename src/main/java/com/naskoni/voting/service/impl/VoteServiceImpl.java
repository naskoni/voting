package com.naskoni.voting.service.impl;

import com.naskoni.voting.dto.VoteResponseDto;
import com.naskoni.voting.entity.BlogPostVote;
import com.naskoni.voting.enumeration.VoteType;
import com.naskoni.voting.exception.NotFoundException;
import com.naskoni.voting.repository.VoteRepository;
import com.naskoni.voting.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {

  private static final String POST_NOT_FOUND = "Post with id: %d could not be found";

  @Autowired private VoteRepository voteRepository;

  @Override
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public VoteResponseDto addVote(Long postId, VoteType voteType) {
    Optional<BlogPostVote> voteOptional = voteRepository.findByBlogPostId(postId);
    if (!voteOptional.isPresent()) {
      throw new NotFoundException(String.format(POST_NOT_FOUND, postId));
    }

    BlogPostVote vote = voteOptional.get();
    switch (voteType) {
      case POSITIVE:
        vote.setPositiveVotes(vote.getPositiveVotes() + 1);
        break;
      case NEGATIVE:
        vote.setNegativeVotes(vote.getNegativeVotes() + 1);
        break;
      default:
        throw new IllegalStateException("Should not happen!");
    }

    BlogPostVote savedVote = voteRepository.save(vote);

    return new VoteResponseDto(savedVote.getPositiveVotes() - savedVote.getNegativeVotes());
  }

  @Override
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public VoteResponseDto getVotes(Long postId) {
    Optional<BlogPostVote> blogPostOptional = voteRepository.findByBlogPostId(postId);
    if (!blogPostOptional.isPresent()) {
      throw new NotFoundException(String.format(POST_NOT_FOUND, postId));
    }

    BlogPostVote vote = blogPostOptional.get();

    return new VoteResponseDto(vote.getPositiveVotes() - vote.getNegativeVotes());
  }
}

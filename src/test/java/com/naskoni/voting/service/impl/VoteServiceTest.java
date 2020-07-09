package com.naskoni.voting.service.impl;

import com.naskoni.voting.dto.VoteResponseDto;
import com.naskoni.voting.entity.BlogPostVote;
import com.naskoni.voting.enumeration.VoteType;
import com.naskoni.voting.exception.NotFoundException;
import com.naskoni.voting.repository.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class VoteServiceTest {

  @Mock private VoteRepository voteRepository;

  @InjectMocks private VoteServiceImpl voteService;

  @Test
  void addPositiveVoteForExistentPostShouldSucceed() {
    BlogPostVote vote = new BlogPostVote();
    when(voteRepository.findByBlogPostId(anyLong())).thenReturn(Optional.of(vote));
    when(voteRepository.save(any())).thenReturn(vote);
    VoteResponseDto voteResponseDto = voteService.addVote(1L, VoteType.POSITIVE);

    assertEquals(1, vote.getPositiveVotes());
    assertEquals(0, vote.getNegativeVotes());
    assertEquals(
        vote.getPositiveVotes() - vote.getNegativeVotes(), voteResponseDto.getVotesCount());
    verify(voteRepository, times(1)).findByBlogPostId(anyLong());
    verify(voteRepository, times(1)).save(any());
    verifyNoMoreInteractions(voteRepository);
  }

  @Test
  void addNegativeVoteForExistentPostShouldSucceed() {
    BlogPostVote vote = new BlogPostVote();
    when(voteRepository.findByBlogPostId(anyLong())).thenReturn(Optional.of(vote));
    when(voteRepository.save(any())).thenReturn(vote);
    VoteResponseDto voteResponseDto = voteService.addVote(1L, VoteType.NEGATIVE);

    assertEquals(0, vote.getPositiveVotes());
    assertEquals(1, vote.getNegativeVotes());
    assertEquals(
        vote.getPositiveVotes() - vote.getNegativeVotes(), voteResponseDto.getVotesCount());
    verify(voteRepository, times(1)).findByBlogPostId(anyLong());
    verify(voteRepository, times(1)).save(any());
    verifyNoMoreInteractions(voteRepository);
  }

  @Test
  void addPositiveVoteForNonExistentPostShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> voteService.addVote(1L, VoteType.POSITIVE));
  }

  @Test
  void addNegativeVoteForNonExistentPostShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> voteService.addVote(1L, VoteType.NEGATIVE));
  }

  @Test
  void getVotesForExistentPostShouldSucceed() throws InterruptedException {
    BlogPostVote vote = new BlogPostVote();
    when(voteRepository.findByBlogPostId(anyLong())).thenReturn(Optional.of(vote));
    VoteResponseDto voteResponseDto = voteService.getVotes(1L);

    assertEquals(0, vote.getPositiveVotes());
    assertEquals(0, vote.getNegativeVotes());
    assertEquals(
        vote.getPositiveVotes() - vote.getNegativeVotes(), voteResponseDto.getVotesCount());
  }

  @Test
  void getVotesForNonExistentPostShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> voteService.getVotes(1L));
  }
}

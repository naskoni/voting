package com.naskoni.voting.service;

import com.naskoni.voting.dto.VoteResponseDto;
import com.naskoni.voting.enumeration.VoteType;

public interface VoteService {
  VoteResponseDto addVote(Long postId, VoteType voteType);

  VoteResponseDto getVotes(Long postId);
}

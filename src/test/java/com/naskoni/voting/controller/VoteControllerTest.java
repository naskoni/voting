package com.naskoni.voting.controller;

import com.naskoni.voting.dto.VoteResponseDto;
import com.naskoni.voting.exception.InvalidPathVariableException;
import com.naskoni.voting.exception.NotFoundException;
import com.naskoni.voting.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VoteControllerTest {

  private static final String VOTES_URI_POSITIVE = "/posts/1/votes/positive";
  private static final String VOTES_URI_NEGATIVE = "/posts/1/votes/negative";
  private static final String VOTES_URI_INVALID_PARAM = "/posts/1/votes/invalid";
  private static final String VOTES_URI = "/posts/1/votes";
  private static final String RESPONSE = "{\"votesCount\":1}";

  private MockMvc mockMvc;

  @InjectMocks private VoteController voteController;

  @Mock private VoteService voteService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(voteController)
            .setControllerAdvice(new ErrorHandler())
            .build();
  }

  @Test
  void addPositiveVoteForExistentPostShouldReturnHttpOk() throws Exception {
    when(voteService.addVote(anyLong(), any())).thenReturn(new VoteResponseDto(1L));

    MvcResult mvcResult =
        mockMvc
            .perform(post(VOTES_URI_POSITIVE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertEquals(RESPONSE, content);
    verify(voteService, times(1)).addVote(anyLong(), any());
    verifyNoMoreInteractions(voteService);
  }

  @Test
  void addNegativeVoteForExistentPostShouldReturnHttpOk() throws Exception {
    when(voteService.addVote(anyLong(), any())).thenReturn(new VoteResponseDto(1L));

    MvcResult mvcResult =
        mockMvc
            .perform(post(VOTES_URI_NEGATIVE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertEquals(RESPONSE, content);
    verify(voteService, times(1)).addVote(anyLong(), any());
    verifyNoMoreInteractions(voteService);
  }

  @Test
  void addVoteWithInvalidParamShouldFailHttpBadRequest() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(post(VOTES_URI_INVALID_PARAM).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    mvcResult.getResolvedException().getClass().equals(InvalidPathVariableException.class);
    verify(voteService, times(0)).addVote(anyLong(), any());
    verifyNoMoreInteractions(voteService);
  }

  @Test
  void addPositiveVoteForNonExistentPostShouldFailHttpNotFound() throws Exception {
    doThrow(new NotFoundException("")).when(voteService).addVote(anyLong(), any());

    MvcResult mvcResult =
        mockMvc
            .perform(post(VOTES_URI_POSITIVE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    assertEquals(NotFoundException.class, mvcResult.getResolvedException().getClass());
    verify(voteService, times(1)).addVote(anyLong(), any());
    verifyNoMoreInteractions(voteService);
  }

  @Test
  void addNegativeVoteForNonExistentPostShouldFailHttpNotFound() throws Exception {
    doThrow(new NotFoundException("")).when(voteService).addVote(anyLong(), any());

    MvcResult mvcResult =
        mockMvc
            .perform(post(VOTES_URI_NEGATIVE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    assertEquals(NotFoundException.class, mvcResult.getResolvedException().getClass());
    verify(voteService, times(1)).addVote(anyLong(), any());
    verifyNoMoreInteractions(voteService);
  }

  @Test
  void getVotesForExistentPostShouldReturnHttpOk() throws Exception {
    when(voteService.getVotes(anyLong())).thenReturn(new VoteResponseDto(1L));

    MvcResult mvcResult =
        mockMvc
            .perform(get(VOTES_URI).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertEquals(RESPONSE, content);
    verify(voteService, times(1)).getVotes(anyLong());
    verifyNoMoreInteractions(voteService);
  }

  @Test
  void getVotesForNonExistentPostShouldFailHttpNotFound() throws Exception {
    doThrow(new NotFoundException("")).when(voteService).getVotes(anyLong());

    MvcResult mvcResult =
        mockMvc
            .perform(get(VOTES_URI).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    assertEquals(NotFoundException.class, mvcResult.getResolvedException().getClass());
    verify(voteService, times(1)).getVotes(anyLong());
    verifyNoMoreInteractions(voteService);
  }
}

package com.naskoni.voting.controller;

import com.naskoni.voting.exception.InvalidPathVariableException;
import com.naskoni.voting.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VoteControllerIT {

  private static final int THREADS = 10;
  private static final String VOTES_URI_POSITIVE = "/posts/1/votes/positive";
  private static final String VOTES_URI_POSITIVE_NON_EXISTENT_POST = "/posts/2/votes/positive";
  private static final String VOTES_URI_NEGATIVE = "/posts/1/votes/negative";
  private static final String VOTES_URI_NEGATIVE_NON_EXISTENT_POST = "/posts/2/votes/negative";
  private static final String VOTES_URI_INVALID_PARAM = "/posts/1/votes/invalid";
  private static final String VOTES_URI = "/posts/1/votes";
  private static final String VOTES_URI_NON_EXISTENT_POST = "/posts/2/votes";
  private static final String RESPONSE_POSITIVE = "{\"votesCount\":1}";
  private static final String RESPONSE_NEGATIVE = "{\"votesCount\":-1}";
  private static final String RESPONSE_ZERO = "{\"votesCount\":0}";

  @Autowired private MockMvc mockMvc;

  @Test
  @Order(1)
  void addPositiveVoteForExistentPostShouldReturnHttpOk() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(post(VOTES_URI_POSITIVE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    String result = content.replaceAll("\\s+", "");
    assertEquals(RESPONSE_POSITIVE, result);
  }

  @Test
  @Order(2)
  void addNegativeVoteForExistentPostShouldReturnHttpOk() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(post(VOTES_URI_NEGATIVE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    String result = content.replaceAll("\\s+", "");
    assertEquals(RESPONSE_NEGATIVE, result);
  }

  @Test
  @Order(3)
  void addVoteWithInvalidParamShouldFailHttpBadRequest() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(post(VOTES_URI_INVALID_PARAM).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    mvcResult.getResolvedException().getClass().equals(InvalidPathVariableException.class);
  }

  @Test
  @Order(4)
  void addPositiveVoteForNonExistentPostShouldFailHttpNotFound() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(post(VOTES_URI_POSITIVE_NON_EXISTENT_POST).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    assertEquals(NotFoundException.class, mvcResult.getResolvedException().getClass());
  }

  @Test
  @Order(5)
  void addNegativeVoteForNonExistentPostShouldFailHttpNotFound() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(post(VOTES_URI_NEGATIVE_NON_EXISTENT_POST).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    assertEquals(NotFoundException.class, mvcResult.getResolvedException().getClass());
  }

  @Test
  @Order(6)
  void getVotesForExistentPostShouldReturnHttpOk() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(get(VOTES_URI).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    String result = content.replaceAll("\\s+", "");
    assertEquals(RESPONSE_ZERO, result);
  }

  @Test
  @Order(7)
  void getVotesForNonExistentPostShouldFailHttpNotFound() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(get(VOTES_URI_NON_EXISTENT_POST).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    assertEquals(NotFoundException.class, mvcResult.getResolvedException().getClass());
  }

  @Test
  @Order(8)
  void addPositiveVoteForExistentPostConcurrent() throws Exception {
    ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
    Callable callablePost = createCallablePost();
    for (int i = 0; i < 1; i++) {
      executorService.submit(callablePost);
      executorService.submit(callablePost);
      executorService.submit(callablePost);
      executorService.submit(callablePost);
      executorService.submit(callablePost);
      executorService.submit(callablePost);
      executorService.submit(callablePost);
      executorService.submit(callablePost);
      executorService.submit(callablePost);
      executorService.submit(callablePost);
    }

    Thread.sleep(2000); // waiting for all threads to finish
    executorService.shutdown();
  }

  @Test
  @Order(9)
  void addPositiveVoteAndGetVotesForExistentPostConcurrent() throws Exception {
    ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
    Callable callablePost = createCallablePost();
    Callable callableGet = createCallableGet();
    for (int i = 0; i < 2; i++) {
      executorService.submit(callablePost);
      executorService.submit(callableGet);
      executorService.submit(callablePost);
      executorService.submit(callableGet);
      executorService.submit(callablePost);
      executorService.submit(callableGet);
      executorService.submit(callablePost);
      executorService.submit(callableGet);
      executorService.submit(callablePost);
      executorService.submit(callableGet);
    }

    Thread.sleep(3000); // waiting for all threads to finish
    executorService.shutdown();
  }

  private Callable createCallablePost() {
    return () -> {
      MvcResult mvcResult =
          mockMvc
              .perform(post(VOTES_URI_POSITIVE).accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();
      String content = mvcResult.getResponse().getContentAsString();
      assertNotNull(content);
      String result = content.replaceAll("\\s+", "");
      log.info("POST result: " + result);
      return result;
    };
  }

  private Callable createCallableGet() {
    return () -> {
      MvcResult mvcResult =
          mockMvc
              .perform(get(VOTES_URI).accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();
      String content = mvcResult.getResponse().getContentAsString();
      assertNotNull(content);
      String result = content.replaceAll("\\s+", "");
      log.info("GET result: " + result);
      return result;
    };
  }
}

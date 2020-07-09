package com.naskoni.voting.exception;

public class InvalidPathVariableException extends RuntimeException {

  private static final long serialVersionUID = -2018266662165109040L;

  public InvalidPathVariableException(String message) {
    super(message);
  }
}

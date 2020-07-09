package com.naskoni.voting.exception;

public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = -31120590647356423L;

  public NotFoundException(String message) {
    super(message);
  }
}

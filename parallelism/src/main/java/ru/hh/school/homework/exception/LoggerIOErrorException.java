package ru.hh.school.homework.exception;

import ru.hh.school.homework.Constants;

public class LoggerIOErrorException extends RuntimeException {

  public LoggerIOErrorException(String message) {
    Constants.LOGGER.error("IO Exceptions: {}", message);
  }
}

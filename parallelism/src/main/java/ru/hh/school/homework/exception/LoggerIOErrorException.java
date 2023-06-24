package ru.hh.school.homework.exception;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class LoggerIOErrorException extends RuntimeException {
  public static final Logger LOGGER = getLogger(LoggerIOErrorException.class);

  public LoggerIOErrorException(String message) {
    LOGGER.error("IO Exceptions: {}", message);
  }
}

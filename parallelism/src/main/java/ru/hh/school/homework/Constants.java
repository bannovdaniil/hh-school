package ru.hh.school.homework;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class Constants {
  public static final String ROOT_DIRECTORY = System.getProperty("user.dir");
  public static final String EXTENSION = ".java";
  public static final int NCPU = Runtime.getRuntime().availableProcessors();
  public static final int TOP_WORDS_LIMIT = 10;
  public static final Logger LOGGER = getLogger(Launcher.class);

}

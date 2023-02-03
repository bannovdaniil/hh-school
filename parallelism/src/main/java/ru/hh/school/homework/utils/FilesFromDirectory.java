package ru.hh.school.homework.utils;

import ru.hh.school.homework.exception.LoggerIOErrorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FilesFromDirectory {

  public static List<Path> search(Path searchPath, String extension) {
    List<Path> files;
    try (var fileStream = Files.list(searchPath)) {
      files = fileStream
          .parallel()
          .filter(path -> path.toString().endsWith(extension))
          .toList();
    } catch (IOException err) {
      throw new LoggerIOErrorException(err.getMessage());
    }
    return files;
  }
}

package ru.hh.school.homework.utils;

import ru.hh.school.homework.exception.LoggerIOErrorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DirectoriesFromPath {
  private List<Path> paths;
  private final Path searchPath;

  public DirectoriesFromPath(String searchPath) {
    this.searchPath = Paths.get(searchPath);
  }

  public void search() {
    try (var fileStream = Files.walk(searchPath)) {
      paths = fileStream
          .parallel()
          .filter(path -> path.toFile().isDirectory())
          .toList();
    } catch (IOException err) {
      paths = List.of();
      throw new LoggerIOErrorException(err.getMessage());
    }
  }

  public List<Path> getPaths() {
    return paths;
  }
}

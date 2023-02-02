package ru.hh.school.homework.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GetAllDirectories {
  private List<Path> paths;
  private final Path searchPath;

  public GetAllDirectories(String searchPath) {
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
      throw new RuntimeException(err.getMessage());
    }
  }

  public List<Path> getPaths() {
    return paths;
  }
}
package ru.hh.school.homework.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DirectoriesFromPath {
  private final Path searchPath;

  public DirectoriesFromPath(String searchPath) {
    this.searchPath = Paths.get(searchPath);
  }

  public List<Path> search() throws IOException {
    try (var fileStream = Files.walk(searchPath)) {
      return fileStream
          .parallel()
          .filter(path -> path.toFile().isDirectory())
          .toList();
    } catch (IOException err) {
      throw new IOException(err);
    }
  }

}

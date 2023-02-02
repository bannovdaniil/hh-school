package ru.hh.school.homework.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GetAllDirectories {
  private List<Path> paths;
  private Path searchPath;

  public GetAllDirectories(String searchPath) {
    this.searchPath = Paths.get(searchPath);
  }

  public void search() throws IOException {
    paths = Files.walk(searchPath)
        .parallel()
        .filter(path -> path.toFile().isDirectory())
        .toList();
  }

  public List<Path> getPaths() {
    return paths;
  }
}

package ru.hh.school.homework.utils;

import ru.hh.school.homework.Constants;
import ru.hh.school.homework.exception.LoggerIOErrorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toMap;

public class FrequenciesUtils {
  private Map<String, Long> frequencyOfWords = new ConcurrentHashMap<>();
  private Path directoryPath;
  private List<Path> files = new ArrayList<>();

  public FrequenciesUtils(Path directoryPath) {
    this.directoryPath = directoryPath;
  }

  public void mergeWordsFrequencies(Path file) {
    Map<String, Long> wordsFrequenciesIn = naiveCount(file);
    for (var element : wordsFrequenciesIn.entrySet()) {
      long frequencyIn = element.getValue();
      String word = element.getKey();

      long frequency = frequencyOfWords.getOrDefault(word, 0L);
      frequencyOfWords.put(word, frequency + frequencyIn);
    }
  }

  public Map<String, Long> getTopWords() {
    return frequencyOfWords.entrySet()
        .stream()
        .sorted(comparingByValue(reverseOrder()))
        .limit(Constants.TOP_WORDS_LIMIT)
        .collect(toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map<String, Long> naiveCount(Path file) {
    files.add(file);
    Map<String, Long> resultFrequencyOfWords;
    try {
      resultFrequencyOfWords = Files.lines(file)
          .flatMap(line -> Stream.of(line.split("[^a-zA-Z0-9]")))
          .filter(word -> word.length() > 3)
          .collect(groupingBy(identity(), counting()))
          .entrySet()
          .stream()
          .sorted(comparingByValue(reverseOrder()))
          .limit(10)
          .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    } catch (IOException err) {
      throw new LoggerIOErrorException(err.getMessage());
    }
    return resultFrequencyOfWords;
  }
}

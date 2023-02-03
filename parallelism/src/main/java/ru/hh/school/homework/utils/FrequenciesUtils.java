package ru.hh.school.homework.utils;

import ru.hh.school.homework.Constants;

import java.util.Map;

import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toConcurrentMap;

public class FrequenciesUtils {
  Map<String, Long> frequencyOfWords;

  public FrequenciesUtils(Map<String, Long> frequencyOfWords) {
    this.frequencyOfWords = frequencyOfWords;
  }

  private void mergeWordsFrequencies(Map<String, Long> wordsFrequenciesIn) {
    for (var element : wordsFrequenciesIn.entrySet()) {
      long frequencyIn = element.getValue();
      String word = element.getKey();

      long frequency = frequencyOfWords.getOrDefault(word, 0L);
      frequencyOfWords.put(word, frequency + frequencyIn);
    }
  }

  private Map<String, Long> getTopWords() {
    return frequencyOfWords.entrySet()
        .stream()
        .sorted(comparingByValue(reverseOrder()))
        .limit(Constants.TOP_WORDS_LIMIT)
        .collect(toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}

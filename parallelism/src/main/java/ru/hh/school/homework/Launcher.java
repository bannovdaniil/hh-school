package ru.hh.school.homework;

import ru.hh.school.homework.utils.FrequenciesUtils;
import ru.hh.school.homework.utils.GetAllDirectories;
import ru.hh.school.homework.utils.GetFilesFromDirectory;
import ru.hh.school.homework.utils.GoogleWordSearch;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;

/**
 * Написать код, который, как можно более параллельно:
 * - по заданному пути найдет все "*.java" файлы
 * - для каждого файла вычислит 10 самых популярных слов (см. #naiveCount())
 * - соберет top 10 для каждой папки в которой есть хотя-бы один java файл
 * - для каждого слова сходит в гугл и вернет количество результатов по нему (см. #naiveSearch())
 * - распечатает в консоль результаты в виде:
 * <папка1> - <слово #1> - <кол-во результатов в гугле>
 * <папка1> - <слово #2> - <кол-во результатов в гугле>
 * ...
 * <папка1> - <слово #10> - <кол-во результатов в гугле>
 * <папка2> - <слово #1> - <кол-во результатов в гугле>
 * <папка2> - <слово #2> - <кол-во результатов в гугле>
 * ...
 * <папка2> - <слово #10> - <кол-во результатов в гугле>
 * ...
 * <p>
 * Порядок результатов в консоли не обязательный.
 * При желании naiveSearch и naiveCount можно оптимизировать.
 * <p>
 * test our naive methods:
 */
public class Launcher {
  private static final GoogleWordSearch googleWordSearch = new GoogleWordSearch();

  public static void main(String[] args) {
    GetAllDirectories getAllDirectories = new GetAllDirectories(Constants.ROOT_DIRECTORY);
    getAllDirectories.search();
    var paths = getAllDirectories.getPaths();


    List<CompletableFuture<Void>> cfs =
        paths.stream().map(path ->
            CompletableFuture.supplyAsync(() -> {
                  Constants.LOGGER.info("Path: {}", path);
                  return GetFilesFromDirectory.search(path, Constants.EXTENSION);
                })
                .thenAccept(files ->
                    CompletableFuture.supplyAsync(() -> getWordsFrequenciesList(files))
                        .thenAccept(frequenciesList -> showWordsFrequencies(path, frequenciesList))
                )
        ).toList();

    CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0])).join();
  }

  private static Map<String, Long> getWordsFrequenciesList(List<Path> files) {
    FrequenciesUtils frequenciesUtils = new FrequenciesUtils();
    List<CompletableFuture<Void>> cfs = files.stream().map(file ->
        CompletableFuture.runAsync(() -> frequenciesUtils.mergeWordsFrequencies(file))
    ).toList();

    CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0])).join();

    return frequenciesUtils.getTopWords();
  }

  private static void showWordsFrequencies(Path path, Map<String, Long> frequenciesList) {
    frequenciesList.entrySet().stream()
        .parallel()
        .sorted(comparingByValue(reverseOrder()))
        .forEachOrdered(element -> {
          long fq = googleWordSearch.naiveSearch(element.getKey());
          System.out.printf("%s - %s - %d%n", path, element.getKey(), fq);
        });
  }

}

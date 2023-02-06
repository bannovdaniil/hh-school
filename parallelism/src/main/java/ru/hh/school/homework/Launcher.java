package ru.hh.school.homework;

import org.slf4j.Logger;
import ru.hh.school.homework.utils.DirectoriesFromPath;
import ru.hh.school.homework.utils.FilesFromDirectory;
import ru.hh.school.homework.utils.FrequenciesUtils;
import ru.hh.school.homework.utils.GoogleWordSearch;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static org.slf4j.LoggerFactory.getLogger;

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
  public static final Logger LOGGER = getLogger(Launcher.class);
  private static final GoogleWordSearch googleWordSearch = new GoogleWordSearch();

  public static void main(String[] args) throws IOException {
    DirectoriesFromPath directoriesFromPath = new DirectoriesFromPath(Constants.ROOT_DIRECTORY);
    var paths = directoriesFromPath.search();

    try (ExecutorService executorService = Executors.newFixedThreadPool(Constants.MAX_SEARCH_THREAD)) {

      List<CompletableFuture<Void>> cfs =
          paths.stream().map(path ->
              CompletableFuture.supplyAsync(() -> FilesFromDirectory.search(path, Constants.EXTENSION))
                  .thenAccept(fileList -> {
                    if (fileList.size() > 0) {
                      LOGGER.info("Path: {}, files: {}", path, fileList.size());
                      CompletableFuture.supplyAsync(() -> getWordsFrequenciesList(fileList))
                          .thenAcceptAsync(frequenciesList -> showWordsFrequencies(path, frequenciesList), executorService);
                    }
                  })
          ).toList();

      CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0])).join();
    } catch (IllegalArgumentException err) {
      LOGGER.error("Error create ExecutorService, count of thread: {}", Constants.MAX_SEARCH_THREAD);
      throw new IllegalArgumentException(err);
    }

  }

  private static Map<String, Long> getWordsFrequenciesList(List<Path> files) {
    FrequenciesUtils frequenciesUtils = new FrequenciesUtils();
    files.stream().parallel().forEach(file -> frequenciesUtils.mergeWordsFrequencies(file));

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

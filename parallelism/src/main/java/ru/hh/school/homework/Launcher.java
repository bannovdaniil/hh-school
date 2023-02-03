package ru.hh.school.homework;

import ru.hh.school.homework.utils.FrequenciesUtils;
import ru.hh.school.homework.utils.GetAllDirectories;
import ru.hh.school.homework.utils.GetFilesFromDirectory;
import ru.hh.school.homework.utils.GoogleWordSearch;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;

public class Launcher {

  public static void main(String[] args) throws IOException {
    GoogleWordSearch googleWordSearch = new GoogleWordSearch();

    GetAllDirectories getAllDirectories = new GetAllDirectories(Constants.ROOT_DIRECTORY);
    getAllDirectories.search();
    var paths = getAllDirectories.getPaths();

    ExecutorService executorService = Executors.newFixedThreadPool(Constants.NCPU);

    paths.forEach(path -> {
          var files = GetFilesFromDirectory.search(path, Constants.EXTENSION);
          Constants.LOGGER.info("Path: {}", path);

          Map<String, Long> frequencyOfWordsToPath = new ConcurrentHashMap<>();
          FrequenciesUtils frequenciesUtils = new FrequenciesUtils(path);
          files.forEach((file) -> {
            frequenciesUtils.mergeWordsFrequencies(file);
          });

          frequenciesUtils.getTopWords().entrySet().stream()
              .sorted(comparingByValue(reverseOrder()))
              .forEach((element) -> {
                long fq = googleWordSearch.naiveSearch(element.getKey());
                System.out.printf("%s - %s - %d%n", path, element.getKey(), element.getValue());
              });
        }
    );

    // Написать код, который, как можно более параллельно:
    // - по заданному пути найдет все "*.java" файлы
    // - для каждого файла вычислит 10 самых популярных слов (см. #naiveCount())
    // - соберет top 10 для каждой папки в которой есть хотя-бы один java файл
    // - для каждого слова сходит в гугл и вернет количество результатов по нему (см. #naiveSearch())
    // - распечатает в консоль результаты в виде:
    // <папка1> - <слово #1> - <кол-во результатов в гугле>
    // <папка1> - <слово #2> - <кол-во результатов в гугле>
    // ...
    // <папка1> - <слово #10> - <кол-во результатов в гугле>
    // <папка2> - <слово #1> - <кол-во результатов в гугле>
    // <папка2> - <слово #2> - <кол-во результатов в гугле>
    // ...
    // <папка2> - <слово #10> - <кол-во результатов в гугле>
    // ...
    //
    // Порядок результатов в консоли не обязательный.
    // При желании naiveSearch и naiveCount можно оптимизировать.

    // test our naive methods:
  }

}

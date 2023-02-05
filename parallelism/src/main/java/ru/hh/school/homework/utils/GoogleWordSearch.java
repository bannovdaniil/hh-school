package ru.hh.school.homework.utils;

import com.google.common.util.concurrent.Uninterruptibles;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.hh.school.homework.Constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GoogleWordSearch {
  private final String GOOGLE_URL = "https://www.google.com/search?q=";
  private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36";
  private static Map<String, Long> cacheQuery = new ConcurrentHashMap<>();

  public long naiveSearch(String query) {
    if (cacheQuery.containsKey(query)) {
      Constants.LOGGER.info("get cache value: {}", query);
      return cacheQuery.get(query);
    }

    try {
      Document document = Jsoup.connect(GOOGLE_URL + query).userAgent(USER_AGENT).get();

      long timeout = ThreadLocalRandom.current().nextLong(Constants.MIN_STEEL_TIME, Constants.MAX_STEEL_TIME);
      Constants.LOGGER.info("Wait, google... Sleep: {}", timeout);
      Uninterruptibles.sleepUninterruptibly(timeout, TimeUnit.MILLISECONDS);

      Element divResultStats = document.select("div#slim_appbar").first();
      long queryFrequencies = -1;
      if (divResultStats != null) {
        String text = divResultStats.text();
        String resultsPart = text.substring(0, text.indexOf('('));
        queryFrequencies = Long.parseLong(resultsPart.replaceAll("[^0-9]", ""));
        cacheQuery.put(query, queryFrequencies);
      }
      return queryFrequencies;
    } catch (Exception err) {
      Constants.LOGGER.error("Google search exception: {}", err.toString());
    }
    return -1L;
  }
}

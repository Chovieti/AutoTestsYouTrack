package org.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class RetryUtils {
  private static final Logger log = LoggerFactory.getLogger(RetryUtils.class);

  public static <T> T retry(int maxAttempts, long pauseMillis, Supplier<T> action) {
    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
      try {
        return action.get();
      } catch (Throwable e) {
        if (attempt == maxAttempts) {
          throw new RuntimeException("Действие не удалось после " + maxAttempts + " попыток", e);
        }
        log.warn("Попытка {} из {} завершилась ошибкой: {}", attempt, maxAttempts, e.getMessage());
        if (pauseMillis > 0) {
          try {
            Thread.sleep(pauseMillis);
          } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
          }
        }
      }
    }
    return null;
  }

  public static void retryVoid(int maxAttempts, long pauseMillis, Runnable action) {
    retry(maxAttempts, pauseMillis, () -> {
      action.run();
      return null;
    });
  }
}

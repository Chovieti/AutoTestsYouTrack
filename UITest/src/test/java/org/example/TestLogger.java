package org.example;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogger implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
  private static final Logger log = LoggerFactory.getLogger(TestLogger.class);

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    log.info("Запуск теста: {}", context.getDisplayName());
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    if (context.getExecutionException().isPresent()) {
      log.error("Тест упал: {}", context.getDisplayName(), context.getExecutionException().get());
    } else {
      log.info("Тест успешно выполнен: {}", context.getDisplayName());
    }
  }
}
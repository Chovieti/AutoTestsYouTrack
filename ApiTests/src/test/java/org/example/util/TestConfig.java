package org.example.util;

import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
  private static final Properties properties = new Properties();

  static {
    try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
      if (input != null) {
        properties.load(input);
      } else {
        throw new RuntimeException("Файл application.properties не найден в src/test/resources");
      }
    } catch (Exception e) {
      throw new RuntimeException("Ошибка загрузки тестовой конфигурации", e);
    }
  }

  public static String getBaseUrl() {
    return properties.getProperty("base.url");
  }

  public static String getBasePath() {
    return properties.getProperty("base.path");
  }

  public static String getDefaultToken() {
    return properties.getProperty("default.token");
  }

  public static String getDefaultProjectId() {
    return properties.getProperty("default.project.id");
  }
}

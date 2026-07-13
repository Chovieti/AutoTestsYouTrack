package org.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
  private static final Properties properties = new Properties();

  static {
    try (InputStream input = TestConfig.class.getClassLoader()
        .getResourceAsStream("config.properties")) {
      if (input == null) {
        throw new RuntimeException("config.properties not found in classpath");
      }
      properties.load(input);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load config.properties", e);
    }
  }

  public static String getBaseUrl() {
    return properties.getProperty("app.baseUrl");
  }

  public static String getUsername() {
    return properties.getProperty("app.username");
  }

  public static String getPassword() {
    return properties.getProperty("app.password");
  }

  public static long getTimeout() {
    return Long.parseLong(properties.getProperty("selenide.timeout"));
  }
}

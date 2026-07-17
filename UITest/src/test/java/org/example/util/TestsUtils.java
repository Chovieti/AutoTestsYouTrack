package org.example.util;

public class TestsUtils {
  public static String uniqueTitle(String base) {
    return base + " - " + System.currentTimeMillis();
  }
}
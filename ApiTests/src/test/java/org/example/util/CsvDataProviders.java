package org.example.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CsvDataProviders {
  @DataProvider(name = "createIssueDataCsv", parallel = true)
  public static Iterator<Object[]> getCreateIssueData() throws IOException, CsvValidationException {
    String path = "csv-data/create-issue-data.csv";
    int numberOfArguments = 2;
    return extractData(path, numberOfArguments).iterator();
  }

  @DataProvider(name = "editIssueDataCsv", parallel = true)
  public static Iterator<Object[]> getEditIssueData() throws IOException, CsvValidationException {
    String path = "csv-data/edit-issue-data.csv";
    int numberOfArguments = 4;
    return extractData(path, numberOfArguments).iterator();
  }

  @DataProvider(name = "stageIssueDataCsv", parallel = true)
  public static Iterator<Object[]> getStageData() throws IOException, CsvValidationException {
    String path = "csv-data/issue-stage-data.csv";
    int numberOfArguments = 1;
    return extractData(path, numberOfArguments).iterator();
  }

  private static List<Object[]> extractData(String path, int k) throws IOException, CsvValidationException {
    List<Object[]> testData = new ArrayList<>();
    InputStream inputStream = getInputStream(path);
    try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {
      csvReader.readNext();

      String[] row;
      while ((row = csvReader.readNext()) != null) {
        Object[] newObj = new Object[k];
        for (int i = 0; i < k; i++) {
          newObj[i] = row[i];
        }
        testData.add(newObj);
      }
    }
    return testData;
  }

  private static InputStream getInputStream(String path) {
    InputStream inputStream = CsvDataProviders.class
        .getClassLoader()
        .getResourceAsStream(path);

    if (inputStream == null) {
      throw new IllegalArgumentException("Файл не найден по пути: " + path);
    }
    return inputStream;
  }
}

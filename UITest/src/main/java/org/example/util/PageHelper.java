package org.example.util;

import org.example.pages.MainPage;
import org.example.pages.issues.IssuesListPage;

public class PageHelper {
  public static IssuesListPage openIssuesPage() {
    return new MainPage().goToIssues().waitForPageLoaded();
  }

  public static IssuesListPage openIssuesPage(String baseUrl) {
    return new MainPage().openPage(baseUrl).goToIssues().waitForPageLoaded();
  }
}

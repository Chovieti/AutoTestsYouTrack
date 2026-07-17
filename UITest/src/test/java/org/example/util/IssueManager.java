package org.example.util;

import com.codeborne.selenide.Selenide;
import org.example.components.CreateIssueDialog;
import org.example.pages.issues.IssueDetailsPage;
import org.example.pages.issues.IssuesListPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IssueManager {
  private static final Logger log = LoggerFactory.getLogger(IssueManager.class);

  public static String createIssue(String title, String description) {
    return RetryUtils.retry(3, 1000, () -> {
      Selenide.refresh();
      IssuesListPage issuesPage = PageHelper.openIssuesPage();
      CreateIssueDialog createIssue = issuesPage.openCreateIssueDialog();
      createIssue.setTitle(title);
      if (description != null && !description.isEmpty()) {
        createIssue.setDescription(description);
      }
      return createIssue.create();
    });
  }

  public static void deleteIssue(String id, String baseUrl) {
    IssuesListPage issuesPage = PageHelper.openIssuesPage(baseUrl);
    if (!issuesPage.checkIssueByIdExists(id)) {
      log.warn("Задача с ID {} уже удалена или отсутствует. Пропускаем", id);
      return;
    }
    RetryUtils.retryVoid(3, 1000, () -> {
      IssuesListPage page = PageHelper.openIssuesPage(baseUrl);
      page.openById(id);
      IssueDetailsPage issueDetails = new IssueDetailsPage().waitForPageLoaded();
      issueDetails.deleteIssue();
    });
  }
}

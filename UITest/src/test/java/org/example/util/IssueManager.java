package org.example.util;

import com.codeborne.selenide.Selenide;
import org.example.components.CreateIssueDialog;
import org.example.pages.LoginPage;
import org.example.pages.MainPage;
import org.example.pages.issues.IssueDetailsPage;
import org.example.pages.issues.IssuesListPage;
import org.openqa.selenium.NoSuchElementException;

public class IssueManager {
  public static String createIssue(String title, String description) {
    int maxRetries = 3;
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        IssuesListPage issuesPage = new MainPage().goToIssues().waitForPageLoaded();
        CreateIssueDialog createIssue = issuesPage.openCreateIssueDialog();
        createIssue.setTitle(title);
        if (description != null && !description.isEmpty()) {
          createIssue.setDescription(description);
        }
        return createIssue.create();
      } catch (AssertionError | NoSuchElementException e) {
        System.out.printf("[Попытка создания %d из %d] Страница ещё не обновилась. Повторяем попытку...%n", attempt, maxRetries);

        Selenide.refresh();
        if (attempt < maxRetries) {
          Selenide.sleep(1000);
        }
      }
    }
    throw new RuntimeException("Ошибка создания задачи после " + maxRetries + " попыток");
  }

  public static void deleteIssue(String id, String baseUrl) {
    int maxRetries = 3;
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        IssuesListPage issuesPage = new MainPage()
            .openPage(baseUrl)
            .goToIssues()
            .waitForPageLoaded();

        if (!issuesPage.checkIssueByIdExists(id)) {
          System.out.printf("[Удаление] Задача с ID %s уже удалена или отсутствует. Пропускаем.%n", id);
          return;
        }

        IssueDetailsPage issueDetails = new IssueDetailsPage().waitForPageLoaded();
        issueDetails.deleteIssue();
        return;
      } catch (Throwable e) {
        System.out.printf("[Попытка удаления %d из %d] Ошибка: %s. Повторяем попытку...%n"
            , attempt, maxRetries, e.getMessage());
        if (attempt < maxRetries) {
          Selenide.sleep(1000);
        }
      }
    }
  }
}

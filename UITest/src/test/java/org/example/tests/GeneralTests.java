package org.example.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.example.pages.LoginPage;
import org.example.pages.MainPage;
import org.example.pages.issues.IssueDetailsPage;
import org.example.pages.issues.IssuesListPage;
import org.example.util.IssueManager;
import org.example.util.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GeneralTests {
  private static final String BASE_URL = TestConfig.getBaseUrl();
  private static final String USERNAME = TestConfig.getUsername();
  private static final String PASSWORD = TestConfig.getPassword();

  @BeforeEach
  public void setUp() {
    Configuration.browser = "chrome";
    Configuration.headless = false;
    Configuration.timeout = TestConfig.getTimeout();
    Configuration.screenshots = true;
    Configuration.savePageSource = true;
//    System.out.println(Thread.currentThread().getName());
//    System.out.println(System.getProperty("junit.jupiter.execution.parallel.enabled"));
//    System.getProperty("junit.jupiter.execution.parallel.config.fixed.parallelism");
//    System.out.println("Parallelism limit: " + System.getProperty("junit.jupiter.execution.parallel.config.fixed.parallelism"));
  }

  @AfterEach
  public void tearDown() {
    Selenide.closeWebDriver();
  }

  @Test
  @DisplayName("Проверка корректного логина")
  public void testSuccessfulLogin() {
    MainPage mainPage = new LoginPage()
        .openPage(BASE_URL)
        .waitForPageLoaded()
        .login(USERNAME, PASSWORD);
    mainPage.waitForPageLoaded();
  }

  @Test
  @DisplayName("Проверка создания задачи")
  public void testCreateIssue() {
    String title = "Тестовое создание задачи";
    String id = IssueManager.createIssue(title, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(title).shouldBe(Condition.visible);

    IssueManager.deleteIssue(id, BASE_URL);
  }
  @Test
  @DisplayName("Проверка удаления задачи")
  public void testDeleteIssue() {
    String title = "Тестовая задача для удаления";
    String id = IssueManager.createIssue(title, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(title).shouldBe(Condition.visible);
    IssueManager.deleteIssue(id, BASE_URL);
    issuesPage.searchByTitle(title);
    issuesPage.getIssueRowByTitle(title).shouldBe(Condition.not(Condition.visible));
  }

  @Test
  @DisplayName("Проверка изменения имени/описания задачи")
  public void testEditIssue() {
    String title = "Тестовая задача для изменения";
    String id = IssueManager.createIssue(title, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(title).shouldBe(Condition.visible);
    IssueDetailsPage issueDetails = issuesPage.openById(id);
    String newTitle = "Изменненное название";
    String newDescription = "Какое-то описание";
    issueDetails.editIssue(newTitle, newDescription);
    IssueManager.deleteIssue(id, BASE_URL);
  }

  @Test
  @DisplayName("Проверка оставления комментария к задаче")
  public void testComments() {
    String title = "Тестовая задача для комментариев";
    String id = IssueManager.createIssue(title, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(title).shouldBe(Condition.visible);
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id);
    String comment = "Some comment";
    issueDetailsPage.addComment(comment).findComment(comment);

    IssueManager.deleteIssue(id, BASE_URL);
  }

  @Test
  @DisplayName("Проверка взятия задачи в работу")
  public void testIssueTake() {
    String title = "Тестовая задача для взятия";
    String id = IssueManager.createIssue(title, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(title).shouldBe(Condition.visible);
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id);
    issueDetailsPage.choiceExecutor(USERNAME);

    IssueManager.deleteIssue(id, BASE_URL);
  }

  @Test
  @DisplayName("Проверка изменения статуса задачи")
  public void testIssueStageChange() {
    String title = "Тестовая задача для изменения";
    String id = IssueManager.createIssue(title, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(title).shouldBe(Condition.visible);
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id);
    // TODO Заменить текст стадии на параметр передаваемый внешне
    issueDetailsPage.choiceStage("Тест");

    IssueManager.deleteIssue(id, BASE_URL);
  }
}

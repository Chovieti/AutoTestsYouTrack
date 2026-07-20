package org.example.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import org.example.TestLogger;
import org.example.pages.LoginPage;
import org.example.pages.MainPage;
import org.example.pages.issues.IssueDetailsPage;
import org.example.pages.issues.IssuesListPage;
import org.example.util.IssueManager;
import org.example.util.TestConfig;
import org.example.util.TestsUtils;
import org.example.util.PageHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({TestLogger.class, ScreenShooterExtension.class})
public class GeneralTests {
  private static final Logger log = LoggerFactory.getLogger(GeneralTests.class);
  private static final String BASE_URL = TestConfig.getBaseUrl();
  private static final String USERNAME = TestConfig.getUsername();
  private static final String PASSWORD = TestConfig.getPassword();

  private final List<String> createdIds = Collections.synchronizedList(new ArrayList<>());

  @BeforeAll
  public static void globalSetUp() {
    log.info("Глобальная конфигурация Selenide для всех потоков");
  }

  @BeforeEach
  public void setUp() {
    log.info("Настройка теста: Инициализация браузера и авторизация перед тестом");
    Configuration.browser = "chrome";
    Configuration.headless = false;
    Configuration.timeout = TestConfig.getTimeout();
    Configuration.screenshots = true;
    Configuration.savePageSource = false;
    Configuration.reportsFolder = "target/screenshots";
    new LoginPage()
        .openPage(BASE_URL)
        .waitForPageLoaded()
        .login(USERNAME, PASSWORD)
        .waitForPageLoaded();
    log.info("Логин выполнен успешно");
  }

  @AfterEach
  public void tearDown() {
    log.info("Очистка после теста: удаление созданных задач");
    try {
      while (!createdIds.isEmpty()) {
        String id = createdIds.getLast();
        try {
          IssueManager.deleteIssue(id, BASE_URL);
          log.debug("Задача {} удалена", id);
          createdIds.remove(id);
        } catch (AssertionError e) {
          log.error("Не удалось удалить задачу {}: {}", id, e.getMessage());
        }
      }
    } finally {
      Selenide.closeWebDriver();
    }
  }

  @Test
  @DisplayName("Проверка корректного логина")
  public void testSuccessfulLogin() {
    MainPage mainPage = new MainPage().waitForPageLoaded();
    assertThat(mainPage.loggedIn())
        .as("После входа не должна быть доступна кнопка перехода на окно авторизации")
        .isTrue();
  }

  @ParameterizedTest(name = "Проверка создания задачи с заголовком {0}")
  @DisplayName("Проверка создания задачи")
  @CsvFileSource(resources = "/csv-data/create-issue-data.csv", numLinesToSkip = 0)
  public void testCreateIssue(String title, String description) {
    String realTitle = TestsUtils.uniqueTitle(title);

    log.info("Создание задачи '{}'", realTitle);
    String id = IssueManager.createIssue(realTitle, description);
    createdIds.add(id);
    log.info("Задача создана с ID: {}", id);

    IssuesListPage issuesPage = PageHelper.openIssuesPage();

    assertThat(issuesPage.checkIssueByTitleExists(realTitle))
        .as("Задача создалась и отображается в списке")
        .isTrue();
  }

  @Test
  @DisplayName("Проверка удаления задачи")
  public void testDeleteIssue() {
    String title = "Тестовая задача для удаления";
    log.info("Создание задачи для удаления: '{}'", title);
    String id = IssueManager.createIssue(title, null);
    createdIds.add(id);

    IssuesListPage issuesPage = PageHelper.openIssuesPage();
    issuesPage.checkIssueByTitleExists(title);
    log.info("Удалкние задачи ID: {}", id);
    IssueManager.deleteIssue(id, BASE_URL);
    createdIds.remove(id);

    assertThat(issuesPage.checkIssueByTitleNotExists(title))
        .as("Задача удалена и не отображается")
        .isTrue();
  }

  @ParameterizedTest(name = "Проверка изменения имени задачи {0} -> {1} и изменения описания")
  @DisplayName("Проверка изменения имени/описания задачи")
  @CsvFileSource(resources = "/csv-data/edit-issue-data.csv", numLinesToSkip = 0)
  public void testEditIssue(String oldTitle, String newTitle, String newDescription) {
    String realTitle = TestsUtils.uniqueTitle(oldTitle);
    log.info("Создание задачи '{}' для редактирования", realTitle);
    String id = IssueManager.createIssue(realTitle, null);
    createdIds.add(id);

    IssuesListPage issuesPage = PageHelper.openIssuesPage();
    issuesPage.checkIssueByTitleExists(realTitle);

    IssueDetailsPage issueDetails = issuesPage.openById(id).waitForPageLoaded();
    log.info("Редактирование задачи: новый заголовок '{}', новое описание '{}'", newTitle, newDescription);
    issueDetails.editIssue(newTitle, newDescription);

    assertThat(issueDetails.getTitleText())
        .as("Заголовок должен измениться на '%s'", newTitle)
        .isEqualTo(newTitle);
    assertThat(issueDetails.getDescriptionText())
        .as("Описание должно измениться на '%s'", newDescription)
        .isEqualTo(newDescription);
  }

  @Test
  @DisplayName("Проверка оставления комментария к задаче")
  public void testComments() {
    String title = "Тестовая задача для комментариев";
    log.info("Создание задачи '{}' для комментариев", title);
    String id = IssueManager.createIssue(title, null);
    createdIds.add(id);

    IssuesListPage issuesPage = PageHelper.openIssuesPage();
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id);
    String comment = "Some comment";
    log.info("Добавление комментария: '{}'", comment);
    issueDetailsPage.addComment(comment);

    assertThat(issueDetailsPage.waitForPageLoaded().checkCommentExists(comment))
        .as("Комментацрий '%s' должен отображаться на странице задачи", comment)
        .isTrue();
  }

  @Test
  @DisplayName("Проверка взятия задачи в работу")
  public void testIssueTake() {
    String title = "Тестовая задача для взятия";
    log.info("Создание задачи '{}' для назначения исполнителя", title);
    String id = IssueManager.createIssue(title, null);
    createdIds.add(id);

    IssuesListPage issuesPage = PageHelper.openIssuesPage();
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id);
    log.info("Назначение исполнителя: {}", USERNAME);
    issueDetailsPage.choiceExecutor(USERNAME);

    assertThat(issueDetailsPage.getExecutorText().contains(USERNAME))
        .as("Исполнителем должен быть '%s'", USERNAME)
        .isTrue();
  }

  @ParameterizedTest(name = "Проверка изменения статус задачи на {0}")
  @DisplayName("Проверка изменения статуса задачи")
  @CsvFileSource(resources = "/csv-data/issue-stage-data.csv", numLinesToSkip = 0)
  public void testIssueStageChange(String stage) {
    String title = "Тестовая задача для изменения";
    String realTitle = TestsUtils.uniqueTitle(title);
    log.info("Создание задачи '{}' для смены статуса", realTitle);
    String id = IssueManager.createIssue(realTitle, null);
    createdIds.add(id);

    IssuesListPage issuesPage = PageHelper.openIssuesPage();
    issuesPage.checkIssueByTitleExists(realTitle);
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id);
    log.info("Изменение статуса на: {}", stage);
    issueDetailsPage.choiceStage(stage);

    assertThat(issueDetailsPage.getStageText())
        .as("Статус должен измениться на '%s'", stage)
        .isEqualTo(stage);
  }
}

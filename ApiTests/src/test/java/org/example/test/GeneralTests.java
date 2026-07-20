package org.example.test;

import io.restassured.response.Response;
import org.example.api.IssueApiClient;
import org.example.dto.CustomFieldDTO;
import org.example.dto.IssueDTO;
import org.example.dto.ProjectDTO;
import org.example.util.CsvDataProviders;
import org.example.util.TestConfig;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneralTests {
  private static final ThreadLocal<List<String>> issueIdForCleanup = new ThreadLocal<>();
  private IssueApiClient issueApiClient;

  @BeforeClass
  public void setUp() {
    issueApiClient = new IssueApiClient(
        TestConfig.getBaseUrl(),
        TestConfig.getBasePath(),
        TestConfig.getDefaultToken()
    );
  }

  @BeforeMethod(onlyForGroups = {"create-issue"}, alwaysRun = true)
  public void setUpMethod() {
//    System.out.println("Тест выполнился в потоке: " + Thread.currentThread().getName());
    issueIdForCleanup.set(new ArrayList<>());
  }

  @AfterMethod(onlyForGroups = {"create-issue"}, alwaysRun = true)
  public void deleteIssue() {
    List<String> issueIds = issueIdForCleanup.get();
    while (!issueIds.isEmpty()) {
      issueApiClient.deleteIssue(issueIds.removeLast());
    }
    issueIdForCleanup.remove();
  }

  @Test(
      description = "Успешное создание задачи",
      groups = "create-issue",
      dataProvider = "createIssueDataCsv",
      dataProviderClass = CsvDataProviders.class
  )
  public void successTestCreateIssue(String title, String description) {
    IssueDTO newIssue = IssueDTO.builder()
        .summary(title)
        .description(description)
        .project(new ProjectDTO(TestConfig.getDefaultProjectId()))
        .build();
    Response response = issueApiClient.createIssue(newIssue);

    Assert.assertEquals(response.getStatusCode(), 200, "Неверный код ответа");
    issueIdForCleanup.get().add(response.jsonPath().getString("id"));
    Assert.assertNotNull(response.jsonPath().getString("id"), "ID задачи не должен быть null");
    Assert.assertEquals(response.jsonPath().getString("$type"), "Issue", "Тип должен быть Issue");
  }

  @Test(description = "Ошибка 403 при создании задачи без авторизации")
  public void testCreateIssueWithoutToken() {
    String titleIssue = "Создание задачи без токена авторизации";
    IssueDTO newIssue = IssueDTO.builder()
        .summary(titleIssue)
        .project(new ProjectDTO(TestConfig.getDefaultProjectId()))
        .build();
    Response response = issueApiClient.createIssue(newIssue, null);

    if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
      System.out.println("Hello");
      issueIdForCleanup.set(new ArrayList<>());
      issueIdForCleanup.get().add(response.jsonPath().getString("id"));
      deleteIssue();
    }

    Assert.assertEquals(response.getStatusCode(), 403, "Ожидался статус 403 Forbidden");
  }

  @Test(description = "Ошибка 400 при создании задачи без проекта")
  public void testCreateIssueWithoutProject() {
    String titleIssue = "Создание задачи без проекта";
    IssueDTO newIssue = IssueDTO.builder()
        .summary(titleIssue)
        .build();
    Response response = issueApiClient.createIssue(newIssue);

    if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
      issueIdForCleanup.set(new ArrayList<>());
      issueIdForCleanup.get().add(response.jsonPath().getString("id"));
      deleteIssue();
    }

    Assert.assertEquals(response.getStatusCode(), 400, "Ожидался статус 400");
  }

  @Test(description = "Получение информации по задаче с несуществующим ID")
  public void testGetIssueWithWrongId() {
    String nonexistentId = "1234567890-0987654321";
    Response response = issueApiClient.getIssue(nonexistentId);

    Assert.assertEquals(response.getStatusCode(), 404, "Ожидался статус 404 Not Found");
  }

  @Test(
      description = "Обновление полей заголовка и описания задачи",
      groups = "create-issue",
      dataProvider = "editIssueDataCsv",
      dataProviderClass = CsvDataProviders.class
  )
  public void successTestUpdateIssueTitleAndDescription(
      String title, String description,
      String updatedTitle, String updatedDescription
  ) {
    IssueDTO newIssue = IssueDTO.builder()
        .summary(title)
        .description(description)
        .project(new ProjectDTO(TestConfig.getDefaultProjectId()))
        .build();
    Response response = issueApiClient.createIssue(newIssue);
    String id = response.jsonPath().getString("id");
    issueIdForCleanup.get().add(response.jsonPath().getString("id"));

    IssueDTO updateData = IssueDTO.builder()
        .summary(updatedTitle)
        .description(updatedDescription)
        .project(new ProjectDTO(TestConfig.getDefaultProjectId()))
        .build();

    response = issueApiClient.updateIssues(id, updateData);

    Assert.assertEquals(response.getStatusCode(), 200, "Код ответа должен быть 200");
    Assert.assertEquals(response.jsonPath().getString("summary"), updatedTitle);
    Assert.assertEquals(response.jsonPath().getString("description"), updatedDescription);
  }

  @Test(description = "Взятие задачи в работу", groups = "create-issue")
  public void successTestUpdateIssueExecutor() {
    IssueDTO newIssue = IssueDTO.builder()
        .summary("Задача для смены исполнителя")
        .project(new ProjectDTO(TestConfig.getDefaultProjectId()))
        .build();
    Response response = issueApiClient.createIssue(newIssue);
    String issueId = response.jsonPath().getString("id");
    issueIdForCleanup.get().add(response.jsonPath().getString("id"));
    String executor = "chovieti";
    IssueDTO updateData = IssueDTO.builder()
        .customFields(List.of(CustomFieldDTO.createAssigneeField(executor)))
        .build();

    response = issueApiClient.updateIssues(issueId, updateData);

    Assert.assertEquals(response.getStatusCode(), 200, "Код ответа должен быть 200");
    String actualExecutor = response.jsonPath().getString(
        "customFields.find { it.name == 'Assignee' } .value.login"
    );
    Assert.assertEquals(actualExecutor, executor, "Исполнитель должен обновиться");
  }

  @Test(
      description = "Проверка изменения статуса задачи",
      groups = "create-issue",
      dataProvider = "stageIssueDataCsv",
      dataProviderClass = CsvDataProviders.class
  )
  public void successTestChangeIssueStage(String stage) {
    IssueDTO newIssue = IssueDTO.builder()
        .summary("Задача для смены статуса")
        .project(new ProjectDTO(TestConfig.getDefaultProjectId()))
        .build();
    Response response = issueApiClient.createIssue(newIssue);
    String id = response.jsonPath().getString("id");
    issueIdForCleanup.get().add(response.jsonPath().getString("id"));

    IssueDTO updateData = IssueDTO.builder()
        .customFields(List.of(CustomFieldDTO.createStateField(stage)))
        .build();
    response = issueApiClient.updateIssues(id, updateData);

    Assert.assertEquals(response.getStatusCode(), 200, "Код ответа должен быть 200");
    String actualState = response.jsonPath().getString(
        "customFields.find { it.name == 'Stage' }.value.name"
    );
    Assert.assertEquals(actualState, stage, "Статус задачи должен измениться");
  }

  @Test(description = "Получение последних 5-ти выполненных задач", groups = "create-issue")
  public void successTestGetLastFiveEndedIssues() {
    String state = "Done";
    issueIdForCleanup.set(new ArrayList<>());
    for (int i = 1; i <= 5; i++) {
      IssueDTO issue = IssueDTO.builder()
          .summary("Завершенная задача №" + i)
          .project(new ProjectDTO(TestConfig.getDefaultProjectId()))
          .customFields(List.of(CustomFieldDTO.createStateField(state)))
          .build();
      Response response = issueApiClient.createIssue(issue);
      Assert.assertEquals(response.getStatusCode(), 200);
      String id = response.jsonPath().getString("id");
      issueIdForCleanup.get().add(response.jsonPath().getString("id"));
    }
    Response response = issueApiClient.getIssues(Map.of(
        "query", "State: { " + state + "}",
        "$top", 5,
        "fields", "id,summary"
    ));

    Assert.assertEquals(response.getStatusCode(), 200, "Код ответа должен быть 200");
    List<Object> issuesList = response.jsonPath().getList("$");
    Assert.assertTrue(issuesList.size() >= 5, "Должно быть найдено как минимум 5 выполненных задач");
  }
}

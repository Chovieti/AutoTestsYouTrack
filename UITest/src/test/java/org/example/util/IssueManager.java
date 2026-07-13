package org.example.util;

import org.example.components.CreateIssueDialog;
import org.example.pages.LoginPage;
import org.example.pages.MainPage;
import org.example.pages.issues.IssueDetailsPage;
import org.example.pages.issues.IssuesListPage;

public class IssueManager {
  public static String createIssue(String title, String baseUrl, String username, String password) {
    LoginPage loginPage = new LoginPage();
    loginPage.openPage(baseUrl);
    loginPage.waitForPageLoaded();
    MainPage mainPage = loginPage.login(username, password).waitForPageLoaded();
    IssuesListPage issuesPage = mainPage.goToIssues().waitForPageLoaded();
    CreateIssueDialog createIssue = issuesPage.openCreateIssueDialog();
    createIssue.setTitle(title);
    String id = createIssue.create();
    return id;
  }
  public static String createIssue(String title, String description, String baseUrl, String username, String password) {
    LoginPage loginPage = new LoginPage();
    loginPage.openPage(baseUrl);
    loginPage.waitForPageLoaded();
    MainPage mainPage = loginPage.login(username, password).waitForPageLoaded();
    IssuesListPage issuesPage = mainPage.goToIssues().waitForPageLoaded();
    CreateIssueDialog createIssue = issuesPage.openCreateIssueDialog();
    createIssue.setTitle(title);
    createIssue.setDescription(description);
    String id = createIssue.create();
    return id;
  }

  public static void deleteIssue(String id, String baseUrl) {
    MainPage mainPage = new MainPage();
    mainPage = mainPage.openPage(baseUrl);
    mainPage.waitForPageLoaded();
    IssuesListPage issuesPage = mainPage.goToIssues();
    issuesPage.waitForPageLoaded();
    IssueDetailsPage issueDetails = issuesPage.openById(id);
    issueDetails.waitForPageLoaded();
    issueDetails.deleteIssue();
  }
}

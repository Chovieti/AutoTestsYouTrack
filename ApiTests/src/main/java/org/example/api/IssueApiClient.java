package org.example.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.dto.IssueDTO;

import java.util.Map;

public class IssueApiClient extends BaseApiClient {
  private static final String ISSUES_ENDPOINT = "/issues";

  public IssueApiClient(String baseUrl, String basePath, String defaultToken) {
    super(baseUrl, basePath, defaultToken);
  }

  public Response createIssue(IssueDTO issueDTO) {
    return createIssue(issueDTO, this.defaultToken);
  }

  public Response createIssue(IssueDTO issueDTO, String token) {
    return RestAssured
        .given()
          .spec(getRequestSpec(token))
          .body(issueDTO)
        .when()
          .post(ISSUES_ENDPOINT)
        .then()
          .extract()
          .response();
  }

  public Response getIssue(String issueId) {
    return RestAssured
        .given()
          .spec(getRequestSpec())
          .queryParam("fields", "id,summary,description,customFields(name,value(name,login))")
        .when()
          .get(ISSUES_ENDPOINT + "/" + issueId)
        .then()
          .extract()
          .response();
  }

  public Response getIssues(Map<String, Object> queryParams) {
    return RestAssured
        .given()
          .spec(getRequestSpec())
          .queryParams(queryParams)
        .when()
          .get(ISSUES_ENDPOINT)
        .then()
          .extract()
          .response();
  }

  public Response updateIssues(String issueId, IssueDTO issueDTO) {
    return RestAssured
        .given()
          .spec(getRequestSpec())
          .queryParam("fields", "id,summary,description,customFields(name,value(name,login))")
          .body(issueDTO)
        .when()
          .post(ISSUES_ENDPOINT + "/" + issueId)
        .then()
          .extract()
          .response();
  }

  public Response deleteIssue(String issueId) {
    return deleteIssue(issueId, this.defaultToken);
  }

  public Response deleteIssue(String issueId, String token) {
    return RestAssured
        .given()
          .spec(getRequestSpec(token))
        .when()
          .delete(ISSUES_ENDPOINT + "/" + issueId)
        .then()
          .extract()
          .response();
  }
}

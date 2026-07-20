package org.example.api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseApiClient {
  protected final String baseUrl;
  protected final String basePath;
  protected final String defaultToken;

  public BaseApiClient(String baseUrl, String basePath, String defaultToken) {
    this.baseUrl = baseUrl;
    this.basePath = basePath;
    this.defaultToken = defaultToken;
  }

  protected RequestSpecification getRequestSpec() {
    return getRequestSpec(this.defaultToken);
  }

  protected RequestSpecification getRequestSpec(String token) {
    RequestSpecBuilder builder = new RequestSpecBuilder()
        .setBaseUri(baseUrl)
        .setBasePath(basePath)
        .setContentType(ContentType.JSON);

    if (token != null) {
      builder.addHeader("Authorization", "Bearer " + token);
    }

    return builder.build();
  }
}

package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IssueDTO(
    String summary,
    String description,
    ProjectDTO project,
    List<CustomFieldDTO> customFields
) {
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String summary;
    private String description;
    private ProjectDTO project;
    private List<CustomFieldDTO> customFields;

    public Builder summary(String summary) {
      this.summary = summary;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder project(ProjectDTO project) {
      this.project = project;
      return this;
    }

    public Builder customFields(List<CustomFieldDTO> customFields) {
      this.customFields = customFields;
      return this;
    }

    public IssueDTO build() {
      return new IssueDTO(summary, description, project, customFields);
    }
  }
}

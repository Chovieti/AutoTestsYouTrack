package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomFieldDTO(
    String name,
    String $type,
    Object value
) {
  public static CustomFieldDTO createStateField(String stateName) {
    return new CustomFieldDTO("Stage", "SingleEnumIssueCustomField", new ValueWithName(stateName));
  }

  public static CustomFieldDTO createAssigneeField(String userLogin) {
    return new CustomFieldDTO("Assignee", "SingleUserIssueCustomField", new ValueWithLogin(userLogin));
  }

  public record ValueWithName(String name) {}
  public record ValueWithLogin(String login) {}
}

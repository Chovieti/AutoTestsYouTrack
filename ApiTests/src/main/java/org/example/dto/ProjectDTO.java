package org.example.dto;

public record ProjectDTO(
    String id,
    String $type
) {
  public ProjectDTO(String id) {
    this(id, "Project");
  }
}

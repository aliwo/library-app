package com.group.libraryapp.dto.user.request;

public class UserUpdateRequest {

  private long id;
  private String name;

  public long getId() {
    return id;
  }

  public UserUpdateRequest(long id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

}

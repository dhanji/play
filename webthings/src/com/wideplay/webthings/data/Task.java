package com.wideplay.webthings.data;

import java.util.Date;

/**
 * A data model object used for storage.
 */
public class Task {
  private long id;
  private String text;
  private String owner; // belongs to.

  private Date createdOn;
  private Date dueOn;
  private Date completedOn;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  public Date getDueOn() {
    return dueOn;
  }

  public void setDueOn(Date dueOn) {
    this.dueOn = dueOn;
  }

  public Date getCompletedOn() {
    return completedOn;
  }

  public void setCompletedOn(Date completedOn) {
    this.completedOn = completedOn;
  }
}

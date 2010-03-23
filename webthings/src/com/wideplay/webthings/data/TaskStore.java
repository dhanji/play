package com.wideplay.webthings.data;

import com.google.inject.ImplementedBy;

import java.util.Collection;

/**
 * A repository for tasks.
 */
@ImplementedBy(InMemoryTaskStore.class)
public interface TaskStore {
  Task fetch(long id);

  void store(Task task);

  Collection<Task> listAll();
}

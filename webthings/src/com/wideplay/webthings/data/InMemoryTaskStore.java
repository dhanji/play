package com.wideplay.webthings.data;

import com.google.common.collect.MapMaker;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * An in-memory repository for to-do items.
 */
@Singleton
public class InMemoryTaskStore implements TaskStore {
  // id -> tasklist map
  private final ConcurrentMap<Long, Task> map = new MapMaker().makeMap();

  public Task fetch(long id) {
    return map.get(id);
  }

  public void store(Task task) {
    map.put(task.getId(), task);
  }

  public Collection<Task> listAll() {
    return map.values();
  }
}

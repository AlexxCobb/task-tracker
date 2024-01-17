package org.tasktracker.manager.interfaces;

import org.tasktracker.taskmodel.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(int id);
}

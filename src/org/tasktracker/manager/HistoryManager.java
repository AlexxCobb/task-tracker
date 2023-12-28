package org.tasktracker.manager;

import org.tasktracker.taskmodel.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    public List<Task> getHistory();
}

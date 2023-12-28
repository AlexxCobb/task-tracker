package org.tasktracker.manager;

import org.tasktracker.manager.HistoryManager;
import org.tasktracker.manager.InMemoryHistoryManager;
import org.tasktracker.manager.InMemoryTaskManager;
import org.tasktracker.manager.Manager;

public class Managers {

    public static Manager getDefault() {
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
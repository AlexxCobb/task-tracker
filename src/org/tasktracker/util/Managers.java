package org.tasktracker.util;

import org.tasktracker.manager.InMemoryHistoryManager;
import org.tasktracker.manager.InMemoryTaskManager;
import org.tasktracker.manager.interfaces.HistoryManager;
import org.tasktracker.manager.interfaces.Manager;

public class Managers {

    public static Manager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
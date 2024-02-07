package org.tasktracker.manager;

import org.tasktracker.manager.interfaces.HistoryManager;
import org.tasktracker.manager.interfaces.Manager;
import org.tasktracker.manager.memorymanager.InMemoryHistoryManager;
import org.tasktracker.manager.memorymanager.InMemoryTaskManager;

public class Managers {

    public static Manager getDefault() {
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
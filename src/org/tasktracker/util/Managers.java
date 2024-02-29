package org.tasktracker.util;

import org.tasktracker.manager.FileBackedTasksManager;
import org.tasktracker.manager.HttpTaskManager;
import org.tasktracker.manager.InMemoryHistoryManager;
import org.tasktracker.manager.interfaces.HistoryManager;
import org.tasktracker.server.KVServer;

public class Managers {

    public static HttpTaskManager getDefaultManager() {
        String url = "http://localhost:" + KVServer.PORT;
        return new HttpTaskManager(url);
    }

    public static FileBackedTasksManager getDefaultFileBackedManager() {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
package org.tasktracker.test;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tasktracker.manager.HttpTaskManager;
import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;
import org.tasktracker.server.KVServer;
import org.tasktracker.util.Managers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest {
    HttpTaskManager manager;
    KVServer kvServer;

    @BeforeEach
    public void setUp() {
        try {
            kvServer = new KVServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        kvServer.start();
        manager = Managers.getDefaultManager();
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    void whenCreateTaskThenAllLoadFromServer() {
        Task task1 = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 20, 2), Duration.ofMinutes(20));
        int task1Id = manager.addTask(task1);
        Task task2 = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 23, 2), Duration.ofMinutes(20));
        int task2Id = manager.addTask(task2);
        manager.getTaskById(task1Id);
        manager.getTaskById(task2Id);
        assertEquals(manager.getTasks(), manager.getHistory());

        HttpTaskManager taskManager = manager.load();
        assertEquals(manager.getTasks(), taskManager.getTasks());
    }

    @Test
    void whenCreateEpicThenAllLoadFromServer() {
        Epic epic = new Epic("name", "details");
        int epic1Id = manager.addEpic(epic);
        Epic epic1 = new Epic("name", "details");
        int epic2Id = manager.addEpic(epic1);
        manager.getEpicById(epic1Id);
        manager.getEpicById(epic2Id);
        assertEquals(manager.getEpics(), manager.getHistory());

        HttpTaskManager taskManager = manager.load();
        assertEquals(manager.getTasks(), taskManager.getTasks());
    }

    @Test
    void whenCreateSubtaskThenAllLoadFromServer() {
        Epic epic = new Epic("name", "details");
        int epic1Id = manager.addEpic(epic);
        Epic epic1 = new Epic("name", "details");
        int epic2Id = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("name", "details", epic1Id, LocalDateTime.of(2000, 4, 20, 2, 2), Duration.ofMinutes(30));
        int subId1 = manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("name", "details", epic1Id, LocalDateTime.of(2000, 4, 21, 2, 2), Duration.ofMinutes(30));
        int subId2 = manager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("name", "details", epic2Id, LocalDateTime.of(2000, 4, 22, 2, 2), Duration.ofMinutes(30));
        int subId3 = manager.addSubtask(subtask3);

        manager.getSubtaskById(subId1);
        manager.getSubtaskById(subId2);
        manager.getSubtaskById(subId3);
        assertEquals(manager.getSubTasks(), manager.getHistory());

        HttpTaskManager taskManager = manager.load();
        assertEquals(manager.getSubTasks(), taskManager.getSubTasks());
    }

    @Test
    void whenCreateTasksAndGetHistoryThenHistoryLoadFromServer() {
        Task task1 = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 20, 2), Duration.ofMinutes(20));
        int task1Id = manager.addTask(task1);
        Task task2 = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 23, 2), Duration.ofMinutes(20));
        int task2Id = manager.addTask(task2);
        Epic epic = new Epic("name", "details");
        int epic1Id = manager.addEpic(epic);
        Epic epic1 = new Epic("name", "details");
        int epic2Id = manager.addEpic(epic1);

        manager.getEpicById(epic1Id);
        manager.getEpicById(epic2Id);
        manager.getTaskById(task1Id);
        manager.getTaskById(task2Id);
        manager.getTaskById(task1Id);

        HttpTaskManager taskManager = manager.load();
        assertEquals(manager.getHistory(), taskManager.getHistory());
    }
}

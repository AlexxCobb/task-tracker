package org.tasktracker.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tasktracker.manager.FileBackedTasksManager;
import org.tasktracker.manager.InMemoryTaskManager;
import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private static Path saveTasks;

    @BeforeEach
    public void setUp() throws IOException {
        createFileBacked();
    }

    @AfterEach
    void clearFileFromTest() throws IOException {
        Path saveTasks = Paths.get("saveTest.csv");
        Files.delete(saveTasks);
    }

    @Test
    void whenCreateTaskEpicSubtaskThenAllOfThemSaveToFile() throws IOException {
        Task task = new Task("name", "details", LocalDateTime.of(2000,2,20,20,2), Duration.ofMinutes(20));
        manager.addTask(task);
        Epic epic = new Epic("name", "details");
        manager.addEpic(epic);
        int epicId = epic.getId();
        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2000,4,20,2,2),Duration.ofMinutes(30));
        manager.addSubtask(subtask);
        List<String> savedTasks = new ArrayList<>();

        savedTasks.add(epic.toCSV());
        savedTasks.add(task.toCSV());
        savedTasks.add(subtask.toCSV());
        savedTasks.add("");

        BufferedReader br = new BufferedReader(new FileReader(saveTasks.toFile(), StandardCharsets.UTF_8));
        List<String> tasks = new ArrayList<>();
        br.readLine();
        while (br.ready()) {
            var line = br.readLine();
            tasks.add(line);
        }
        br.close();

        assertEquals(savedTasks, tasks);
    }

    @Test
    void whenLoadFromFileTasksWithEmptyHistoryThenAllOfTasksCorrectlyLoadFromFile() throws IOException {
        Task task = new Task("name", "details", LocalDateTime.of(2000,2,20,20,2), Duration.ofMinutes(20));
        manager.addTask(task);
        Epic epic = new Epic("name", "details");
        manager.addEpic(epic);
        int epicId = epic.getId();
        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2000,3,20,2,2),Duration.ofMinutes(30));
        manager.addSubtask(subtask);

        FileBackedTasksManager managerFromLoad = FileBackedTasksManager.loadFromFile(saveTasks.toFile());

        assertEquals(manager.getEpics(), managerFromLoad.getEpics());
        assertEquals(manager.getTasks(), managerFromLoad.getTasks());
        assertEquals(manager.getSubTasks(), managerFromLoad.getSubTasks());
    }

    @Test
    void whenLoadFromFileTasksAndHistoryThenAllOfThemLoadFromFile() throws IOException {
        Task task = new Task("name", "details", LocalDateTime.of(2000,2,20,20,2), Duration.ofMinutes(20));
        int taskId = manager.addTask(task);
        Epic epic = new Epic("name", "details");
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2020,2,20,2,2),Duration.ofMinutes(30));
        int subtaskId = manager.addSubtask(subtask);

        manager.getTaskById(taskId);
        manager.getEpicById(epicId);
        manager.getSubtaskById(subtaskId);

        FileBackedTasksManager managerFromLoad = FileBackedTasksManager.loadFromFile(saveTasks.toFile());

        assertEquals(manager.getEpics(), managerFromLoad.getEpics());
        assertEquals(manager.getTasks(), managerFromLoad.getTasks());
        assertEquals(manager.getSubTasks(), managerFromLoad.getSubTasks());
        assertEquals(manager.getHistory(), managerFromLoad.getHistory());
    }

    @Test
    void whenLoadFromEmptyFileThenLoadEmptyManager() throws IOException {
        FileBackedTasksManager managerFromLoad = FileBackedTasksManager.loadFromFile(saveTasks.toFile());

        assertEquals(manager.getHistory(), managerFromLoad.getHistory());
        assertEquals(manager.getTasks(), managerFromLoad.getTasks());
    }

    @Test
    void whenHistorySaveToFileAndLoadFromFileThenHistorysEquals() throws IOException {
        Task task = new Task("name", "details", LocalDateTime.of(2000,2,20,20,2), Duration.ofMinutes(20));
        int taskId = manager.addTask(task);
        Epic epic = new Epic("name", "details");
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2020,2,20,2,2),Duration.ofMinutes(30));
        int subtaskId = manager.addSubtask(subtask);

        manager.getTaskById(taskId);
        manager.getEpicById(epicId);
        manager.getSubtaskById(subtaskId);
        FileBackedTasksManager managerFromLoad = FileBackedTasksManager.loadFromFile(saveTasks.toFile());

        assertEquals(manager.getHistory(), managerFromLoad.getHistory());
    }
    private void createFileBacked() throws IOException {
        try {
            saveTasks = Files.createFile(Paths.get("saveTest.csv"));
        } catch (FileAlreadyExistsException e) {
            Files.delete(Paths.get("saveTest.csv"));
            saveTasks = Files.createFile(Paths.get("saveTest.csv"));
        }
        manager = new FileBackedTasksManager(saveTasks.toFile());
    }
}



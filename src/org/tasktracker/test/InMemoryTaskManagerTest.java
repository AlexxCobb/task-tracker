package org.tasktracker.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tasktracker.manager.InMemoryTaskManager;
import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void whenCreateThreeTasksIdAssignedInOrder() {
        Task task1 = new Task("name", "details", LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));
        Task task2 = new Task("name", "details", LocalDateTime.of(2020, 3, 20, 20, 2), Duration.ofMinutes(20));
        Task task3 = new Task("name", "details", LocalDateTime.of(2020, 4, 20, 20, 2), Duration.ofMinutes(20));

        int taskId1 = manager.addTask(task1);
        int taskId2 = manager.addTask(task2);
        int taskId3 = manager.addTask(task3);

        var tasks = manager.getTasks();

        assertEquals(1, taskId1);
        assertEquals(2, taskId2);
        assertEquals(3, taskId3);
        assertEquals(3, manager.getTasks().size());
    }

    @Test
    void whenClearAllTasksThenTasksSize0() {
        Task task = new Task("name", "details", LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));
        manager.addTask(task);
        manager.clearAllTasks();
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    void whenClearAllEpicsThenEpicsSize0AndSubtasksSize0() {
        Epic epic = new Epic("Epic NAME", "Epic description");
        int epicId = manager.addEpic(epic);
        Subtask subtask1 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 23, 2, 2), Duration.ofMinutes(30));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.clearAllEpics();
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubTasks().isEmpty());
    }

    @Test
    void whenClearAllSubtasksThenSubtasksSize0AndEpicEpicListSubtasksIdsEmpty() {
        Epic epic = new Epic("Epic NAME", "Epic description");
        int epicId = manager.addEpic(epic);

        Subtask subtask1 = new Subtask("name", "details", epicId, LocalDateTime.of(2020, 2, 20, 2, 2), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("name", "details", epicId, LocalDateTime.of(2021, 2, 20, 2, 2), Duration.ofMinutes(30));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.clearAllSubtasks();
        assertTrue(manager.getEpicSubtasks(epic).isEmpty());
        assertTrue(manager.getSubTasks().isEmpty());
    }
}

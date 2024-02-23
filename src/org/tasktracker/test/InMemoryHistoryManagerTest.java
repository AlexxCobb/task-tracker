package org.tasktracker.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tasktracker.manager.InMemoryHistoryManager;
import org.tasktracker.manager.InMemoryTaskManager;
import org.tasktracker.manager.interfaces.HistoryManager;
import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;
import org.tasktracker.model.enums.Status;
import org.tasktracker.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    HistoryManager manager;

    @BeforeEach
    public void setUp() {
        manager = Managers.getDefaultHistory();
    }

    @Test
    void whenCreateTaskThenGetHistorySizeIncrease() {
        Task task = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 20, 2), Duration.ofMinutes(20));
        manager.add(task);
        var history = manager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void whenNoAddedTaskThenHistorySize0() {
        var history = manager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void whenCreateTaskAndAddSameTaskSeveralTimesThenHistorySize1WithoutDuplicationAndGetCorrectlyTask() {
        Task task = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 20, 2), Duration.ofMinutes(20));
        manager.add(task);
        manager.add(task);
        manager.add(task);
        manager.add(task);
        manager.add(task);
        manager.add(task);
        var history = manager.getHistory();

        Task task1 = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 20, 2), Duration.ofMinutes(20));

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void whenCreateTaskEpicSubtaskAndAddSameTaskEpicSubtaskSeveralTimesThenHistorySize3WithoutDuplication() {
        Task task = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 20, 2), Duration.ofMinutes(20));
        task.setId(1);
        manager.add(task);

        Epic epic = new Epic("name", "details");
        epic.setId(2);
        manager.add(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 20, 2, 2), Duration.ofMinutes(30));
        subtask.setId(3);
        manager.add(subtask);

        manager.add(task);
        manager.add(epic);
        manager.add(subtask);
        manager.add(task);
        manager.add(epic);
        manager.add(subtask);

        var history = manager.getHistory();
        assertEquals(3, history.size());
    }

    @Test
    void whenDeleteTaskFromHeadOfHistoryThenHistorySizeDecreaseAndNextTaskBecomeHead() {
        Task task = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 20, 2), Duration.ofMinutes(20));
        task.setId(1);
        manager.add(task);

        Epic epic = new Epic("name", "details");
        epic.setId(2);
        manager.add(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 20, 2, 2), Duration.ofMinutes(30));
        subtask.setId(3);
        manager.add(subtask);

        var history1 = manager.getHistory();
        assertEquals(task, history1.get(0));
        assertEquals(epic, history1.get(1));
        assertEquals(subtask, history1.get(2));

        manager.remove(1);
        var history2 = manager.getHistory();
        assertEquals(2, history2.size());
        assertEquals(epic, history2.get(0));
        assertEquals(subtask, history2.get(1));
    }

    @Test
    void whenDeleteTaskFromMiddleOfHistoryThenHistorySizeDecrease() {
        Task task = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 20, 2), Duration.ofMinutes(20));
        task.setId(1);
        manager.add(task);

        Epic epic = new Epic("name", "details");
        epic.setId(2);
        manager.add(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 20, 2, 2), Duration.ofMinutes(30));
        subtask.setId(3);
        manager.add(subtask);

        manager.remove(2);
        var history2 = manager.getHistory();
        assertEquals(2, history2.size());
        assertEquals(task, history2.get(0));
        assertEquals(subtask, history2.get(1));
    }

    @Test
    void whenDeleteTaskFromEndOfHistoryThenHistorySizeDecreaseAndPreviousTaskBecomeTail() {
        Task task = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 20, 2), Duration.ofMinutes(20));
        task.setId(1);
        manager.add(task);

        Epic epic = new Epic("name", "details");
        epic.setId(2);
        manager.add(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 20, 2, 2), Duration.ofMinutes(30));
        subtask.setId(3);
        manager.add(subtask);

        manager.remove(3);
        var history2 = manager.getHistory();
        assertEquals(2, history2.size());
        assertEquals(task, history2.get(0));
        assertEquals(epic, history2.get(1));
    }
}

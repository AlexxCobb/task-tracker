package org.tasktracker.test;

import org.junit.jupiter.api.Test;
import org.tasktracker.manager.interfaces.HistoryManager;
import org.tasktracker.manager.interfaces.Manager;
import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;
import org.tasktracker.model.enums.Status;
import org.tasktracker.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends Manager> {
    T manager;

    @Test
    public void whenCreateEpicWithoutSubtasksThenEpicStatusNEW() {
        Epic epic = new Epic("Epic NAME", "Epic description");
        int epicId = manager.addEpic(epic);
        var subtasks = manager.getEpicSubtasks(epic);

        assertTrue(subtasks.isEmpty());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void whenCreateEpicWithAllSubtasksHaveStatusNEWThenEpicStatusNEW() {
        Epic epic = new Epic("Epic NAME", "Epic description");
        int epicId = manager.addEpic(epic);

        Subtask subtask1 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 22, 2, 2), Duration.ofMinutes(30));
        Subtask subtask3 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 23, 2, 2), Duration.ofMinutes(30));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        var subtasks = manager.getEpicSubtasks(epic);

        assertEquals(3, subtasks.size());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void whenCreateEpicWithAllSubtasksHaveStatusDONEThenEpicStatusDONE() {
        Epic epic = new Epic("Epic NAME", "Epic description");
        int epicId = manager.addEpic(epic);

        Subtask subtask1 = new Subtask("name", "details", epicId, LocalDateTime.of(2020, 2, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("name", "details", epicId, LocalDateTime.of(2021, 2, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask3 = new Subtask("name", "details", epicId, LocalDateTime.of(2022, 2, 21, 2, 2), Duration.ofMinutes(30));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        assertEquals(Status.NEW, subtask1.getStatus());
        assertEquals(Status.NEW, subtask2.getStatus());
        assertEquals(Status.NEW, subtask3.getStatus());

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);

        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void whenCreateEpicWithSubtasksStatusDONEAndNEWThenEpicStatusIN_PROGRESS() {
        Epic epic = new Epic("Epic NAME", "Epic description");
        int epicId = manager.addEpic(epic);

        Subtask subtask1 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 22, 2, 2), Duration.ofMinutes(30));
        Subtask subtask3 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 23, 2, 2), Duration.ofMinutes(30));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        subtask3.setStatus(Status.DONE);
        manager.updateSubtask(subtask3);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void whenCreateEpicWithSubtasksStatusIN_PROGRESSAndNEWThenEpicStatusIN_PROGRESS() {
        Epic epic = new Epic("Epic NAME", "Epic description");
        int epicId = manager.addEpic(epic);

        Subtask subtask1 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask3 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        subtask3.setStatus(Status.IN_PROGRESS);
        subtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask3);
        manager.updateSubtask(subtask1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }


    @Test
    public void whenCreateTaskThenTaskSizeIncreaseAndCorrectlyGetById() {
        Task task = new Task("name", "details", LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));
        int taskId = manager.addTask(task);
        var savedTask = manager.getTaskById(taskId);
        var tasks = manager.getTasks();

        assertNotNull(savedTask);
        assertEquals(task, savedTask);
        assertEquals(1, manager.getTasks().size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    public void whenAddTaskWithStartTimeAndDurationThenTaskCorrectlyCreate() {
        Task task = new Task("name", "details", LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));
        manager.addTask(task);
        LocalDateTime date = LocalDateTime.of(2020, 2, 20, 20, 2);
        Duration duration = Duration.ofMinutes(20);

        assertEquals(date, manager.getTasks().get(0).getStartTime());
        assertEquals(duration, manager.getTasks().get(0).getDuration());
    }

    @Test
    public void whenCreateThreeTasksIdAssignedInOrderAndGetTasksCorrectly() {
        Task task1 = new Task("name", "details", LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));
        Task task2 = new Task("name", "details", LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));
        Task task3 = new Task("name", "details", LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));

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
    public void whenCreateEpicThenEpicSizeIncreaseAndCorrectlyGetById() {
        Epic epic = new Epic("name", "descr");
        int epicId = manager.addEpic(epic);
        var savedEpic = manager.getEpicById(epicId);
        var epics = manager.getEpics();

        assertNotNull(savedEpic);
        assertEquals(epic, savedEpic);
        assertEquals(1, manager.getEpics().size());
        assertEquals(epic, epics.get(0));
    }

    @Test
    public void whenCreateThreeEpicsIdAssignedInOrderAndGetEpicCorrectly() {
        Epic epic1 = new Epic("name", "descr");
        Epic epic2 = new Epic("name", "descr");
        Epic epic3 = new Epic("name", "descr");

        int epicId1 = manager.addEpic(epic1);
        int epicId2 = manager.addEpic(epic2);
        int epicId3 = manager.addEpic(epic3);

        var epics = manager.getEpics();

        assertEquals(1, epicId1);
        assertEquals(2, epicId2);
        assertEquals(3, epicId3);
        assertEquals(3, manager.getEpics().size());
    }

    @Test
    public void whenCreateSubtaskThenSubtaskSizeIncreaseAndCorrectlyGetById() {
        Epic epic = new Epic("name", "descr");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        int subtaskId = manager.addSubtask(subtask);

        var savedSubtask = manager.getSubtaskById(subtaskId);
        var subtasks = manager.getSubTasks();

        assertNotNull(savedSubtask);
        assertEquals(subtask, savedSubtask);
        assertEquals(1, manager.getSubTasks().size());
        assertEquals(subtask, subtasks.get(0));
        assertEquals(epicId, subtask.getEpicId());
    }

    @Test
    public void whenCreateSubtaskThenSubtaskAddToEpicListOfSubtasks() {
        Epic epic = new Epic("name", "descr");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        int subtaskId = manager.addSubtask(subtask);

        assertEquals(subtaskId, epic.getSubtaskIds().get(0));
    }

    @Test
    public void whenCreateSubtaskWithWrongEpicIdThrowException() {
        Subtask subtask = new Subtask("name", "details", 6, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));

        assertThrows(NullPointerException.class,
                () -> manager.addSubtask(subtask)
        );
    }

    @Test
    void whenUpdateTaskStatusToDONEThenTaskStatusChangeToDONE() {
        Task task = new Task("name", "details", LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));
        manager.addTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, manager.getTasks().get(0).getStatus());
    }

    @Test
    void whenCreateEpicWith3SubtasksThenSizeOfEpicListSubtasksIdsBecomes3() {
        Epic epic = new Epic("Epic NAME", "Epic description");
        int epicId = manager.addEpic(epic);

        Subtask subtask1 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask3 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        assertEquals(3, epic.getSubtaskIds().size());
    }

    @Test
    void whenCreateTaskAndGetByIdThenReturnCorrectTask() {
        Task task = new Task("name", "details", LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));
        int taskId = manager.addTask(task);

        Task task1 = new Task("name", "details", LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));
        task1.setId(1);

        assertEquals(task1, manager.getTaskById(taskId));
    }

    @Test
    void whenCreateEpicAndGetByIdThenReturnCorrectEpic() {
        Epic epic = new Epic("name", "details");
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        manager.addSubtask(subtask);

        Epic epic1 = new Epic("name", "details");
        epic1.setId(1);
        ArrayList<Integer> subIds = new ArrayList<>();
        subIds.add(2);
        epic1.setSubtaskIds(subIds);

        assertEquals(epic1, manager.getEpicById(epicId));
    }

    @Test
    void whenCreateSubtaskAndGetByIdThenReturnCorrectSubtask() {
        Epic epic = new Epic("name", "details");
        int epicId = manager.addEpic(epic);
        Subtask subtask1 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        int subtaskId = manager.addSubtask(subtask1);

        Subtask subtask3 = new Subtask("name", "details", epicId, LocalDateTime.of(2000, 2, 21, 2, 2), Duration.ofMinutes(30));
        subtask3.setId(2);

        assertEquals(subtask3, manager.getSubtaskById(subtaskId));
    }

    @Test
    void whenCreateTaskAndGetByIdWithWrongIdThenReturnNull() {
        Task task = new Task("name", "details", LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));
        task.setId(3);

        assertNull(manager.getTaskById(task.getId()));
    }

    @Test
    void whenCreateEpicAndGetByIdWithWrongIdThenReturnNull() {
        Epic epic = new Epic("name", "details");
        epic.setId(1);

        assertNull(manager.getTaskById(epic.getId()));
    }

    @Test
    void whenCreateSubtaskAndGetByIdWithWrongIdThenReturnNull() {
        Subtask subtask = new Subtask("name", "details", 6, LocalDateTime.of(2020, 2, 20, 20, 2), Duration.ofMinutes(20));

        assertNull(manager.getTaskById(subtask.getId()));
    }
}

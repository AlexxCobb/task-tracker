
import org.tasktracker.manager.interfaces.Manager;
import org.tasktracker.util.Managers;
import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("�������!");

        // �������� ����������������� ����

        Manager inMemoryTaskManager = Managers.getDefault();


        Task task = new Task("������� � �������", "������ ���, �����, ��������, ����", null, Duration.ofMinutes(30));
        Task task2 = new Task("������� � ���������", "������� ������ � ������", LocalDateTime.of(2024, 3, 23, 13, 13), Duration.ofMinutes(50));

        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(task2);

        int taskId1 = task.getId();
        int taskId2 = task2.getId();

        Epic epic = new Epic("�������� ������ �� �/�",
                "��������� ������ ��� ������, ������ �� � �������� �� �����������");
        inMemoryTaskManager.addEpic(epic);
        int epicId1 = epic.getId();

        Subtask subtask1 = new Subtask("������ � ������", "��������� ������ ��� ������ ������", epicId1, LocalDateTime.of(2024, 3, 23, 12, 30), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("������ ������", "������� ���������� ������� � ������ ������", epicId1, LocalDateTime.of(2024, 3, 23, 13, 0), Duration.ofMinutes(30));
        Subtask subtask3 = new Subtask("����������", "������� ��������� ���������� � �������� ������", epicId1, LocalDateTime.of(2024, 3, 25, 13, 1), Duration.ofMinutes(30));

        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);
        inMemoryTaskManager.addSubtask(subtask3);
        int subTaskId1 = subtask1.getId();
        int subTaskId2 = subtask2.getId();
        int subTaskId3 = subtask3.getId();


        inMemoryTaskManager.getTaskById(taskId1);
        inMemoryTaskManager.getTaskById(taskId2);
        inMemoryTaskManager.getTaskById(taskId1);
        inMemoryTaskManager.getTaskById(taskId2);
        inMemoryTaskManager.getTaskById(taskId2);

        inMemoryTaskManager.getEpicById(epicId1);


        System.out.println(inMemoryTaskManager.getPrioritizedTasks());
    }
}

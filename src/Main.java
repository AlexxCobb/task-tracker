import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("�������!");

        // �������� ����������������� ����

        Manager inMemoryTaskManager = Managers.getDefault();


        Task task = new Task("������� � �������", "������ ���,�����,��������, ����");
        Task task2 = new Task("������� � ���������", "������� ������ � ������");

        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(task2);

        int taskId = task.getId();
        int taskId2 = task2.getId();

        Epic epic = new Epic("�������� ������ �� �/�",
                "��������� ������ ��� ������, ������ �� � �������� �� �����������");
        inMemoryTaskManager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("������ � ������", "��������� ������ ��� ������ ������", epicId);
        Subtask subtask1 = new Subtask("������ ������", "������� ���������� ������� � ������ ������", epicId);
        Subtask subtask2 = new Subtask("����������", "������� ��������� ���������� � �������� ������", epicId);

        inMemoryTaskManager.addSubtask(subtask);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("������ �������", "������ ������� ������� �� ��");
        inMemoryTaskManager.addEpic(epic2);
        int epicId2 = epic2.getId();

        Subtask subtask3 = new Subtask("�������", "������ ������� ������� �� ��", epicId2);
        inMemoryTaskManager.addSubtask(subtask3);

        subtask.setStatusOfTask(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask);

        inMemoryTaskManager.getTaskForId(taskId);
        inMemoryTaskManager.getTaskForId(taskId2);
        inMemoryTaskManager.getTaskForId(taskId);
        inMemoryTaskManager.getTaskForId(taskId2);

        System.out.println(inMemoryTaskManager.getHistory().toString());
    }
}

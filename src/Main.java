import org.tasktracker.manager.interfaces.Manager;
import org.tasktracker.manager.Managers;
import org.tasktracker.taskmodel.Epic;
import org.tasktracker.taskmodel.Status;
import org.tasktracker.taskmodel.Subtask;
import org.tasktracker.taskmodel.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("�������!");

        // �������� ����������������� ����

        Manager inMemoryTaskManager = Managers.getDefault();


        Task task = new Task("������� � �������", "������ ���, �����, ��������, ����");
        Task task2 = new Task("������� � ���������", "������� ������ � ������");

        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(task2);

        int taskId1 = task.getId();
        int taskId2 = task2.getId();

        Epic epic = new Epic("�������� ������ �� �/�",
                "��������� ������ ��� ������, ������ �� � �������� �� �����������");
        inMemoryTaskManager.addEpic(epic);
        int epicId1 = epic.getId();

        Subtask subtask1 = new Subtask("������ � ������", "��������� ������ ��� ������ ������", epicId1);
        Subtask subtask2 = new Subtask("������ ������", "������� ���������� ������� � ������ ������", epicId1);
        Subtask subtask3 = new Subtask("����������", "������� ��������� ���������� � �������� ������", epicId1);

        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);
        inMemoryTaskManager.addSubtask(subtask3);
        int subTaskId1 = subtask1.getId();
        int subTaskId2 = subtask2.getId();
        int subTaskId3 = subtask3.getId();


        Epic epic2 = new Epic("������ �������", "������ ������� ������� �� ��");
        inMemoryTaskManager.addEpic(epic2);
        int epicId2 = epic2.getId();


        inMemoryTaskManager.getTaskForId(taskId1);
        inMemoryTaskManager.getTaskForId(taskId2);
        inMemoryTaskManager.getTaskForId(taskId1);
        inMemoryTaskManager.getTaskForId(taskId2);
        inMemoryTaskManager.getTaskForId(taskId2);

        inMemoryTaskManager.getEpicForId(epicId1);
        inMemoryTaskManager.getEpicForId(epicId2);
        inMemoryTaskManager.getEpicForId(epicId2);

        inMemoryTaskManager.getSubTaskForId(subTaskId1);
        inMemoryTaskManager.getSubTaskForId(subTaskId2);
        inMemoryTaskManager.getSubTaskForId(subTaskId2);
        inMemoryTaskManager.getSubTaskForId(subTaskId3);
        inMemoryTaskManager.getSubTaskForId(subTaskId1);
        inMemoryTaskManager.getSubTaskForId(subTaskId2);



        inMemoryTaskManager.removeTask(taskId1);

        inMemoryTaskManager.removeEpic(epicId1);

        System.out.println(inMemoryTaskManager.getHistory().toString());
    }
}

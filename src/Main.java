import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("�������!");

        // �������� ����������������� ����
        Manager manager = new Manager();

        Task task = new Task("������� � �������", "������ ���,�����,��������, ����");
        Task task2 = new Task("������� � ���������", "������� ������ � ������");

        manager.addTask(task);
        manager.addTask(task2);

        Epic epic = new Epic("�������� ������ �� �/�",
                "��������� ������ ��� ������, ������ �� � �������� �� �����������");
        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("������ � ������", "��������� ������ ��� ������ ������", epicId);
        Subtask subtask1 = new Subtask("������ ������", "������� ���������� ������� � ������ ������", epicId);
        Subtask subtask2 = new Subtask("����������", "������� ��������� ���������� � �������� ������", epicId);

        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("������ �������", "������ ������� ������� �� ��");
        manager.addEpic(epic2);
        int epicId2 = epic2.getId();

        Subtask subtask3 = new Subtask("�������", "������ ������� ������� �� ��", epicId2);
        manager.addSubtask(subtask3);

        subtask.setStatusOfTask("DONE");
        manager.updateSubtask(subtask);

        manager.removeSubtask(subtask.getId());

        System.out.println(manager.getEpicForId(epicId).toString());
    }
}

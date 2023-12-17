import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        // проверка работоспособности кода
        Manager manager = new Manager();

        Task task = new Task("Сходить в магазин", "Купить сыр,багет,помидоры, вино");
        Task task2 = new Task("Сходить в химчистку", "Отнести пальто и куртку");

        manager.addTask(task);
        manager.addTask(task2);

        Epic epic = new Epic("Поменять резину на а/м",
                "Прочитать отзывы про резину, купить ее и поменять на шиномонтаже");
        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("Отзывы о резине", "Прочитать отзывы про зимнюю резину", epicId);
        Subtask subtask1 = new Subtask("Купить резину", "Выбрать конкретный магазин и купить резину", epicId);
        Subtask subtask2 = new Subtask("Шиномонтаж", "Выбрать ближайший шиномонтаж и поменять резину", epicId);

        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("Купить подарки", "Купить подарки близким на НГ");
        manager.addEpic(epic2);
        int epicId2 = epic2.getId();

        Subtask subtask3 = new Subtask("Подарки", "Купить подарки близким на НГ", epicId2);
        manager.addSubtask(subtask3);

        subtask.setStatusOfTask("DONE");
        manager.updateSubtask(subtask);

        manager.removeSubtask(subtask.getId());

        System.out.println(manager.getEpicForId(epicId).toString());
    }
}

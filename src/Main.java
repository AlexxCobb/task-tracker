import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        // проверка работоспособности кода

        Manager inMemoryTaskManager = Managers.getDefault();


        Task task = new Task("Сходить в магазин", "Купить сыр,багет,помидоры, вино");
        Task task2 = new Task("Сходить в химчистку", "Отнести пальто и куртку");

        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(task2);

        int taskId = task.getId();
        int taskId2 = task2.getId();

        Epic epic = new Epic("Поменять резину на а/м",
                "Прочитать отзывы про резину, купить ее и поменять на шиномонтаже");
        inMemoryTaskManager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("Отзывы о резине", "Прочитать отзывы про зимнюю резину", epicId);
        Subtask subtask1 = new Subtask("Купить резину", "Выбрать конкретный магазин и купить резину", epicId);
        Subtask subtask2 = new Subtask("Шиномонтаж", "Выбрать ближайший шиномонтаж и поменять резину", epicId);

        inMemoryTaskManager.addSubtask(subtask);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Купить подарки", "Купить подарки близким на НГ");
        inMemoryTaskManager.addEpic(epic2);
        int epicId2 = epic2.getId();

        Subtask subtask3 = new Subtask("Подарки", "Купить подарки близким на НГ", epicId2);
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

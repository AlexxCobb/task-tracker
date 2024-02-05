package org.tasktracker.manager;

import org.tasktracker.manager.interfaces.HistoryManager;
import org.tasktracker.taskmodel.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File fileSaves;

    public FileBackedTasksManager(File fileSaves) {
        this.fileSaves = fileSaves;
    }

    // метод сохранения состояния менеджера в файл
    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter
                (new FileWriter(fileSaves, StandardCharsets.UTF_8))) {
            String heading = "id, type, name, status, description, epic";
            bufferedWriter.write(heading + "\n");

            for (Epic epic : epicTasks.values()) {
                bufferedWriter.write(epic.toCSV() + "\n");
            }
            for (Task task : tasks.values()) {
                bufferedWriter.write(task.toCSV() + "\n");
            }
            for (Subtask subtask : subTasks.values()) {
                bufferedWriter.write(subtask.toCSV() + "\n");
            }
            bufferedWriter.newLine();
            bufferedWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Сохранение данных не удалось", e.getCause());
        }
    }

    @Override
    public Task getTaskForId(int iD) {
        Task task = super.getTaskForId(iD);
        save();
        return task;
    }

    @Override
    public Epic getEpicForId(int iD) {
        Epic epic = super.getEpicForId(iD);
        save();
        return epic;
    }

    @Override
    public Subtask getSubTaskForId(int iD) {
        Subtask subtask = super.getSubTaskForId(iD);
        save();
        return subtask;
    }

    @Override
    public int addTask(Task task) {
        int Id = super.addTask(task);
        save();
        return Id;
    }

    @Override
    public int addEpic(Epic epic) {
        int Id = super.addEpic(epic);
        save();
        return Id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int Id = super.addSubtask(subtask);
        save();
        return Id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(Integer Id) {
        super.removeTask(Id);
        save();
    }

    @Override
    public void removeEpic(Integer Id) {
        super.removeEpic(Id);
        save();
    }

    @Override
    public void removeSubtask(Integer Id) {
        super.removeSubtask(Id);
        save();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        save();
        return history;
    }

    // метод получения ЗАДАЧИ из строки
    private Task fromString(String value) {
        String[] stringToTask = value.split(",");
        int ID = Integer.parseInt(stringToTask[0]);
        TypeOfTasks type = TypeOfTasks.valueOf(stringToTask[1]);
        String nameTask = stringToTask[2];
        Status status = Status.valueOf(stringToTask[3]);
        String description = stringToTask[4];

        switch (type) {
            case TASK:
                Task task = new Task(nameTask, description);
                task.setId(ID);
                task.setType(type);
                task.setStatusOfTask(status);
                return task;
            case EPIC:
                Epic epic = new Epic(nameTask, description);
                epic.setId(ID);
                epic.setType(type);
                epic.setStatusOfTask(status);
                return epic;
            case SUBTASK:
                int epicID = Integer.parseInt(stringToTask[5]);
                Subtask subtask = new Subtask(nameTask, description, epicID);
                subtask.setId(ID);
                subtask.setType(type);
                subtask.setStatusOfTask(status);
                return subtask;
            default:
                throw new IllegalStateException("Такой тип задач отсутствует" + type);
        }
    }

    // метод формирующий историю просмотров ЗАДАЧ в строку
    private static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < history.size(); i++) {
            sb.append(history.get(i).getId());
            if (i < history.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    // метод восстановления истории из строки
    private static List<Integer> historyFromString(String value) {
        List<Integer> historyTasks = new ArrayList<>();
        String[] historyString = value.split(",");
        for (String s : historyString) {
            int Id = Integer.parseInt(s);
            historyTasks.add(Id);
        }
        return historyTasks;
    }

    // метод восстановления ЗАДАЧ в менеджер
    private void saveTaskToManager(Task task) {
        if (task instanceof Epic) {
            epicTasks.put(task.getId(), (Epic) task);
        } else if (task instanceof Subtask) {
            ArrayList<Integer> subTaskIds = new ArrayList<>();
            subTaskIds.add(task.getId());
            epicTasks.get(((Subtask) task).getEpicId()).setSubTaskIds(subTaskIds);
            subTasks.put(task.getId(), (Subtask) task);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    // метод загрузки менеджера из файла
    private static FileBackedTasksManager loadFromFile(File file) throws IOException {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine();
            boolean isHistoryLine = false;

            while (br.ready()) {
                String line = br.readLine();
                if (isHistoryLine) {
                    List<Integer> hist = historyFromString(line);
                    for (Integer Id : hist) {
                        if (fileBackedTasksManager.tasks.containsKey(Id)) {
                            fileBackedTasksManager.historyManager.add(fileBackedTasksManager.tasks.get(Id));
                        } else if (fileBackedTasksManager.epicTasks.containsKey(Id)) {
                            fileBackedTasksManager.historyManager.add(fileBackedTasksManager.epicTasks.get(Id));
                        } else if (fileBackedTasksManager.subTasks.containsKey(Id)) {
                            fileBackedTasksManager.historyManager.add(fileBackedTasksManager.subTasks.get(Id));
                        }
                    }
                    continue;
                }
                if (!line.isBlank()) {
                    Task task = fileBackedTasksManager.fromString(line);
                    fileBackedTasksManager.saveTaskToManager(task);
                }
                if (line.isEmpty()) {
                    isHistoryLine = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Загрузка данных из файла не удалась");
            e.printStackTrace();
        }
        return fileBackedTasksManager;
    }


    // проверка работоспособности

    public static void main(String[] args) throws IOException {
        Path fileSave;
        try {
            fileSave = Files.createFile(Paths.get("save.csv"));
        } catch (FileAlreadyExistsException e) {
            Files.delete(Paths.get("save.csv"));
            fileSave = Files.createFile(Paths.get("save.csv"));
        }

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(fileSave.toFile());

        Task task = new Task("Сходить в магазин", "Купить сыр, багет, помидоры, вино");
        fileBackedTasksManager.addTask(task);

        Epic epic = new Epic("Поменять резину на а/м",
                "Прочитать отзывы про резину, купить ее и поменять на шиномонтаже");
        fileBackedTasksManager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask1 = new Subtask("Отзывы о резине", "Прочитать отзывы про зимнюю резину", epicId);
        Subtask subtask2 = new Subtask("Купить резину", "Выбрать конкретный магазин и купить резину", epicId);
        Subtask subtask3 = new Subtask("Шиномонтаж", "Выбрать ближайший шиномонтаж и поменять резину", epicId);

        fileBackedTasksManager.addSubtask(subtask1);
        fileBackedTasksManager.addSubtask(subtask2);
        fileBackedTasksManager.addSubtask(subtask3);

        fileBackedTasksManager.getTaskForId(task.getId());
        fileBackedTasksManager.getEpicForId(epic.getId());
        fileBackedTasksManager.getTaskForId(task.getId());
        fileBackedTasksManager.getSubTaskForId(subtask1.getId());

        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(fileSave.toFile());
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager1.getHistory());
    }
}

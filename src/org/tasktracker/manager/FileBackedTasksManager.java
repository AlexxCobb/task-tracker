package org.tasktracker.manager;

import org.tasktracker.exception.ManagerRuntimeException;
import org.tasktracker.manager.interfaces.HistoryManager;
import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;
import org.tasktracker.model.enums.ParseIndexes;
import org.tasktracker.model.enums.Status;
import org.tasktracker.model.enums.TypeOfTasks;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.tasktracker.model.enums.ParseIndexes.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final String FILE_HEADER = "id, type, name, status, description, startTime, duration, epic";

    private File saveTasks;

    public FileBackedTasksManager(File saveTasks) {
        this.saveTasks = saveTasks;
    }

    public FileBackedTasksManager() {
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
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
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    // метод сохранения состояния менеджера в файл
    protected void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter
                (new FileWriter(saveTasks, StandardCharsets.UTF_8))) {

            bufferedWriter.write(FILE_HEADER + "\n");

            for (Epic epic : epicIdToEpicMap.values()) {
                bufferedWriter.write(epic.toCSV() + "\n");
            }
            for (Task task : taskIdToTaskMap.values()) {
                bufferedWriter.write(task.toCSV() + "\n");
            }
            for (Subtask subtask : subtaskIdToSubtaskMap.values()) {
                bufferedWriter.write(subtask.toCSV() + "\n");
            }
            bufferedWriter.newLine();
            bufferedWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerRuntimeException("Сохранение данных не удалось", e.getCause());
        }
    }

    // метод получения ЗАДАЧИ из строки
    private Task getTaskFromString(String value) {
        String[] stringToTask = value.split(",");
        int ID = Integer.parseInt(stringToTask[ID_INDEX.getIndex()]);
        TypeOfTasks type = TypeOfTasks.valueOf(stringToTask[TYPE_INDEX.getIndex()]);
        String nameTask = stringToTask[NAME_OF_TASK_INDEX.getIndex()];
        Status status = Status.valueOf(stringToTask[STATUS_INDEX.getIndex()]);
        String description = stringToTask[DESC_INDEX.getIndex()];
        LocalDateTime startTime = getStartTime(stringToTask);
        Duration duration = getDuration(stringToTask);

        switch (type) {
            case TASK:
                Task task = new Task(nameTask, description, startTime, duration);
                task.setId(ID);
                task.setType(type);
                task.setStatus(status);
                return task;
            case EPIC:
                LocalDateTime endTime = getEndTimeToEpic(stringToTask);
                Epic epic = new Epic(nameTask, description);
                epic.setId(ID);
                epic.setType(type);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                epic.setEndTime(endTime);
                return epic;
            case SUBTASK:
                int epicID = Integer.parseInt(stringToTask[7]);
                Subtask subtask = new Subtask(nameTask, description, epicID, startTime, duration);
                subtask.setId(ID);
                subtask.setType(type);
                subtask.setStatus(status);
                return subtask;
            default:
                throw new IllegalStateException("Такой тип задач отсутствует" + type);
        }
    }

    private LocalDateTime getStartTime(String[] stringToTask) {
        LocalDateTime startTime;
        if (!stringToTask[START_TIME_INDEX.getIndex()].equals("null")) {
            return startTime = LocalDateTime.parse(stringToTask[START_TIME_INDEX.getIndex()]);
        }
        return startTime = null;
    }

    private Duration getDuration(String[] stringToTask) {
        Duration duration;
        if (!stringToTask[DURATION_INDEX.getIndex()].equals("null")) {
            return duration = Duration.parse(stringToTask[DURATION_INDEX.getIndex()]);
        }
        return duration = null;
    }

    private LocalDateTime getEndTimeToEpic(String[] stringToTask) {
        LocalDateTime endTime;
        if (!stringToTask[7].equals("null")) {
            return endTime = LocalDateTime.parse(stringToTask[7]);
        }
        return endTime = null;
    }

    // метод формирующий историю просмотров ЗАДАЧ в строку
    private static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        if (!history.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Task task : history) {
                sb.append(task.getId());
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return "";
    }

    // метод восстановления истории из строки
    private static List<Integer> historyFromString(String value) {
        List<Integer> historyTasks = new ArrayList<>();
        String[] historyString = value.split(",");
        for (String s : historyString) {
            int id = Integer.parseInt(s);
            historyTasks.add(id);
        }
        return historyTasks;
    }

    // метод восстановления ЗАДАЧ в менеджер
    private void loadTaskToManager(Task task) {
        if (task instanceof Epic) {
            epicIdToEpicMap.put(task.getId(), (Epic) task);
        } else if (task instanceof Subtask) {
            epicIdToEpicMap.get(((Subtask) task).getEpicId()).getSubtaskIds().add(task.getId());
            subtaskIdToSubtaskMap.put(task.getId(), (Subtask) task);
        } else {
            taskIdToTaskMap.put(task.getId(), task);
        }
    }

    // метод загрузки менеджера из файла
    public static FileBackedTasksManager loadFromFile(File file) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine();
            boolean isHistoryLine = false;

            while (br.ready()) {
                var line = br.readLine();
                if (isHistoryLine) {
                    var history = historyFromString(line);
                    for (Integer taskId : history) {
                        if (fileBackedTasksManager.taskIdToTaskMap.containsKey(taskId)) {
                            fileBackedTasksManager.historyManager.add(fileBackedTasksManager.taskIdToTaskMap.get(taskId));
                        } else if (fileBackedTasksManager.epicIdToEpicMap.containsKey(taskId)) {
                            fileBackedTasksManager.historyManager.add(fileBackedTasksManager.epicIdToEpicMap.get(taskId));
                        } else if (fileBackedTasksManager.subtaskIdToSubtaskMap.containsKey(taskId)) {
                            fileBackedTasksManager.historyManager.add(fileBackedTasksManager.subtaskIdToSubtaskMap.get(taskId));
                        }
                    }
                    continue;
                }
                if (!line.isEmpty()) {
                    Task task = fileBackedTasksManager.getTaskFromString(line);
                    fileBackedTasksManager.loadTaskToManager(task);
                }
                if (line.isBlank()) {
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
        Path saveTasks;
        try {
            saveTasks = Files.createFile(Paths.get("save.csv"));
        } catch (FileAlreadyExistsException e) {
            Files.delete(Paths.get("save.csv"));
            saveTasks = Files.createFile(Paths.get("save.csv"));
        }

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(saveTasks.toFile());

        Task task = new Task("Сходить в магазин", "Купить сыр, багет, помидоры, вино", LocalDateTime.of(2025, 2, 2, 12, 0), Duration.ofMinutes(30));
        fileBackedTasksManager.addTask(task);

        Epic epic = new Epic("Поменять резину на а/м",
                "Прочитать отзывы про резину, купить ее и поменять на шиномонтаже");
        fileBackedTasksManager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask1 = new Subtask("Отзывы о резине", "Прочитать отзывы про зимнюю резину", epicId, LocalDateTime.of(2025, 2, 3, 12, 0), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Купить резину", "Выбрать конкретный магазин и купить резину", epicId, LocalDateTime.of(2025, 2, 3, 13, 0), Duration.ofMinutes(30));
        Subtask subtask3 = new Subtask("Шиномонтаж", "Выбрать ближайший шиномонтаж и поменять резину", epicId, LocalDateTime.of(2025, 2, 5, 14, 0), Duration.ofMinutes(30));

        fileBackedTasksManager.addSubtask(subtask1);
        fileBackedTasksManager.addSubtask(subtask2);
        fileBackedTasksManager.addSubtask(subtask3);

        fileBackedTasksManager.getTaskById(task.getId());
        fileBackedTasksManager.getEpicById(epic.getId());
        fileBackedTasksManager.getTaskById(task.getId());
        fileBackedTasksManager.getSubtaskById(subtask1.getId());

        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(saveTasks.toFile());
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager1.getHistory());
    }
}

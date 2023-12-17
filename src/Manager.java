import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private int nextId = 1;

    // получение списка всех задач
    public ArrayList<Task> getTasks() {
        ArrayList<Task> nameOfTask = new ArrayList<>();
        for (Integer integer : tasks.keySet()) {
            nameOfTask.add(tasks.get(integer));
        }
        return nameOfTask;
    }

    public ArrayList<Epic> getEpicTasks() {
        ArrayList<Epic> nameOfEpic = new ArrayList<>();
        for (Integer integer : epicTasks.keySet()) {
            nameOfEpic.add(epicTasks.get(integer));
        }
        return nameOfEpic;
    }

    public ArrayList<Subtask> getSubTasks() {
        ArrayList<Subtask> nameOfSubTask = new ArrayList<>();
        for (Integer integer : subTasks.keySet()) {
            nameOfSubTask.add(subTasks.get(integer));
        }
        return nameOfSubTask;
    }


    // удаление всех задач
    public void clearAllTask() {
        tasks.clear();
    }

    public void clearAllEpic() {
        epicTasks.clear();
    }

    public void clearAllSubTask() {
        subTasks.clear();
    }


    // получение по идентификатору
    public Task getTaskForId(int iD) {
        return tasks.get(iD);
    }
    public Epic getEpicForId(int iD) {
        return epicTasks.get(iD);
    }

    public Subtask getSubTaskForId(int iD) {
        return subTasks.get(iD);
    }


    // создание задачи, передача объекта в качестве параметра
    public int addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task.getId();
    }


    public int addEpic(Epic epic) {
        epic.setId(nextId++);
        epicTasks.put(epic.getId(), epic);
        updateStatusEpic(epic);
        return epic.getId();
    }

    public int addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subTasks.put(subtask.getId(), subtask);

        epicTasks.get(subtask.getEpicId()).getSubTaskIds().add(subtask.getId());
        updateStatusEpic(epicTasks.get(subtask.getEpicId()));

        return subtask.getId();
    }


    // обновление задачи
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epicTasks.put(epic.getId(), epic);
        updateStatusEpic(epic);
    }
    public void updateSubtask(Subtask subtask) {
        subTasks.put(subtask.getId(), subtask);
        epicTasks.get(subtask.getEpicId()).getSubTaskIds().add(subtask.getId());
        updateStatusEpic(epicTasks.get(subtask.getEpicId()));
    }

    // удаление по идентификатору
    void removeTask(Integer Id) {
        tasks.remove(Id);
    }

    void removeEpic(Integer Id) {
        epicTasks.remove(Id);
    }
    void removeSubtask(Integer Id) {
        subTasks.remove(Id);
    }


    //получение списка подзадач определенного эпика
    ArrayList<Subtask> getSubTaskOfEpic(Epic epic) {
        ArrayList<Subtask> subTask = new ArrayList<>();
        for (Integer subTaskId : epic.getSubTaskIds()) {
            subTask.add(subTasks.get(subTaskId));
        }
        return subTask;
    }

    // проверка статуса epic
    private void updateStatusEpic(Epic epic) {
        int countNew = 0;
        int countInProgress = 0;
        int countDone = 0;
        for (Integer subTaskId : epic.getSubTaskIds()) {
            Subtask subtask = subTasks.get(subTaskId);
            if (subtask != null) {
                switch (subtask.getStatusOfTask()) {
                    case "NEW":
                        countNew++;
                        break;
                    case "IN_PROGRESS":
                        countInProgress++;
                        break;
                    case "DONE":
                        countDone++;
                        break;
                    default:
                        break;
                }
            } else {
                epic.setStatusOfTask("NEW");
            }
        }
        if (countNew >= 0 && countInProgress == 0 && countDone == 0) {
            epic.setStatusOfTask("NEW");
        } else if (countDone > 0 && countNew == 0 && countInProgress == 0) {
            epic.setStatusOfTask("DONE");
        } else {
            epic.setStatusOfTask("IN_PROGRESS");
        }
    }
}

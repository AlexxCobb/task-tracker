package org.tasktracker.manager.memorymanager;

import org.tasktracker.manager.Managers;
import org.tasktracker.manager.interfaces.HistoryManager;
import org.tasktracker.manager.interfaces.Manager;
import org.tasktracker.taskmodel.Epic;
import org.tasktracker.taskmodel.Status;
import org.tasktracker.taskmodel.Subtask;
import org.tasktracker.taskmodel.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements Manager {

    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epicTasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private int nextId = 1;


    // получение списка всех задач
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // удаление всех задач
    @Override
    public void clearAllTask() {
        tasks.clear();
    }

    @Override
    public void clearAllEpic() {
        for (Epic epic : epicTasks.values()) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
        }
        epicTasks.clear();
    }

    @Override
    public void clearAllSubTask() {
        for (Subtask subtask : subTasks.values()) {
            epicTasks.get(subtask.getEpicId()).getSubTaskIds().clear();
        }
        subTasks.clear();
    }


    // получение по идентификатору
    @Override
    public Task getTaskForId(int iD) {
        historyManager.add(tasks.get(iD));
        return tasks.get(iD);
    }

    @Override
    public Epic getEpicForId(int iD) {
        historyManager.add(epicTasks.get(iD));
        return epicTasks.get(iD);
    }

    @Override
    public Subtask getSubTaskForId(int iD) {
        historyManager.add(subTasks.get(iD));
        return subTasks.get(iD);
    }


    // создание задачи, передача объекта в качестве параметра
    @Override
    public int addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(nextId++);
        epicTasks.put(epic.getId(), epic);
        updateStatusEpic(epic);
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subTasks.put(subtask.getId(), subtask);

        epicTasks.get(subtask.getEpicId()).getSubTaskIds().add(subtask.getId());
        updateStatusEpic(epicTasks.get(subtask.getEpicId()));

        return subtask.getId();
    }


    // обновление задачи
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicTasks.put(epic.getId(), epic);
        updateStatusEpic(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subTasks.put(subtask.getId(), subtask);
        epicTasks.get(subtask.getEpicId()).getSubTaskIds().add(subtask.getId());
        updateStatusEpic(epicTasks.get(subtask.getEpicId()));
    }

    // удаление по идентификатору
    @Override
    public void removeTask(Integer Id) {
        tasks.remove(Id);
        historyManager.remove(Id);
    }

    @Override
    public void removeEpic(Integer Id) {
        for (Integer subTaskId : epicTasks.get(Id).getSubTaskIds()) {
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
        epicTasks.remove(Id);
        historyManager.remove(Id);
    }

    @Override
    public void removeSubtask(Integer Id) {
        epicTasks.get(subTasks.get(Id).getEpicId()).getSubTaskIds().remove(Id);
        subTasks.remove(Id);
        historyManager.remove(Id);
    }


    //получение списка подзадач определенного эпика
    @Override
    public ArrayList<Subtask> getSubTaskOfEpic(Epic epic) {
        ArrayList<Subtask> subTask = new ArrayList<>();
        for (Integer subTaskId : epic.getSubTaskIds()) {
            subTask.add(subTasks.get(subTaskId));
        }
        return subTask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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
                    case NEW:
                        countNew++;
                        break;
                    case IN_PROGRESS:
                        countInProgress++;
                        break;
                    case DONE:
                        countDone++;
                        break;
                    default:
                        break;
                }
            } else {
                epic.setStatusOfTask(Status.NEW);
            }
        }
        if (countNew >= 0 && countInProgress == 0 && countDone == 0) {
            epic.setStatusOfTask(Status.NEW);
        } else if (countDone > 0 && countNew == 0 && countInProgress == 0) {
            epic.setStatusOfTask(Status.DONE);
        } else {
            epic.setStatusOfTask(Status.IN_PROGRESS);
        }
    }
}

package org.tasktracker.manager;

import org.tasktracker.util.Managers;
import org.tasktracker.manager.interfaces.HistoryManager;
import org.tasktracker.manager.interfaces.Manager;
import org.tasktracker.model.Epic;
import org.tasktracker.model.enums.Status;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements Manager {


    private int nextId = 1;
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected HashMap<Integer, Task> taskIdToTaskMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicIdToEpicMap = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskIdToSubtaskMap = new HashMap<>();
    protected static Set<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));


    // создание задачи, передача объекта в качестве параметра
    @Override
    public int addTask(Task task) {
        task.setId(nextId++);
        taskIdToTaskMap.put(task.getId(), task);
        if (validateToStartTime(task)) {
            sortedTasks.add(task);
        }
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(nextId++);
        epicIdToEpicMap.put(epic.getId(), epic);
        updateEpicStatus(epic);
        setStartTimeAndEndTimeAndDurationToEpic(epic);
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int subtaskId = nextId++;
        subtask.setId(subtaskId);
        subtaskIdToSubtaskMap.put(subtaskId, subtask);
        var epic = epicIdToEpicMap.get(subtask.getEpicId());
        var subTaskIds = epic.getSubtaskIds();
        subTaskIds.add(subtaskId);
        setStartTimeAndEndTimeAndDurationToEpic(epic);
        updateEpicStatus(epic);
        if (validateToStartTime(subtask)) {
            sortedTasks.add(subtask);
        }
        return subtaskId;
    }

    // обновление задачи
    @Override
    public void updateTask(Task task) {
        taskIdToTaskMap.put(task.getId(), task);
        if (validateToStartTime(task)) {
            sortedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epicIdToEpicMap.put(epic.getId(), epic);
        updateEpicStatus(epic);
        setStartTimeAndEndTimeAndDurationToEpic(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtaskIdToSubtaskMap.put(subtask.getId(), subtask);
        if (validateToStartTime(subtask)) {
            sortedTasks.add(subtask);
        }
        updateEpicStatus(epicIdToEpicMap.get(subtask.getEpicId()));
        setStartTimeAndEndTimeAndDurationToEpic(epicIdToEpicMap.get(subtask.getEpicId()));
    }

    // получение по идентификатору
    @Override
    public Task getTaskById(int id) {
        var task = taskIdToTaskMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        var epic = epicIdToEpicMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        var subtask = subtaskIdToSubtaskMap.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    // получение списка всех задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(taskIdToTaskMap.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicIdToEpicMap.values());
    }

    @Override
    public List<Subtask> getSubTasks() {
        return new ArrayList<>(subtaskIdToSubtaskMap.values());
    }


    //получение списка подзадач определенного эпика
    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> subTask = new ArrayList<>();
        for (Integer subTaskId : epic.getSubtaskIds()) {
            subTask.add(subtaskIdToSubtaskMap.get(subTaskId));
        }
        return subTask;
    }

    // получение истории списка задач, к которым обращались
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // удаление по идентификатору
    @Override
    public void removeTaskById(Integer id) {
        taskIdToTaskMap.remove(id);
        sortedTasks.remove(taskIdToTaskMap.get(id));
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(Integer id) {
        for (Integer subTaskId : epicIdToEpicMap.get(id).getSubtaskIds()) {
            subtaskIdToSubtaskMap.remove(subTaskId);
            sortedTasks.remove(subtaskIdToSubtaskMap.get(subTaskId));
            historyManager.remove(subTaskId);
        }
        epicIdToEpicMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(Integer id) {
        Epic epic = epicIdToEpicMap.get(subtaskIdToSubtaskMap.get(id).getEpicId());
        epic.getSubtaskIds().remove(id);
        updateEpicStatus(epic);
        setStartTimeAndEndTimeAndDurationToEpic(epic);
        subtaskIdToSubtaskMap.remove(id);
        sortedTasks.remove(subtaskIdToSubtaskMap.get(id));
        historyManager.remove(id);
    }

    // удаление всех задач
    @Override
    public void clearAllTasks() {
        for (Task value : taskIdToTaskMap.values()) {
            
        }
        taskIdToTaskMap.clear();
        
    }

    @Override
    public void clearAllEpics() {
        for (Epic epic : epicIdToEpicMap.values()) {
            for (Integer subTaskId : epic.getSubtaskIds()) {
                subtaskIdToSubtaskMap.remove(subTaskId);
            }
        }
        epicIdToEpicMap.clear();
    }

    @Override
    public void clearAllSubtasks() {
        for (Subtask subtask : subtaskIdToSubtaskMap.values()) {
            var epic = epicIdToEpicMap.get(subtask.getEpicId());
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
            setStartTimeAndEndTimeAndDurationToEpic(epic);
        }
        subtaskIdToSubtaskMap.clear();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<Task>(sortedTasks);
    }

    private boolean validateToStartTime(Task task) {
        List<Task> tasksSort = getPrioritizedTasks();
        if (tasksSort.isEmpty()) {
            return true;
        }
        for (Task sortTask : tasksSort) {
            if (sortTask.getStartTime() != null && task.getStartTime() != null) {
                if (task.getId() == sortTask.getId()) {
                    continue;
                }
                if (((task.getStartTime().isBefore(sortTask.getEndTime()))
                        || (task.getStartTime().equals(sortTask.getEndTime())))
                        && ((task.getEndTime().isAfter(sortTask.getStartTime())
                        || task.getEndTime().equals(sortTask.getStartTime())))) {
                    return false;

                }
            }
        }
        return true;
    }

    // проверка статуса epic
    private void updateEpicStatus(Epic epic) {
        int countNew = 0;
        int countInProgress = 0;
        int countDone = 0;
        for (Integer subTaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtaskIdToSubtaskMap.get(subTaskId);
            if (subtask != null) {
                switch (subtask.getStatus()) {
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
                epic.setStatus(Status.NEW);
            }
        }
        if (countNew >= 0 && countInProgress == 0 && countDone == 0) {
            epic.setStatus(Status.NEW);
        } else if (countDone > 0 && countNew == 0 && countInProgress == 0) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    // расчет времени и продолжительности epic
    private void setStartTimeAndEndTimeAndDurationToEpic(Epic epic) {
        if (epic.getSubtaskIds().size() != 0) {
            setStartTimeToEpic(epic);
            setDurationToEpic(epic);
            setEndTimeToEpic(epic);
        }
    }

    private void setStartTimeToEpic(Epic epic) {
        LocalDateTime start = epic.getSubtaskIds().stream()
                .map(subtask -> subtaskIdToSubtaskMap.get(subtask))
                .min(Comparator.comparing(Subtask::getStartTime))
                .orElseThrow().getStartTime();
        epic.setStartTime(start);
    }

    private void setDurationToEpic(Epic epic) {
        Duration durationOfEpic = Duration.ofMinutes(0);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Duration duration = subtaskIdToSubtaskMap.get(subtaskId).getDuration();
            durationOfEpic = durationOfEpic.plus(duration);
        }
        epic.setDuration(durationOfEpic);
    }

    private void setEndTimeToEpic(Epic epic) {
        LocalDateTime end = epic.getSubtaskIds().stream()
                .map(subtask -> subtaskIdToSubtaskMap.get(subtask))
                .max(Comparator.comparing(Task::getEndTime))
                .orElseThrow().getEndTime();
        epic.setEndTime(end);
    }


}



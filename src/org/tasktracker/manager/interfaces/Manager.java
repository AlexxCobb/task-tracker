package org.tasktracker.manager.interfaces;

import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;

import java.util.List;
import java.util.Set;

public interface Manager {


    // получение списка всех задач
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubTasks();


    // удаление всех задач
    void clearAllTasks();

    void clearAllEpics();

    void clearAllSubtasks();


    // получение по идентификатору
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);


    // создание задачи, передача объекта в качестве параметра
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);


    // обновление задачи
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);


    // удаление по идентификатору
    void removeTaskById(Integer id);

    void removeEpicById(Integer id);

    void removeSubtaskById(Integer id);

    //получение списка подзадач определенного эпика
    List<Subtask> getEpicSubtasks(Epic epic);

    public List<Task> getHistory();

   List<Task> getPrioritizedTasks();
}

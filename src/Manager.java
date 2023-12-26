import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Manager {


    // получение списка всех задач
    List<Task> getTasks();

    List<Epic> getEpicTasks();

    List<Subtask> getSubTasks();


    // удаление всех задач
    void clearAllTask();

    void clearAllEpic();

    void clearAllSubTask();


    // получение по идентификатору
    Task getTaskForId(int iD);

    Epic getEpicForId(int iD);

    Subtask getSubTaskForId(int iD);


    // создание задачи, передача объекта в качестве параметра
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);


    // обновление задачи
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);


    // удаление по идентификатору
    void removeTask(Integer Id);

    void removeEpic(Integer Id);

    void removeSubtask(Integer Id);


    //получение списка подзадач определенного эпика
    ArrayList<Subtask> getSubTaskOfEpic(Epic epic);

    public List<Task> getHistory();
}

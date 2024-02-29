
import com.google.gson.Gson;
import org.tasktracker.manager.HttpTaskManager;
import org.tasktracker.manager.interfaces.Manager;
import org.tasktracker.server.HttpTaskServer;
import org.tasktracker.server.KVServer;
import org.tasktracker.server.KVTaskClient;
import org.tasktracker.util.Managers;
import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Gson gson = new Gson();
        KVServer kvServer = new KVServer();
        kvServer.start();

        HttpTaskManager manager = Managers.getDefaultManager();

        Task task = new Task("name", "details", LocalDateTime.of(2000, 2, 20, 20, 2), Duration.ofMinutes(20));
        manager.addTask(task);
        Epic epic = new Epic("name", "details");
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("name", "details", epicId, LocalDateTime.of(2020, 2, 20, 2, 2), Duration.ofMinutes(30));
        manager.addSubtask(subtask);

        manager.getTaskById(task.getId());
        manager.getEpicById(epicId);
        manager.getSubtaskById(subtask.getId());

        System.out.println(gson.toJson(manager.getTasks()));
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
        System.out.println(manager.getHistory());
        System.out.println();

        kvServer.stop();
    }
}

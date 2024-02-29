package org.tasktracker.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;
import org.tasktracker.server.KVTaskClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson;
    private final String url;

    public KVTaskClient getClient() {
        return client;
    }

    public HttpTaskManager(String url) {
        super();
        this.url = url;
        this.client = new KVTaskClient(url);
        this.gson = new Gson();
    }

    public String getUrl() {
        return url;
    }

    @Override
    protected void save() {
        String tasksToJson = gson.toJson(taskIdToTaskMap.values());
        String epicsToJson = gson.toJson(epicIdToEpicMap.values());
        String subtasksToJson = gson.toJson(subtaskIdToSubtaskMap.values());
        String historyToJson = gson.toJson(getHistory().stream().map(Task::getId).collect(Collectors.toList()));

        client.put("tasks", tasksToJson);
        client.put("epics", epicsToJson);
        client.put("subtasks", subtasksToJson);
        client.put("history", historyToJson);
    }

    public HttpTaskManager load() {
        HttpTaskManager taskManager = new HttpTaskManager(url);
        JsonElement tasks = JsonParser.parseString(client.load("tasks"));
        if (!tasks.isJsonNull()) {
            JsonArray tasksAsArray = tasks.getAsJsonArray();
            for (JsonElement taskElement : tasksAsArray) {
                Task task = gson.fromJson(taskElement, Task.class);
                taskManager.taskIdToTaskMap.put(task.getId(), taskIdToTaskMap.get(task.getId()));
            }
        }
        JsonElement epics = JsonParser.parseString(client.load("epics"));
        if (!epics.isJsonNull()) {
            JsonArray epicsAsArray = epics.getAsJsonArray();
            for (JsonElement epicElement : epicsAsArray) {
                Epic epic = gson.fromJson(epicElement, Epic.class);
                taskManager.epicIdToEpicMap.put(epic.getId(), epicIdToEpicMap.get(epic.getId()));
            }
        }
        JsonElement subtasks = JsonParser.parseString(client.load("subtasks"));
        if (!subtasks.isJsonNull()) {
            JsonArray subtasksAsArray = subtasks.getAsJsonArray();
            for (JsonElement subtaskElement : subtasksAsArray) {
                Subtask subtask = gson.fromJson(subtaskElement, Subtask.class);
                taskManager.subtaskIdToSubtaskMap.put(subtask.getId(), subtaskIdToSubtaskMap.get(subtask.getId()));
            }
        }
        JsonElement historyJson = JsonParser.parseString(client.load("history"));
        if (!historyJson.isJsonNull()) {
            JsonArray historyAsArray = historyJson.getAsJsonArray();
            for (JsonElement historyOfTask : historyAsArray) {
                int taskId = historyOfTask.getAsInt();
                if (taskIdToTaskMap.containsKey(taskId)) {
                    taskManager.getTaskById(taskId);
                } else if (epicIdToEpicMap.containsKey(taskId)) {
                    taskManager.getEpicById(taskId);
                } else if (subtaskIdToSubtaskMap.containsKey(taskId)) {
                    taskManager.getSubtaskById(taskId);
                }
            }
        }
        return taskManager;
    }
}



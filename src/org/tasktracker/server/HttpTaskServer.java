package org.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.tasktracker.manager.FileBackedTasksManager;
import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;
import org.tasktracker.model.enums.Endpoint;
import org.tasktracker.util.Managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private static final int PORT = 8080;
    private final String PATH = "/tasks";
    private static final Gson gson = new Gson();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    static FileBackedTasksManager manager = Managers.getDefaultFileBackedManager();

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext(PATH, new TaskHandler());
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        httpServer.start();
    }

    private static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                Endpoint endpoint = getEndpoint(path, method);

                switch (endpoint) {
                    case GET_TASKS:
                        handleGetTasks(exchange);
                        break;
                    case GET_EPICS:
                        handleGetEpics(exchange);
                        break;
                    case GET_SUBTASKS:
                        handleGetSubtasks(exchange);
                        break;
                    case GET_TASK_BY_ID:
                        handleGetTaskById(exchange);
                        break;
                    case GET_EPIC_BY_ID:
                        handleGetEpicById(exchange);
                        break;
                    case GET_SUBTASK_BY_ID:
                        handleGetSubtaskById(exchange);
                        break;
                    case GET_PRIORITIZED_TASKS:
                        handleGetPrioritizedTasks(exchange);
                        break;
                    case GET_HISTORY:
                        handleGetHistory(exchange);
                        break;
                    case GET_EPIC_SUBTASKS:
                        handleGetEpicSubtasksById(exchange);
                        break;
                    case POST_TASK:
                        handlePostTask(exchange);
                        break;
                    case POST_EPIC:
                        handlePostEpic(exchange);
                        break;
                    case POST_SUBTASK:
                        handlePostSubtask(exchange);
                        break;
                    case DELETE_TASK_BY_ID:
                        handleDeleteTaskById(exchange);
                        break;
                    case DELETE_EPIC_BY_ID:
                        handleDeleteEpicById(exchange);
                        break;
                    case DELETE_SUBTASK_BY_ID:
                        handleDeleteSubtaskById(exchange);
                        break;
                    case DELETE_ALL_TASKS:
                        handleDeleteAllTasks(exchange);
                        break;
                    case DELETE_ALL_EPICS:
                        handleDeleteAllEpics(exchange);
                        break;
                    case DELETE_ALL_SUBTASKS:
                        handleDeleteAllSubtasks(exchange);
                        break;
                    case UNKNOWN:
                        writeResponse(exchange, "Такого эндпоинта не существует", 404);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }

        private Endpoint getEndpoint(String requestPath, String requestMethod) {
            String[] pathParts = requestPath.split("/");

            switch (requestMethod) {
                case "GET":
                    if (pathParts.length == 2) {
                        return Endpoint.GET_PRIORITIZED_TASKS;
                    } else if (pathParts.length == 3) {
                        switch (pathParts[2]) {
                            case "task":
                                return Endpoint.GET_TASKS;
                            case "epic":
                                return Endpoint.GET_EPICS;
                            case "subtask":
                                return Endpoint.GET_SUBTASKS;
                            case "history":
                                return Endpoint.GET_HISTORY;
                        }
                    } else if (pathParts.length == 4) {
                        switch (pathParts[2]) {
                            case "task":
                                return Endpoint.GET_TASK_BY_ID;
                            case "epic":
                                return Endpoint.GET_EPIC_BY_ID;
                            case "subtask":
                                return Endpoint.GET_SUBTASK_BY_ID;
                        }
                    } else if (pathParts.length == 5 && pathParts[3].equals("epic")) {
                        return Endpoint.GET_EPIC_SUBTASKS;
                    }
                    break;
                case "POST":
                    switch (pathParts[2]) {
                        case "task":
                            return Endpoint.POST_TASK;
                        case "epic":
                            return Endpoint.POST_EPIC;
                        case "subtask":
                            return Endpoint.POST_SUBTASK;
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        switch (pathParts[2]) {
                            case "task":
                                return Endpoint.DELETE_ALL_TASKS;
                            case "epic":
                                return Endpoint.DELETE_ALL_EPICS;
                            case "subtask":
                                return Endpoint.DELETE_ALL_SUBTASKS;
                        }
                    } else if (pathParts.length == 4) {
                        switch (pathParts[2]) {
                            case "task":
                                return Endpoint.DELETE_TASK_BY_ID;
                            case "epic":
                                return Endpoint.DELETE_EPIC_BY_ID;
                            case "subtask":
                                return Endpoint.DELETE_SUBTASK_BY_ID;
                        }
                    }
                    break;
            }
            return Endpoint.UNKNOWN;
        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(manager.getTasks()), 200);
        }

        private void handleGetEpics(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(manager.getEpics()), 200);
        }

        private void handleGetSubtasks(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(manager.getSubTasks()), 200);
        }

        private void handleGetTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> postIdOpt = getPostId(exchange);
            if (postIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор поста", 400);
                return;
            }
            int taskId = postIdOpt.get();
            writeResponse(exchange, gson.toJson(manager.getTaskById(taskId)), 200);
        }

        private void handleGetEpicById(HttpExchange exchange) throws IOException {
            Optional<Integer> postIdOpt = getPostId(exchange);
            if (postIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор поста", 400);
                return;
            }
            int epicId = postIdOpt.get();
            writeResponse(exchange, gson.toJson(manager.getEpicById(epicId)), 200);
        }

        private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> postIdOpt = getPostId(exchange);
            if (postIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор поста", 400);
                return;
            }
            int subtaskId = postIdOpt.get();
            writeResponse(exchange, gson.toJson(manager.getSubtaskById(subtaskId)), 200);
        }

        private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(manager.getHistory()), 200);
        }

        private void handleGetEpicSubtasksById(HttpExchange exchange) throws IOException {
            Optional<Integer> postIdOpt = getPostId(exchange);
            if (postIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор поста", 400);
                return;
            }
            int epicId = postIdOpt.get();
            writeResponse(exchange, gson.toJson(manager.getEpicSubtasks(manager.getEpicById(epicId))), 200);
        }

        private void handlePostTask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Task task;
            try {
                task = gson.fromJson(body, Task.class);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорректный JSON", 400);
                return;
            }
            Task existTask = manager.getTaskById(task.getId());
            if (existTask == null) {
                manager.addTask(task);
                writeResponse(exchange, "Задача успешно добавлена", 200);
            } else {
                manager.updateTask(task);
                writeResponse(exchange, "Задача успешно обновлена ", 201);
            }
        }

        private void handlePostEpic(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Epic epic;
            try {
                epic = gson.fromJson(body, Epic.class);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорректный JSON", 400);
                return;
            }
            if (manager.getEpicById(epic.getId()) != null) {
                manager.updateEpic(epic);
                writeResponse(exchange, "Эпик задача успешно обновлена", 201);
            } else {
                manager.addEpic(epic);
                writeResponse(exchange, "Эпик задача успешно добавлена", 200);
            }
        }

        private void handlePostSubtask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask;
            try {
                subtask = gson.fromJson(body, Subtask.class);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорректный JSON", 400);
                return;
            }
            if (manager.getSubtaskById(subtask.getId()) != null) {
                manager.updateSubtask(subtask);
                writeResponse(exchange, "Подзадача успешно обновлена", 201);
            } else {
                manager.addSubtask(subtask);
                writeResponse(exchange, "Подзадача успешно добавлена", 200);
            }
        }

        private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> postIdOpt = getPostId(exchange);
            if (postIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор поста", 400);
                return;
            }
            int taskId = postIdOpt.get();
            if (manager.getTaskById(taskId) != null) {
                manager.removeTaskById(taskId);
                writeResponse(exchange, "Задача успешно удалена", 204);
            } else {
                writeResponse(exchange, "Задача с таким id не найдена", 404);
            }
        }

        private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> postIdOpt = getPostId(exchange);
            if (postIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор поста", 400);
                return;
            }
            int subtaskId = postIdOpt.get();
            if (manager.getSubtaskById(subtaskId) != null) {
                manager.removeSubtaskById(subtaskId);
                writeResponse(exchange, "Подзадача успешно удалена", 204);
            } else {
                writeResponse(exchange, "Подзадача с таким id не найдена", 404);
            }
        }

        private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
            Optional<Integer> postIdOpt = getPostId(exchange);
            if (postIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор поста", 400);
                return;
            }
            int epicId = postIdOpt.get();
            if (manager.getEpicById(epicId) != null) {
                manager.removeEpicById(epicId);
                writeResponse(exchange, "Эпик задача успешно удалена", 204);
            } else {
                writeResponse(exchange, "Эпик задача с таким id не найдена", 404);
            }
        }

        private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
            manager.clearAllTasks();
            writeResponse(exchange, "Все задачи успешно удалены", 204);
        }

        private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
            manager.clearAllEpics();
            writeResponse(exchange, "Все эпик задачи успешно удалены", 204);
        }

        private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
            manager.clearAllSubtasks();
            writeResponse(exchange, "Все подзадачи успешно удалены", 204);
        }

        private Optional<Integer> getPostId(HttpExchange exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            try {
                return Optional.of(Integer.parseInt(pathParts[3]));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }

        private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
    }
}




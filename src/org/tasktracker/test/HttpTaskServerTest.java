package org.tasktracker.test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tasktracker.manager.HttpTaskManager;
import org.tasktracker.model.Epic;
import org.tasktracker.model.Subtask;
import org.tasktracker.model.Task;
import org.tasktracker.server.HttpTaskServer;
import org.tasktracker.server.KVServer;
import org.tasktracker.util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {

    private HttpTaskServer taskServer;
    private KVServer kvServer;
    private HttpClient client;
    private Gson gson;

    private static final String TASK_URI = "http://localhost:8080/tasks/task/";
    private static final String EPIC_URI = "http://localhost:8080/tasks/epic/";
    private static final String SUBTASK_URI = "http://localhost:8080/tasks/subtask/";
    private static final String HISTORY_URI = "http://localhost:8080/tasks/history/";

    @BeforeEach
    public void setUp() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    void whenRequestAllTasksThenGetAllTasks() throws IOException, InterruptedException {
        URI url = URI.create(TASK_URI);

        Task task = new Task("Сходить в магазин", "Купить сыр, багет, помидоры, вино", LocalDateTime.of(2025, 2, 2, 12, 0), Duration.ofMinutes(30));
        task.setId(1);
        String jsonOfTask1 = gson.toJson(task);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();

        Task task1 = new Task("Сходить в магазин", "Купить сыр, багет, помидоры, вино", LocalDateTime.of(2025, 2, 2, 12, 0), Duration.ofMinutes(30));
        task1.setId(2);
        String jsonOfTask2 = gson.toJson(task1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();

        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(jsonOfTask1 + jsonOfTask2, response.body());

        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenRequestAllEpicsThenGetAllEpics() {
        URI url = URI.create(EPIC_URI);

        Epic epic1 = new Epic("name", "details");
        epic1.setId(1);
        String jsonOfTask1 = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();

        Epic epic2 = new Epic("name", "details");
        epic2.setId(2);
        String jsonOfTask2 = gson.toJson(epic2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(jsonOfTask1 + jsonOfTask2, response.body());

        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenRequestAllSubtasksThenGetAllSubtasks() {
        URI url = URI.create(EPIC_URI);

        Subtask subtask1 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 20, 2, 2), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask3 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 22, 2, 2), Duration.ofMinutes(30));
        String jsonOfTask1 = gson.toJson(subtask1);
        String jsonOfTask2 = gson.toJson(subtask2);
        String jsonOfTask3 = gson.toJson(subtask3);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(jsonOfTask3);

        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).POST(body3).build();
        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            client.send(request3, HttpResponse.BodyHandlers.ofString());
            HttpRequest request4 = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request4, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(jsonOfTask1 + jsonOfTask2 + jsonOfTask3, response.body());

        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenRequestGetTaskByIdThenGetCorrectTask() {
        URI url = URI.create(TASK_URI + "?id=1");

        Task task = new Task("Сходить в магазин", "Купить сыр, багет, помидоры, вино", LocalDateTime.of(2025, 2, 2, 12, 0), Duration.ofMinutes(30));
        task.setId(1);
        String jsonOfTask1 = gson.toJson(task);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();

        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(jsonOfTask1, response.body());

        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenRequestGetEpicByIdThenGetCorrectEpic() {
        URI url = URI.create(EPIC_URI + "?id=1");

        Epic epic1 = new Epic("name", "details");
        epic1.setId(1);
        String jsonOfTask1 = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();

        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(jsonOfTask1, response.body());

        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenRequestGetSubtaskByIdThenGetCorrectSubtask() {
        URI url = URI.create(SUBTASK_URI + "?id=1");

        Subtask subtask1 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 20, 2, 2), Duration.ofMinutes(30));
        subtask1.setId(1);
        String jsonOfTask1 = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();

        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(jsonOfTask1, response.body());

        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenRequestPrioritizedTasksThenGetCorrectTasks() {
        URI url = URI.create("http://localhost:8080/tasks/");

        Subtask subtask1 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 21, 2, 2), Duration.ofMinutes(30));
        Subtask subtask3 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 22, 2, 2), Duration.ofMinutes(30));
        String jsonOfTask1 = gson.toJson(subtask1);
        String jsonOfTask2 = gson.toJson(subtask2);
        String jsonOfTask3 = gson.toJson(subtask3);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(jsonOfTask3);

        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).POST(body3).build();
        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            client.send(request3, HttpResponse.BodyHandlers.ofString());
            HttpRequest request4 = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request4, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(jsonOfTask1 + jsonOfTask3, response.body());

        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenRequestHistoryThenGetCorrectHistory() {
        URI url1 = URI.create(HISTORY_URI);
        URI url2 = URI.create(SUBTASK_URI + "?id=1");
        URI url3 = URI.create(SUBTASK_URI + "?id=2");
        URI url4 = URI.create(SUBTASK_URI + "?id=3");

        Subtask subtask1 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 20, 2, 2), Duration.ofMinutes(30));
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("name", "details", 1, LocalDateTime.of(2020, 4, 20, 2, 2), Duration.ofMinutes(30));
        subtask2.setId(2);
        Subtask subtask3 = new Subtask("name", "details", 1, LocalDateTime.of(2030, 4, 20, 2, 2), Duration.ofMinutes(30));
        subtask3.setId(3);
        String jsonOfTask1 = gson.toJson(subtask1);
        String jsonOfTask2 = gson.toJson(subtask2);
        String jsonOfTask3 = gson.toJson(subtask3);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(jsonOfTask3);

        HttpRequest request1 = HttpRequest.newBuilder().uri(url2).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url3).POST(body2).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url4).POST(body3).build();

        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            client.send(request3, HttpResponse.BodyHandlers.ofString());
            HttpRequest request4 = HttpRequest.newBuilder().uri(url1).GET().build();
            HttpResponse<String> response = client.send(request4, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(jsonOfTask1 + jsonOfTask2 + jsonOfTask3, response.body());

        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenRequestGetEpicSubtasksByIdThenGetCorrectListOfEpic() {
        URI url = URI.create(SUBTASK_URI + "/epic/?id=1");
        URI url2 = URI.create(SUBTASK_URI);

        Epic epic1 = new Epic("name", "details");
        epic1.setId(1);
        String jsonOfTask1 = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();

        Subtask subtask1 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 20, 2, 2), Duration.ofMinutes(30));
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("name", "details", 1, LocalDateTime.of(2020, 4, 20, 2, 2), Duration.ofMinutes(30));
        subtask2.setId(2);
        Subtask subtask3 = new Subtask("name", "details", 1, LocalDateTime.of(2030, 4, 20, 2, 2), Duration.ofMinutes(30));
        subtask3.setId(3);

        String jsonOfTask2 = gson.toJson(subtask1);
        String jsonOfTask3 = gson.toJson(subtask2);
        String jsonOfTask4 = gson.toJson(subtask3);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(jsonOfTask3);
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(jsonOfTask4);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).POST(body3).build();
        HttpRequest request4 = HttpRequest.newBuilder().uri(url2).POST(body4).build();

        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            client.send(request3, HttpResponse.BodyHandlers.ofString());
            client.send(request4, HttpResponse.BodyHandlers.ofString());
            HttpRequest request5 = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request5, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(jsonOfTask2 + jsonOfTask3 + jsonOfTask4, response.body());
        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenUpdateTaskThenCorrectPostTask() {
        URI url = URI.create(TASK_URI);

        Task task = new Task("Сходить в магазин", "вино", LocalDateTime.of(2024, 2, 2, 12, 0), Duration.ofMinutes(30));
        task.setId(1);
        String jsonOfTask1 = gson.toJson(task);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();

        Task task1 = new Task("Сходить в магазин", "Купить сыр, багет, помидоры, вино", LocalDateTime.of(2025, 2, 2, 12, 0), Duration.ofMinutes(30));
        task.setId(1);
        String jsonOfTask2 = gson.toJson(task1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();

        try {
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response3.statusCode());
            assertEquals(jsonOfTask2, response3.body());
            assertEquals(200, response1.statusCode());
            assertEquals(201, response2.statusCode());

        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenUpdateEpicThenCorrectPostEpic() {
        URI url = URI.create(EPIC_URI);

        Epic epic1 = new Epic("name", "details");
        epic1.setId(1);
        String jsonOfTask1 = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();

        Epic epic2 = new Epic("name", "details, data, water");
        epic2.setId(1);
        String jsonOfTask2 = gson.toJson(epic2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        try {
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response3.statusCode());
            assertEquals(jsonOfTask2, response3.body());
            assertEquals(200, response1.statusCode());
            assertEquals(201, response2.statusCode());

        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenUpdateSubtaskThenCorrectPostSubtask() {
        URI url = URI.create(SUBTASK_URI);

        Subtask subtask1 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 20, 2, 2), Duration.ofMinutes(30));
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("name", "details", 1, LocalDateTime.of(2010, 4, 21, 2, 2), Duration.ofMinutes(30));
        subtask2.setId(1);

        String jsonOfTask1 = gson.toJson(subtask1);
        String jsonOfTask2 = gson.toJson(subtask2);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        try {
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request4 = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response3 = client.send(request4, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response3.statusCode());
            assertEquals(jsonOfTask2, response3.body());
            assertEquals(200, response1.statusCode());
            assertEquals(201, response2.statusCode());
        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenDeleteTaskByIdThenCorrectDeleteTask() {
        URI url1 = URI.create(TASK_URI + "?id=1");
        URI url2 = URI.create(TASK_URI);

        Task task = new Task("Сходить в магазин", "вино", LocalDateTime.of(2024, 2, 2, 12, 0), Duration.ofMinutes(30));
        task.setId(1);
        String jsonOfTask1 = gson.toJson(task);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();

        Task task1 = new Task("Сходить в магазин", "Купить сыр, багет, помидоры, вино", LocalDateTime.of(2025, 2, 2, 12, 0), Duration.ofMinutes(30));
        task1.setId(2);
        String jsonOfTask2 = gson.toJson(task1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();

        try {
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url1).DELETE().build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response3.statusCode());
        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenDeleteEpicByIdThenCorrectDeleteEpic() {
        URI url1 = URI.create(EPIC_URI + "?id=1");
        URI url2 = URI.create(EPIC_URI);

        Epic epic1 = new Epic("name", "details");
        epic1.setId(1);
        String jsonOfTask1 = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();

        Epic epic2 = new Epic("name", "details, data, water");
        epic2.setId(2);
        String jsonOfTask2 = gson.toJson(epic2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url1).DELETE().build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response3.statusCode());
        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenDeleteSubtaskByIdThenCorrectDeleteSubtask() {
        URI url1 = URI.create(SUBTASK_URI + "?id=1");
        URI url2 = URI.create(SUBTASK_URI);

        Subtask subtask1 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 20, 2, 2), Duration.ofMinutes(30));
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("name", "details", 1, LocalDateTime.of(2010, 4, 21, 2, 2), Duration.ofMinutes(30));
        subtask2.setId(2);

        String jsonOfTask1 = gson.toJson(subtask1);
        String jsonOfTask2 = gson.toJson(subtask2);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();

        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url1).DELETE().build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response3.statusCode());
        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenDeleteAllTasksThenCorrectDelete() {
        URI url = URI.create(TASK_URI);

        Task task = new Task("Сходить в магазин", "вино", LocalDateTime.of(2024, 2, 2, 12, 0), Duration.ofMinutes(30));
        task.setId(1);
        String jsonOfTask1 = gson.toJson(task);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();

        Task task1 = new Task("Сходить в магазин", "Купить сыр, багет, помидоры, вино", LocalDateTime.of(2025, 2, 2, 12, 0), Duration.ofMinutes(30));
        task1.setId(2);
        String jsonOfTask2 = gson.toJson(task1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();

        try {
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response3.statusCode());
        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenDeleteAllEpicsThenCorrectDelete() {
        URI url = URI.create(EPIC_URI);

        Epic epic1 = new Epic("name", "details");
        epic1.setId(1);
        String jsonOfTask1 = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();

        Epic epic2 = new Epic("name", "details, data, water");
        epic2.setId(2);
        String jsonOfTask2 = gson.toJson(epic2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response3.statusCode());
        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    @Test
    void whenDeleteAllSubtaskThenCorrectDelete() {
        URI url = URI.create(SUBTASK_URI);

        Subtask subtask1 = new Subtask("name", "details", 1, LocalDateTime.of(2000, 4, 20, 2, 2), Duration.ofMinutes(30));
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("name", "details", 1, LocalDateTime.of(2010, 4, 21, 2, 2), Duration.ofMinutes(30));
        subtask2.setId(2);

        String jsonOfTask1 = gson.toJson(subtask1);
        String jsonOfTask2 = gson.toJson(subtask2);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonOfTask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonOfTask2);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();

        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response3.statusCode());
        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }
}

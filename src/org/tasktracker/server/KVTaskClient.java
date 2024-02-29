package org.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.tasktracker.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class KVTaskClient {

    private HttpClient client;
    private final String apiToken;
    private final String urlServer;

    public KVTaskClient(String urlServer) {
        this.client = HttpClient.newHttpClient();
        this.urlServer = urlServer;
        this.apiToken = getApiToken();
    }

    public HttpClient getClient() {
        return client;
    }

    public void put(String key, String json) {
        String urlForToken = urlServer + "/save/" + key + "?API_TOKEN=" + apiToken;
        URI url = URI.create(urlForToken);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                System.out.println("Где-то возникла проблема. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. Проверьте указанный адрес, наличие ключа и API_TOKEN!");
        }
    }

    public String load(String key) {
        String urlForToken = urlServer + "/load/" + key + "?API_TOKEN=" + apiToken;
        URI url = URI.create(urlForToken);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        String result = "";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                System.out.println("Где-то возникла проблема. Сервер вернул код состояния: " + response.statusCode());
            } else {
                result = response.body();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. Проверьте указанный адрес, наличие ключа и API_TOKEN!");
        }
        return result;
    }

    private String getApiToken() {
        String urlForToken = urlServer + "/register";
        URI urlToken = URI.create(urlForToken);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(urlToken).build();
        String apiToken = "";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                apiToken = jsonElement.getAsString();
            } else {
                System.out.println("Где-то возникла проблема. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. Проверьте указанный адрес!");
        }
        return apiToken;
    }

    public static void main(String[] args) throws IOException {

        KVServer serverKV = new KVServer();
        serverKV.start();
        KVTaskClient kvTaskClient = new KVTaskClient("http://localhost:8888");

        Gson gson = new Gson();
        Task task = new Task("Сходить в магазин", "Купить сыр, багет, помидоры, вино", LocalDateTime.of(2025, 2, 2, 12, 0), Duration.ofMinutes(30));
        task.setId(1);
        String jsonOfTask = gson.toJson(task);

        Task task1 = new Task("erer", "ere", LocalDateTime.of(2025, 2, 2, 12, 0), Duration.ofMinutes(30));
        task.setId(1);
        String jsonOfTask1 = gson.toJson(task1);

        kvTaskClient.put("tasks", jsonOfTask);
        System.out.println(kvTaskClient.load("tasks"));

        kvTaskClient.put("tasks", jsonOfTask1);
        System.out.println(kvTaskClient.load("tasks"));
        serverKV.stop();
    }
}

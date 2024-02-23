package org.tasktracker.model;

import org.tasktracker.manager.FileBackedTasksManager;
import org.tasktracker.manager.InMemoryTaskManager;
import org.tasktracker.model.enums.Status;
import org.tasktracker.model.enums.TypeOfTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;
    protected TypeOfTasks type;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = TypeOfTasks.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(TypeOfTasks type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public TypeOfTasks getType() {
        return type;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusMinutes(duration.toMinutes());
        }
        return null;
    }


    public String toCSV() {
        if (startTime != null && duration != null) {
            return String.format("%s,%s,%s,%s,%s,%s,%s", id, type, name, status, description.replace(',', ' '), startTime, duration);
        } else {
            return String.format("%s,%s,%s,%s,%s,%s,%s", id, type, name, status, description.replace(',', ' '), startTime = null, duration = null);
        }

    }

    @Override
    public String toString() {
        if (startTime != null) {
            return "Task{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", status=" + status +
                    ", type=" + type +
                    ", startTime=" + startTime +
                    ", duration=" + duration +
                    '}';
        } else {
            return "Task{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", status=" + status +
                    ", type=" + type +
                    ", startTime=" + null +
                    ", duration=" + duration +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        if (startTime == null || duration == null) {
            return id == task.id && name.equals(task.name) && description.equals(task.description) && status == task.status && type == task.type;
        }
        return id == task.id && name.equals(task.name) && description.equals(task.description) && status == task.status && type == task.type && duration.equals(task.duration) && startTime.equals(task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, type, duration, startTime);
    }
}

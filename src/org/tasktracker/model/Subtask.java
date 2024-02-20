package org.tasktracker.model;

import org.tasktracker.model.enums.Status;
import org.tasktracker.model.enums.TypeOfTasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.type = TypeOfTasks.SUBTASK;
        this.epicId = epicId;
    }

    @Override
    public Duration getDuration() {
        return super.getDuration();
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime();
    }

    @Override
    public LocalDateTime getEndTime() {
        return super.getEndTime();
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public Status getStatus() {
        return super.getStatus();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + epicId;
    }
}

package org.tasktracker.model;

import org.tasktracker.manager.FileBackedTasksManager;
import org.tasktracker.model.enums.Status;
import org.tasktracker.model.enums.TypeOfTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Epic extends Task {
    private List<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, null, null);
        this.subtaskIds = new ArrayList<>();
        this.type = TypeOfTasks.EPIC;
        this.endTime = null;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String toString() {
        if (endTime != null) {
            return super.toString() + ", endTime=" + endTime + '}';
        } else {
            return super.toString() + ", endTime=" + null + '}';
        }
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + Optional.ofNullable(endTime).map(LocalDateTime::toString).orElse(null);
    }
}

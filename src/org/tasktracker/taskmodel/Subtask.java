package org.tasktracker.taskmodel;

import org.tasktracker.taskmodel.Task;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String nameOfTask, String detailsOfTask, int epicId) {
        super(nameOfTask, detailsOfTask);
        this.epicId = epicId;
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
    public Status getStatusOfTask() {
        return super.getStatusOfTask();
    }

    @Override
    public String getNameOfTask() {
        return super.getNameOfTask();
    }

    @Override
    public void setStatusOfTask(Status statusOfTask) {
        super.setStatusOfTask(statusOfTask);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

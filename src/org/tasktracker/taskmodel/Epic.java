package org.tasktracker.taskmodel;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds;

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public Epic(String nameOfTask, String detailsOfTask) {
        super(nameOfTask, detailsOfTask);
        subTaskIds = new ArrayList<>();
        this.type = TypeOfTasks.EPIC;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
    }

    @Override
    public int getId() {
        return super.getId();
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    @Override
    public String getNameOfTask() {
        return super.getNameOfTask();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String toCSV() {
        return super.toCSV();
    }
}

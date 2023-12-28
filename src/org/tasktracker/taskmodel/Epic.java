package org.tasktracker.taskmodel;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds;


    public Epic(String nameOfTask, String detailsOfTask) {
        super(nameOfTask, detailsOfTask);
        subTaskIds = new ArrayList<>();
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
}

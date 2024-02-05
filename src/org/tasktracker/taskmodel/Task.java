package org.tasktracker.taskmodel;

public class Task {
    private int id;
    private String nameOfTask;
    private String detailsOfTask;
    private Status statusOfTask;
    protected TypeOfTasks type;

    public Task(String nameOfTask, String detailsOfTask) {
        this.nameOfTask = nameOfTask;
        this.detailsOfTask = detailsOfTask;
        this.statusOfTask = Status.NEW;
        this.type = TypeOfTasks.TASK;
    }

    public void setDetailsOfTask(String detailsOfTask) {
        this.detailsOfTask = detailsOfTask;
    }

    public void setType(TypeOfTasks type) {
        this.type = type;
    }

    public String getDetailsOfTask() {
        return detailsOfTask;
    }

    public String getNameOfTask() {
        return nameOfTask;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Status getStatusOfTask() {
        return statusOfTask;
    }

    public void setStatusOfTask(Status statusOfTask) {
        this.statusOfTask = statusOfTask;
    }

    public TypeOfTasks getType() {
        return type;
    }


    public String toCSV() {
        return id + "," + type + "," + nameOfTask + "," + statusOfTask + "," + detailsOfTask.replace(',' , ' ');
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", nameOfTask='" + nameOfTask + '\'' +
                ", detailsOfTask='" + detailsOfTask + '\'' +
                ", statusOfTask=" + statusOfTask +
                ", type=" + type +
                '}';
    }
}

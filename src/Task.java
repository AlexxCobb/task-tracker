public class Task {
    private int id;
    private String nameOfTask;
    private String detailsOfTask;
    private Status statusOfTask;

    public Task(String nameOfTask, String detailsOfTask) {
        this.nameOfTask = nameOfTask;
        this.detailsOfTask = detailsOfTask;
        this.statusOfTask = Status.NEW;
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

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", nameOfTask='" + nameOfTask + '\'' +
                ", detailsOfTask='" + detailsOfTask + '\'' +
                ", statusOfTask='" + statusOfTask + '\'' +
                '}';
    }
}

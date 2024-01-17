package org.tasktracker.manager;

public class Node <Task> {
    public Node <Task> prev;
    public Task data;
    public Node <Task> next;


    public Node(Node<Task> prev, Task data, Node<Task> next) {
        this.prev = prev;
        this.data = data;
        this.next = next;
    }
}

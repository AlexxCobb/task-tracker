package org.tasktracker.manager;

import org.tasktracker.manager.interfaces.HistoryManager;
import org.tasktracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;

    private Map<Integer, Node<Task>> taskIdsToNode;

    public InMemoryHistoryManager() {
        taskIdsToNode = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (head == null) {
            linkLast(task);
            taskIdsToNode.put(task.getId(), head);
        } else {
            if (taskIdsToNode.containsKey(task.getId())) {
                removeNode(taskIdsToNode.get(task.getId()));
                taskIdsToNode.remove(task.getId());
            }
            linkLast(task);
            taskIdsToNode.put(task.getId(), tail);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    // удаление задач из списка просмотров
    @Override
    public void remove(int id) {
        removeNode(taskIdsToNode.get(id));
        taskIdsToNode.remove(id);
    }

    // добавление задачи в конец списка
    private void linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
        size++;
    }

    // формирование всех задач в ArrayList
    private List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node<Task> currentNode = head;
        while (currentNode != null) {
            tasksList.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasksList;
    }

    // метод по удалению Node
    private void removeNode(Node<Task> node) {
        Node<Task> first = head;
        Node<Task> last = tail;
        if (node == first) {
            Node<Task> next = first.next;
            if (next != null) {
                next.prev = null;
                first.next = null;
                head = next;
            } else {
                head = null;
                tail = null;
            }
        } else if (node == last) {
            Node<Task> prev = last.prev;
            if (prev != null) {
                prev.next = null;
                tail = prev;
            }
        } else {
            Node<Task> nextNode = node.next;
            Node<Task> prevNode = node.prev;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
            node.next = null;
            node.prev = null;
        }
        size--;
    }

    private static class Node<Task> {
        public Node<Task> prev;
        public Task data;
        public Node<Task> next;

        private Node(Node<Task> prev, Task data, Node<Task> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }
}

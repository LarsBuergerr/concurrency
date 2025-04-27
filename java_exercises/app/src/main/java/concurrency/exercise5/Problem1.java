package concurrency.exercise5;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Problem1 {
  public static void main(String[] args) throws InterruptedException {
    AscendingLinkedList list = new AscendingLinkedList();

    Inserter inserter = new Inserter("inserter", list);
    Deleter deleter = new Deleter("deleter", list);

    inserter.start();
    Thread.sleep(Duration.ofSeconds(5));
    deleter.start();
    Thread.sleep(Duration.ofSeconds(10));

    inserter.interrupt();
    deleter.interrupt();

    inserter.join();
    deleter.join();

    System.out.println(list);
  }
}

class Inserter extends Thread {
  String name;
  AscendingLinkedList list;

  public Inserter(String name, AscendingLinkedList list) {
    this.name = name;
    this.list = list;
  }

  @Override
  public void run() {
    try {
      int i = 1;
      while (!Thread.currentThread().isInterrupted()) {
        list.insert(i);
        System.out.println(list.toString());
        i++;
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      System.out.println("Inserter interrupted!");
    }
  }
}

class Deleter extends Thread {
  String name;
  AscendingLinkedList list;

  public Deleter(String name, AscendingLinkedList list) {
    this.name = name;
    this.list = list;
  }

  @Override
  public void run() {
    try {
      int i = 1;
      while (!Thread.currentThread().isInterrupted()) {
        list.delete(i);
        System.out.println(list.toString());
        i++;
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      System.out.println("Deleter interrupted!");
    }
  }
}

class AscendingLinkedList {
  private Node head;

  public AscendingLinkedList() {
    this.head = null;
  }

  public void insert(int value) throws InterruptedException {
    Node newNode = new Node(value);

    if (head == null) {
      head = newNode;
      return;
    }

    Node prev = null;
    Node curr = head;    

    try {
    while (true) {
        curr.lock.lockInterruptibly();
        if (curr.value > value) {
          if (prev == null) {
            newNode.next = curr;
            head = newNode;
          } else {
            prev.next = newNode;
            newNode.next = curr;
          }
          break;
        }
        if (curr.next != null) {
          if (prev != null) prev.lock.unlock();
          prev = curr;
          curr = curr.next;
          continue;
        } else {
          curr.next = newNode;
          break;
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      if (prev != null) prev.lock.unlock();
      if (curr != null) curr.lock.unlock();
      throw e;
    } finally {
      if (prev != null) prev.lock.unlock();
      if (curr != null) curr.lock.unlock();
    }
  }
  

  public void delete(int value) throws InterruptedException {
    if (head == null) return;

    Node prev = null;
    Node curr = head;

    if (curr != null) curr.lock.lockInterruptibly();

    try {
      while (curr != null && curr.value < value) {
        if (prev != null) prev.lock.unlock();
        prev = curr;
        curr = curr.next;
        if (curr != null) curr.lock.lockInterruptibly();
      }

      if (curr != null && curr.value == value) {
        if (prev == null) {
          head = curr.next; // delete head
        } else {
          prev.next = curr.next;
        }
      }
    } finally {
      if (curr != null) curr.lock.unlock();
      if (prev != null) prev.lock.unlock();
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Node current = head;
    Node prev = null;

    try {
      if (current != null) current.lock.lockInterruptibly();
      while (current != null) {
        sb.append(current.value).append(" -> ");
        Node next = current.next;
        if (next != null) next.lock.lockInterruptibly();
        if (prev != null) prev.lock.unlock();
        prev = current;
        current = next;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      if (prev != null) prev.lock.unlock();
    }

    sb.append("null");
    return sb.toString();
  }
}

class Node {
  int value;
  Node next;
  final ReentrantLock lock;

  public Node(int value) {
    this.value = value;
    this.lock = new ReentrantLock();
  }
}

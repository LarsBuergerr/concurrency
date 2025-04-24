package concurrency.exercise5;

import java.time.Duration;
import java.util.concurrent.locks.ReentrantLock;

public class Problem1 {
  public static void main(String[] args) throws InterruptedException {
    AscendingLinkedList list = new AscendingLinkedList();

    Inserter inserter = new Inserter("inserter", list);
    Deleter deleter = new Deleter("deleter", list);

    inserter.start();

    Thread.sleep(Duration.ofSeconds(10));

    deleter.start();

    Thread.sleep(Duration.ofSeconds(60));

    inserter.interrupt();
    inserter.interrupt();

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
      while(true) {
        list.insert(i);
        System.out.println(list.toString());
        i++;
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
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
      while(true) {
        list.delete(i);
        System.out.println(list.toString());
        i++;
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      System.out.println("Deleter interrupted!");
    }
  }
}

class AscendingLinkedList {
  private final Node head;

  public AscendingLinkedList() {
    head = new Node(Integer.MIN_VALUE);
    head.next = new Node(Integer.MAX_VALUE);
  }

  public void insert(int value) {
    Node prev = head;
    prev.lock.lock();
    Node curr = prev.next;
    curr.lock.lock();

    try {
      while (curr.value < value) {
        prev.lock.unlock();
        prev = curr;
        curr = curr.next;
        curr.lock.lock();
      }

      Node newNode = new Node(value);
      newNode.next = curr;
      prev.next = newNode;
    } finally {
      curr.lock.unlock();
      prev.lock.unlock();
    }
  }

  public void delete(int value) {
    Node prev = head;
    prev.lock.lock();
    Node curr = prev.next;
    curr.lock.lock();

    try {
      while (curr.value < value) {
        prev.lock.unlock();
        prev = curr;
        curr = curr.next;
        curr.lock.lock();
      }

      if (curr.value == value) {
        prev.next = curr.next;
      }
    } finally {
      curr.lock.unlock();
      prev.lock.unlock();
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Node current = head.next;
    while (current.value != Integer.MAX_VALUE) {
      sb.append(current.value).append(" -> ");
      current = current.next;
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

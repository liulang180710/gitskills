package org.my.springcloud.producer.utils;

import java.util.LinkedList;

import static java.lang.Thread.sleep;

final class Message{
    private int id;
    private Object value;

    public Message(int id, Object value) {
        this.id = id;
        this.value = value;
    }
    public int getId() {
        return id;
    }
    public Object getValue() {
        return  value;
    }

}
// 消息队列
public class MessageQueue {
    private LinkedList<Message> list = new LinkedList<>();
    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    public Message take() {
        synchronized (list) {
            while(list.isEmpty()) {
                try {
                    System.out.println("消息队列已空，等待生产！");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message = list.removeFirst();
            System.out.println("消费者消费了一个消息！");
            list.notifyAll();
            return message;
        }
    }

    public void put(Message message) {
        synchronized (list) {
            while(list.size() == capacity) {
                try {
                    System.out.println("消息队列已满，等待消费！");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            list.addLast(message);
            System.out.println("生产者生产了一个消息！");
            list.notifyAll();
        }
    }

    public static void main(String[] args) {
        MessageQueue messageQueue = new MessageQueue(5);
        for (int i= 0 ; i < 10 ;i++) {
            int id = i;
            new Thread(()->{
                messageQueue.put(new Message(id, "值" + id));
            },"生产者" + i).start();
        }

        new Thread(() -> {
            while(true) {
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                messageQueue.take();
            }

        },"消费者").start();
    }
}


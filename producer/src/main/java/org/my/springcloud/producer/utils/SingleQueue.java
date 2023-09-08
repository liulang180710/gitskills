package org.my.springcloud.producer.utils;

import java.util.LinkedList;
import java.util.Queue;

public class SingleQueue {
    private static SingleQueue instance;

    public SingleQueue() {
        queue = new LinkedList<>();
    }

    public static synchronized SingleQueue getInstance() {
        if (instance == null) {
            instance = new SingleQueue();
        }
        return instance;
    }

    // 创建一个队列
    private static Queue<Character> queue;
    // 添加元素
    public synchronized void offer(char ch) {
        queue.offer(ch);
        System.out.println(System.currentTimeMillis()/1000 + Thread.currentThread().toString()+  ch);
        this.notify();
    }

    // 取出元素
    public synchronized Character poll() {
        while (queue.isEmpty()) {
            try {
                // 队列为空时，等待
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 有数据就输出
        Character c = queue.poll();
        System.out.println(System.currentTimeMillis()/1000 + Thread.currentThread().toString()+  c);
        return c;
    }

    // 获取队列大小
    public synchronized int size() {
        return queue.size();
    }

    public static void main (String args[]) {
        String a = "hloaiaa";
        String b = "el,lbb";
        final SingleQueue singleQueue = SingleQueue.getInstance();
        final Object object = new Object();


        // 创建线程1
        Thread threadOne = new Thread(() -> {
            for (char c : a.toCharArray()) {
                synchronized (object) {
                    // 唤醒线程2
                    object.notify();
                    singleQueue.offer(c);
                    try {
                        // 休息1S之后
                        Thread.sleep(1000);
                        // 进入休息区等待线程1唤醒
                        object.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        // 创建线程2
        Thread threadTwo = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (char c : b.toCharArray()) {
                synchronized (object) {
                    object.notify();
                    singleQueue.offer(c);
                    try {
                        Thread.sleep(1000);
                        object.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        // 创建线程3
        Thread threadThree = new Thread(() -> {
            while(true) {
                Character item = singleQueue.poll();
                System.out.println(item);
                try {
                    Thread.sleep(1000);
                }catch(Exception e) {
                    e.printStackTrace();

                }
            }


        });

        threadOne.start();
        threadTwo.start();
        threadThree.start();

        // 等待所有线程执行完毕
        try {
            threadOne.join();
            threadTwo.join();
            threadThree.join();


        }catch(Exception e) {
            e.printStackTrace();

        }



    }
}

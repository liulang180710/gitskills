package org.my.springcloud.producer.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SyncUtils {
    private static Lock lock = new ReentrantLock();// 通过JDK5中的Lock锁来保证线程的访问的互斥
    private static int state = 0;//通过state的值来确定是否打印


    private static Condition condictionA = lock.newCondition();
    private static Condition condictionB = lock.newCondition();
    private static Condition condictionC = lock.newCondition();
    private static int count = 0;

    /**三个线程，顺序输出ABCABC*/

    // reentranlock实现
    private static void sequentialOutputOne() {
        new Thread(()->{
            for (int i = 0; i < 10;) {
                lock.lock();
                try {
                    while (state % 3 == 0) {// 多线程并发，不能用if，必须用循环测试等待条件，避免虚假唤醒
                        System.out.print("A");
                        state ++;
                        i++;
                    }
                } finally {
                    lock.unlock();// unlock()操作必须放在finally块中
                }
            }
        }).start();
        new Thread(()->{
            for (int i = 0; i < 10;) {
                lock.lock();
                try {
                    while (state % 3 == 1) {// 多线程并发，不能用if，必须用循环测试等待条件，避免虚假唤醒
                        System.out.print("B");
                        state++;
                        i++;
                    }
                } finally {
                    lock.unlock();// unlock()操作必须放在finally块中
                }
            }
        }).start();
        new Thread(()->{
            for (int i = 0; i < 10;) {
                lock.lock();
                try {
                    while (state % 3 == 2) {// 多线程并发，不能用if，必须用循环测试等待条件，避免虚假唤醒
                        System.out.print("C");
                        state++;
                        i++;
                    }
                } finally {
                    lock.unlock();// unlock()操作必须放在finally块中
                }
            }
        }).start();
    }

    // reentranlock + condiction
    private static void sequentialOutputTwo() {
        new Thread(() -> {
            lock.lock();
            try {
                for (int i = 0; i < 10; i++) {
                    while (count % 3 != 0)
                        condictionA.await(); //注意这里是不等于0，也就是说在count % 3为0之前，当前线程一直阻塞状态
                    System.out.print("A");
                    count++;
                    condictionB.signal(); // A执行完唤醒B线程
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
        new Thread(()->{
            lock.lock();
            try {
                for (int i = 0; i < 10; i++) {
                    while (count % 3 != 1)
                        condictionB.await();
                    System.out.print("B");
                    count++;
                    condictionC.signal();// B执行完唤醒C线程
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
        new Thread(()->{
            lock.lock();
            try {
                for (int i = 0; i < 10; i++) {
                    while (count % 3 != 2)
                        condictionC.await();// C释放lock锁
                    System.out.print("C");
                    count++;
                    condictionA.signal();// C执行完唤醒A线程
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
    }

    public static void sequentialOutputThree() {
        WaitNotify waitNotify = new WaitNotify(10, 1);
        new Thread(()->
                waitNotify.print("A", 1, 2)
        ).start();
        new Thread(()-> waitNotify.print("B", 2, 3)).start();
        new Thread(()-> waitNotify.print("C", 3, 1)).start();
    }


    /*public static void main(String[] args) {

    }*/

}

class WaitNotify {

    private int loopNumber;
    private int flag;

    public WaitNotify (int loopNumber, int flag) {
        this.loopNumber = loopNumber;
        this.flag = flag;
    }

    public void print(String str, int waitFlag, int nextFlag) {
        for (int i = 0; i < loopNumber ; i++) {
            synchronized (this) {
                while(flag != waitFlag) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print(str);
                flag = nextFlag;
                this.notifyAll();
            }
        }
    }
}

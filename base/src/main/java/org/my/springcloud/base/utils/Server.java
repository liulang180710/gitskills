package org.my.springcloud.base.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile boolean start = false;
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }
        public void register(SocketChannel socketChannel) throws IOException {
            if (!start) {
                thread = new Thread(this, name);
                selector = Selector.open();
                start = true;
            }
            queue.add(()->{
                try{
                    socketChannel.register(selector, SelectionKey.OP_READ, null);
                }catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup(); //唤醒select

        }

        @Override
        public void run() {
           while(true) {
               try{
                   selector.select(); //阻塞
                   Runnable task = queue.poll();
                   if (task != null) {
                       task.run();
                   }
                   Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                   while (iterator.hasNext()) {
                       SelectionKey key = iterator.next();
                       iterator.remove();
                       if (key.isReadable()) {
                           ByteBuffer buffer = ByteBuffer.allocate(16);
                           SocketChannel socketChannel = (SocketChannel) key.channel();
                           socketChannel.read(buffer);
                           buffer.flip();
                           System.out.println(buffer);
                       }
                   }
               }catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }

    }

    public static void main(String[] args) throws IOException {
        creatServer();
        return;
    }

    //同步阻塞（accept、read都会阻塞线程）
    private static void creatServer() throws IOException {
        // 创建服务器
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 监听端口
        serverSocketChannel.bind(new InetSocketAddress(8080));

        ByteBuffer buffer = ByteBuffer.allocate(16);

        List<SocketChannel> socketChannelList = new ArrayList<>();
        while (true) {
            // 建立与客户端的连接
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannelList.add(socketChannel);
            socketChannelList.forEach(channel -> {
                try {
                    channel.read(buffer);
                    buffer.flip();
                    System.out.printf("输出结果" + buffer.toString());
                    buffer.clear();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void split(ByteBuffer source) {
        source.flip();
        for (int i=0 ;i < source.limit() ; i++) {
            if (source.get(i) == '\n') {
                int length = i + 1 -source.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j=0;j< length;j++) {
                    target.put(source.get());
                }
                System.out.println(source);
            }
        }
        source.compact();
    }

    public static void createServerBySelector() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        SelectionKey selectionKey = serverSocketChannel.register(selector,0,null);

        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(8080));
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey1 = iterator.next();
                iterator.remove();
                if (selectionKey1.isAcceptable()) {
                    // seelct 在事件未处理时，它不会阻塞
                    // 获取selector关注的连接事件，从而获取channel
                    ServerSocketChannel channel = (ServerSocketChannel)selectionKey1.channel();
                    SocketChannel socketChannel = channel.accept();
                    // 非阻塞
                    socketChannel.configureBlocking(false);
                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                    // 对连接的socketChannel增加关注的事件，绑定到selector上
                    SelectionKey selectionKey2 = socketChannel.register(selector, 0, byteBuffer);
                    selectionKey2.interestOps(SelectionKey.OP_READ);
                }else if (selectionKey1.isReadable()) {
                    try{
                        SocketChannel channel = (SocketChannel)selectionKey1.channel();
                        ByteBuffer buffer = (ByteBuffer) selectionKey1.attachment();
                        int read = channel.read(buffer);
                        // 客户端正常断开后需要移除
                        if (read == -1) {
                            selectionKey1.cancel();
                        }else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                selectionKey1.attach(newBuffer);
                            }
                        }
                    }catch (Exception e) {
                        selectionKey1.cancel();
                    }
                }
            }
        }
    }

    public static void createServerBySelectorAndThread() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        SelectionKey selectionKey = serverSocketChannel.register(selector,0,null);

        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(8080));
        Worker[] workers = new Worker[2];
        for (int i = 0; i < workers.length ; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        AtomicInteger atomicInteger = new AtomicInteger();

        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey1 = iterator.next();
                iterator.remove();
                if (selectionKey1.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    workers[atomicInteger.incrementAndGet() % workers.length].register(socketChannel);
                }else if (selectionKey1.isReadable()) {
                    try{
                        SocketChannel channel = (SocketChannel)selectionKey1.channel();
                        ByteBuffer buffer = (ByteBuffer) selectionKey1.attachment();
                        int read = channel.read(buffer);
                        if (read == -1) {
                            selectionKey1.cancel();
                        }else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                selectionKey1.attach(newBuffer);
                            }
                        }
                    }catch (Exception e) {
                        selectionKey1.cancel();
                    }
                }
            }
        }
    }
}

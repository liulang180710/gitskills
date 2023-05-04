package org.my.springcloud.base.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException {
        creatServer();
        return;
    }

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
                    ServerSocketChannel channel = (ServerSocketChannel)selectionKey1.channel();
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                    SelectionKey selectionKey2 = socketChannel.register(selector, 0, byteBuffer);
                    selectionKey2.interestOps(SelectionKey.OP_READ);
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

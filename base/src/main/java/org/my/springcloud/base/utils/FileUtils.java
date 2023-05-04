package org.my.springcloud.base.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class FileUtils {

    public static String getFileByFileName(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            FileChannel channel = new FileInputStream(fileName).getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                int len = channel.read(buffer);
                if (len == -1) {
                    break;
                }

                buffer.flip();
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    stringBuilder.append((char) b);
                }
                buffer.clear();
            }

        }catch (IOException e) {

        }
        return stringBuilder.toString();

    }

    public static void fileCount(String fileName) throws IOException{
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get(fileName), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir ,attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }

        });
        System.out.println("dir count:" + dirCount);
        System.out.println("file count:" + fileCount);

    }

    public static void fileDelete(String fileName) throws IOException{
        Files.walkFileTree(Paths.get(fileName), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

}

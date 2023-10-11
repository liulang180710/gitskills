package org.my.springcloud.base.utils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class NettyServer {
    public static void main(String[] args) throws IOException {
        creatServer();
    }

    private static void creatServer(){


       new ServerBootstrap()
               .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
               .channel(NioServerSocketChannel.class)
               .childHandler(new ChannelInitializer<NioSocketChannel>() {
                   @Override
                   protected void initChannel(NioSocketChannel nioSocketChannel) {
                       nioSocketChannel.pipeline().addLast(new StringDecoder());
                       nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                           @Override
                           public void channelRead(ChannelHandlerContext ctx, Object msg)  {
                               System.out.println(msg);
                           }
                       });
                   }
               }).bind(8080);
    }

    private static void startServer(){
        // boss只负责serversocketchannel上的accept事件
        // worker 只负责socketchannel上的读写，默认为CPU的数量*2
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss,worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e) {
            log.error("server error", e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}

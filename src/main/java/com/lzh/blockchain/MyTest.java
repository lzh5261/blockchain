package com.lzh.blockchain;

import java.net.URI;

public class MyTest {
    public static void main(String[] args) {
        try {
            // 创建并开启服务器
            MyServer myServer = new MyServer(8000);
            myServer.startServer();
            // 指定服务器地址
            URI uri = new URI("ws://localhost:8000");
            // 创建客户端
            MyClient client01 = new MyClient(uri, "客户端01");
            MyClient client02 = new MyClient(uri, "客户端02");
            // 客户端连接服务器
            client01.connect();
            client02.connect();
            // 避免连接尚未成功,就发送消息,导致的发送失败
            Thread.sleep(1000);
            // 服务器发送广播
//            myServer.broadcast("这是来自服务器的广播");
            // 客户端发送消息给服务器
            client01.send("你好，我是客户端01");
            client02.send("你好，我是客户端02");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

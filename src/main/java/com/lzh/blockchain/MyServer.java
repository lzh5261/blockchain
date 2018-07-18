package com.lzh.blockchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzh.blockchain.bean.Block;
import com.lzh.blockchain.bean.MessageBean;
import com.lzh.blockchain.bean.NoteBook;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;

//创建服务器
public class MyServer extends WebSocketServer {
    //创建服务器端口号
    private int port;

    //构造函数
    public MyServer(int port) {
        super(new InetSocketAddress(port));
        this.port = port;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("端口号为" + port + "的服务端打开了连接。。。");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("端口号为" + port + "的服务端关闭了连接。。。");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("端口号为" + port + "的服务端接收到了：" + message);
        //服务端接收到客户端的同步请求
        try {
            if ("请求同步区块链数据。。。".equals(message)) {
                //获取本地的区块链数据
                NoteBook book = NoteBook.getInstance();
                ArrayList<Block> blocks = book.showlist();
                //// 发送给连接到本服务器的所有客户端
                ObjectMapper objectMapper = new ObjectMapper();
                String blockData = objectMapper.writeValueAsString(blocks);

                MessageBean messageBean = new MessageBean(1, blockData);
                String msg = objectMapper.writeValueAsString(messageBean);
                System.out.println(msg);
                //服务器收到请求之后把消息广播出去
                broadcast(msg);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("端口号为" + port + "的服务端发生错误。。。" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket服务器__" + port + "__启动成功");
    }

    //开启服务器
    public void startServer() {
        new Thread(this).start();
    }
}

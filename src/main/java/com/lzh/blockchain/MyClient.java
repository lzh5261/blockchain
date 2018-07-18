package com.lzh.blockchain;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzh.blockchain.bean.Block;
import com.lzh.blockchain.bean.MessageBean;
import com.lzh.blockchain.bean.NoteBook;
import com.lzh.blockchain.bean.Transaction;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

public class MyClient extends WebSocketClient {
    /**
     * @param serverUri : 要连接的服务器的地址
     * @param name      : 本客户端的名字
     */
    private String name;

    public MyClient(URI serverUri, String name) {
        super(serverUri);
        this.name = name;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("客户端" + name + "打开了连接。。。");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("客户端" + name + "的接收到了：" + message);
        try {
            //客户端收到服务端发送过来的数据
            ObjectMapper objectMapper = new ObjectMapper();
            MessageBean messageBean = objectMapper.readValue(message, MessageBean.class);
            NoteBook book = NoteBook.getInstance();
            // 判断消息类型
            if (messageBean.type == 1) {
                //客户端收到的是区块链数据需要反序列化区块数据
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Block.class);
                ArrayList<Block> newList = objectMapper.readValue(messageBean.msg, javaType);
                //新的区块链和本地的对比，比较长度，如果新的长就把旧的替换为新的
                book.compareDate(newList);
            }else if (messageBean.type == 2){
                Transaction transaction = objectMapper.readValue(messageBean.msg, Transaction.class);
                if (transaction.verifySign()){
                    book.addNote(messageBean.msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("客户端" + name + "关闭了连接。。。");
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("客户端" + name + "发生错误。。。" + ex);
    }
}

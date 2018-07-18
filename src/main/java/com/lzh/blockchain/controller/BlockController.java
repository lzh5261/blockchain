package com.lzh.blockchain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzh.blockchain.BlockchainApplication;
import com.lzh.blockchain.MyClient;
import com.lzh.blockchain.MyServer;
import com.lzh.blockchain.bean.Block;
import com.lzh.blockchain.bean.MessageBean;
import com.lzh.blockchain.bean.NoteBook;
import com.lzh.blockchain.bean.Transaction;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.PostConstruct;

@RestController
public class BlockController {
    /*---------------------------- NoteBbook部分 ----------------------------*/
    private NoteBook book = NoteBook.getInstance();

    @PostMapping("/addGenesis")
    public String addGenesis(String genesis) {
        try {
            book.addGenesis(genesis);
            return "封面添加成功";
        } catch (Exception e) {
            return "封面添加失败:" + e.getMessage();
        }
    }

    @PostMapping("/addNote")
    public String addNote(Transaction transaction) {
        try {
            //添加数据之前校验数据
            if (transaction.verifySign()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String transactionAsString = objectMapper.writeValueAsString(transaction);
                //广播交易数据
                MessageBean messageBean = new MessageBean(2,transactionAsString);
                String msg = objectMapper.writeValueAsString(messageBean);
                server.broadcast(msg);
                book.addNote(transactionAsString);
                return "该条记录添加成功";
            } else {
                throw new RuntimeException("交易数据校验失败！");
            }
        } catch (Exception e) {
            return "该条记录添加失败:" + e.getMessage();
        }
    }

    @GetMapping("/check")
    public String check() {
        String check = book.check();
        //if (check.equals("")||check == null) {   //??????为啥不能用check==null判断？
        if (StringUtils.isEmpty(check)) {
            return "数据正常";
        }
        return check;
    }

    @GetMapping("/showlist")
    public ArrayList<Block> showlist() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return book.showlist();
    }

    /*---------------------------- WebSocket部分 ----------------------------*/
    private MyServer server;
    @PostConstruct
    public void init() {
        server = new MyServer(Integer.parseInt(BlockchainApplication.port) + 1);
        server.startServer();
    }

    //节点注册
    private HashSet<String> set = new HashSet<>();
    @RequestMapping("/regist")
    public String regist(String node) {
        set.add(node);
        return "添加成功";
    }

    private ArrayList<MyClient> clients = new ArrayList<>();

    //连接
    @RequestMapping("/conn")
    public String conn() {
        try {
            for (String s : set) {
                URI uri = new URI("ws://localhost:" + s);
                MyClient client = new MyClient(uri, s);
                client.connect();
                clients.add(client);
            }
            return "连接成功";
        } catch (URISyntaxException e) {
            return "连接失败："+ e.getStackTrace();
        }
    }
    //广播
    @RequestMapping("/broadcast")
    public String broadcast(String msg) {
        server.broadcast(msg);
        return "广播成功";
    }
/*---------------------------- 同步节点区块数据 ----------------------------*/
    // 请求同步其他节点的区块链数据
    @RequestMapping("/syncData")
    public String syncData() {

        for (MyClient client : clients) {
            client.send("请求同步区块链数据。。。");
        }
        return "同步成功";
    }

}

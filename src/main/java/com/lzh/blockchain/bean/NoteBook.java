package com.lzh.blockchain.bean;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzh.blockchain.utils.HashUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NoteBook {
    // 用于保存数据的集合
    private ArrayList<Block> list = new ArrayList<>();

    //构造函数里面调用文件加载方法，使在new对象的时候就读取文件
    private NoteBook() {
        loadFile();
    }
    //将NoteBook设置成单例模式
    public static volatile NoteBook instance;

    public static NoteBook getInstance(){
        if (instance == null){
            synchronized (NoteBook.class){
                if (instance == null){
                    instance = new NoteBook();
                }
            }
        }
        return instance;
    }

    //挖矿的过程就是求取一个符合特定规则的hash值
    private int mine(String content){
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (HashUtils.sha256(i+content).startsWith("0000")){
                System.out.println("第"+i+"次挖矿成功！hash值为：" + HashUtils.sha256(i+content));
                return i;
            }else{
//                System.out.println("第"+i+"次挖矿失败！");
            }
        }
        throw new RuntimeException("挖矿失败！") ;
    }
    // 添加封面 = 创世区块
    // 添加封面的时候,必须保证账本是新的
    public void addGenesis(String genesis) {
        if (list.size() > 0) {
            throw new RuntimeException("添加封面的时候,必须保证账本是新的");
        }
        //第一个preHash值设定为固定初始值：
        String preHash = "0000000000000000000000000000000000000000000000000000000000000000";
        int nonce = mine(genesis+preHash);
        list.add(new Block(
                list.size() + 1,
                genesis,
                HashUtils.sha256(nonce+genesis+preHash),
                nonce,
                preHash
        ));
        saveToDisk();
    }

    // 添加交易记录 = 普通区块
    // 添加交易记录的时候,必须保证账本已经有封面了
    public void addNote(String note) {
        if (list.size() < 1) {
            throw new RuntimeException("添加交易记录的时候,必须保证账本已经有封面了");
        }
        Block block = list.get(list.size()-1);
        String preHash = block.hash;
        int nonce = mine(note+preHash);
        list.add(new Block(
                list.size() + 1,
                note,
                HashUtils.sha256(nonce+note+preHash),
                nonce,
                preHash
        ));
        saveToDisk();
    }

    // 展示数据
    public ArrayList<Block> showlist() {
        return list;
    }

    // 保存到本地硬盘
    public void saveToDisk() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File("a.json");
            objectMapper.writeValue(file, list);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //增加加载本地数据的方法
    public void loadFile() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File("a.json");
            // 判断文件是否存在
            if (file.exists() && file.length() > 0) {
                //  如果文件存在,读取之前的数据
                JavaType javatype = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Block.class);
                list = objectMapper.readValue(file, javatype);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //校验数据是否被篡改
    public String check() {
        StringBuilder sb = new StringBuilder();
        /*for (Block block : list) {
            String checkHash = HashUtils.sha256(block.content);
            if (!block.hash.equals(checkHash)) {
                sb.append("编号为" + block.id + "的数据不对，请检查！<br>");
            }
        }*/
        for (int i = 0; i < list.size(); i++) {
            Block block = list.get(i);
            String preHash = block.preHash;
            String content = block.content;
            String hash = block.hash;
            int nonce = block.nonce;
            int id = block.id;
            //第一个区块，只要校验hash值
            if (i==0){
                String s = HashUtils.sha256(nonce + content + preHash);
                if (!s.equals(hash)){
                    sb.append("编号为" + block.id + "的hash不对，请检查！<br>");
                }
            }else{
                //普通区块即要校验hash值又要校验preHash值
                String checkHash = HashUtils.sha256(nonce + content + preHash);
                //hash值比较
                if (!checkHash.equals(hash)){
                    sb.append("编号为" + block.id + "的hash不对，请检查！<br>");
                }
                //preHash比较
                String preBlockHash = list.get(i - 1).hash;
                if (!preBlockHash.equals(preHash)){
                    sb.append("编号为" + block.id + "的preHash不对，请检查！<br>");
                }
            }
        }
        return sb.toString();
    }
    //新的区块链和本地的对比，比较长度，如果新的长就把旧的替换为新的
    public void compareDate(ArrayList<Block> newList) {
        if (newList.size()>list.size()){
            list = newList;
        }
    }
    /*public static void main(String[] args) {
        NoteBook noteBook = new NoteBook();
        noteBook.addGenesis("封面");
        noteBook.addNote("交易记录");
        noteBook.showlist();
    }*/
}

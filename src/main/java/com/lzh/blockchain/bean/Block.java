package com.lzh.blockchain.bean;

public class Block {
    public int id;          //编号
    public String content;  //交易记录
    public String hash;     //哈希值
    public int nonce;       //工作量证明
    public String preHash;  //上一个区块的hash值

    public Block() {
    }

    public Block(int id, String content, String hash,int nonce,String preHash) {
        this.id = id;
        this.content = content;
        this.hash = hash;
        this.nonce = nonce;
        this.preHash = preHash;
    }
}

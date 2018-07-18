package com.lzh.blockchain.bean;

import com.lzh.blockchain.utils.RSAUtils;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

//钱包
public class Wallet {
    //定义密钥对
    public PublicKey publicKey;
    public PrivateKey privateKey;
    public String name;

    //构造方法
    public Wallet(String name) {
        this.name = name;
        //定义密钥文件
        File pubFile = new File(name + ".pub");
        File priFile = new File(name + ".pri");
        //判断文件里是否存在公钥和私钥，若存在则读取出来赋值给publicKey,privateKey
        if (pubFile.exists() && priFile.exists() && pubFile.length() != 0 && priFile.length() != 0) {
//            publicKey = RSAUtils.getPublicKeyFromFile("RSA", name + ".pub");
//            privateKey = RSAUtils.getPrivateKeyFromFile("RSA", name + ".pri");
        } else {
            //若文件里不存在密钥或者公私钥有一个不存在则为其重新创建
            RSAUtils.generateKeysJS("RSA", name + ".pub", name + ".pri");
        }
    }
    //转账方法

    /**
     * @param publicKeyTo //接收方公钥
     * @param money        //转账信息
     */
    public Transaction transfer(String publicKeyTo, String money){
        //获取转出方的签名
        String sign = RSAUtils.getSignature("SHA256withRSA", privateKey, money);
        //转出方的公钥
        String publicKeyFrom = Base64.encode(publicKey.getEncoded());
        Transaction transaction = new Transaction(publicKeyFrom,publicKeyTo,money,sign);
        return transaction;
    }
    //测试一下
    public static void main(String[] args) {
        Wallet a = new Wallet("a") ;
        Wallet b = new Wallet("b") ;
//        PublicKey publicKey = b.publicKey;
//        System.out.println(receivePublicKey);
//        Transaction transfer = a.transfer(receivePublicKey, "100");
//        boolean b1 = transfer.verifySign();
//        System.out.println(b1);
    }
}

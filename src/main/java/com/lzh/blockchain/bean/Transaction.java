package com.lzh.blockchain.bean;

import com.lzh.blockchain.utils.RSAUtils;

import java.security.PublicKey;

//钱包的功能：交易
public class Transaction {
    //转账的地址，包括转出方的公钥和转入方的公钥
    public String publicKeyFrom;
    public String publicKeyTo;
    //转账的金额
    public String money;
    //转账的签名
    public String sign;

    public Transaction() {
    }

    public Transaction(String publicKeyFrom, String publicKeyTo, String money, String sign) {
        this.publicKeyFrom = publicKeyFrom;
        this.publicKeyTo = publicKeyTo;
        this.money = money;
        this.sign = sign;
    }

    public String getPublicKeyFrom() {
        return publicKeyFrom;
    }

    public void setPublicKeyFrom(String publicKeyFrom) {
        this.publicKeyFrom = publicKeyFrom;
    }

    public String getPublicKeyTo() {
        return publicKeyTo;
    }

    public void setPublicKeyTo(String publicKeyTo) {
        this.publicKeyTo = publicKeyTo;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "publicKeyFrom='" + publicKeyFrom + '\'' +
                ", publicKeyTo='" + publicKeyTo + '\'' +
                ", money=" + money +
                ", sign='" + sign + '\'' +
                '}';
    }
    //验证签名
    public boolean verifySign(){
        //发送方公钥
        PublicKey senderPublicKey = RSAUtils.getPublicKeyFromString("RSA", publicKeyFrom);
        return RSAUtils.verifyDataJS("SHA256withRSA",senderPublicKey,money,sign);
    }
}

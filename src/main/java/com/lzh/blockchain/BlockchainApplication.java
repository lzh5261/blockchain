package com.lzh.blockchain;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Scanner;

@SpringBootApplication
public class BlockchainApplication {
    public static String port;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        port = scanner.nextLine();
        new SpringApplicationBuilder(BlockchainApplication.class).properties("server.port=" + port).run(args);
    }
}

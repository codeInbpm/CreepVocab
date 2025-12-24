package com.creepvocab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.creepvocab.mapper")
@EnableDiscoveryClient
@EnableFeignClients
public class CreepVocabApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreepVocabApplication.class, args);
    }

}

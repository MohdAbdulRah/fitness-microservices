package org.fitness.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class UserserviceApplication {
    //hi
    //by AbdulRahman
    public static void main(String[] args) {
        SpringApplication.run(UserserviceApplication.class, args);
    }

}

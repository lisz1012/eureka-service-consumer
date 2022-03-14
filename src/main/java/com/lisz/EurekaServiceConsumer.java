package com.lisz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class EurekaServiceConsumer {
    public static void main( String[] args ){
        SpringApplication.run(EurekaServiceConsumer.class, args);
    }
}

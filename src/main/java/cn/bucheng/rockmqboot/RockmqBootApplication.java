package cn.bucheng.rockmqboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class RockmqBootApplication {

    public static void main(String[] args) {
        log.info("begain start application");
        SpringApplication.run(RockmqBootApplication.class, args);
    }

}

package io.sparqs.hrworks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HrWorksApplication {

    public static final String API_PATH_PREFIX = "api";

    public static void main(String[] args) {
        SpringApplication.run(HrWorksApplication.class, args);
    }

}

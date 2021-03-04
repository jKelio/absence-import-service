package io.sparqs.hrworks;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final ScheduledTaskHolder holder;

    TestController(ScheduledTaskHolder holder) {
        this.holder = holder;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok().body("Test");
    }

}

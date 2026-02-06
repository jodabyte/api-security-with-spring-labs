package de.jodabyte.apisecurity.bfla;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/admin")
    public String hello() {
        return "Hello Admin!";
    }
}

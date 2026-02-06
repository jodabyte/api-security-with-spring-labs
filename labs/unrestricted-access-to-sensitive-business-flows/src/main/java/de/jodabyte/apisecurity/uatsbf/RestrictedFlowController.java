package de.jodabyte.apisecurity.uatsbf;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestrictedFlowController {

    @PostMapping("/restricted-flow")
    public String restrictedFlow() {
        return "Executed!";
    }
}

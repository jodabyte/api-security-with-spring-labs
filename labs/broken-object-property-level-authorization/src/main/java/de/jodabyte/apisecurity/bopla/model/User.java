package de.jodabyte.apisecurity.bopla.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private String uuid;
    private String username;
    private String name;
    private String email;
    private LocalDate createdAt;
    private boolean isActive;
}

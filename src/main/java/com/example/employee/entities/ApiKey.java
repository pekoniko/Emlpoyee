package com.example.employee.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_key")
@Getter
@Setter
@NoArgsConstructor
public class ApiKey {

    @Id
    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "api_time", nullable = false)
    private Timestamp apiTime;

    public ApiKey(String key, LocalDateTime apiTime) {
        this.key = key;
        this.apiTime = Timestamp.valueOf(apiTime);
    }
}
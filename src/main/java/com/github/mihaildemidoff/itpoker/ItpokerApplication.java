package com.github.mihaildemidoff.itpoker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@Slf4j
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class ItpokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItpokerApplication.class, args);
    }

}

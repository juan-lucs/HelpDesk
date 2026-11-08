package com.juanlucas.helpdesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // vai fazer com que as analises (@EntityListeners) das classes (historico e chamado), funcionem
public class HelpdeskApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelpdeskApplication.class, args);
    }

}

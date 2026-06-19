package com.blocksquarelabs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableJpaAuditing
@SpringBootApplication
public class BlocksquarelabsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlocksquarelabsApplication.class, args);
    }

}

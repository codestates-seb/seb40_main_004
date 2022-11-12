package com.morakmorak.morak_back_end;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class MorakBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(MorakBackEndApplication.class, args);
	}

}

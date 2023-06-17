package io.github.steliospaps.spike.jpa.postgresql.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SpikeJpaPostgresqlEventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpikeJpaPostgresqlEventsApplication.class, args);
	}

}

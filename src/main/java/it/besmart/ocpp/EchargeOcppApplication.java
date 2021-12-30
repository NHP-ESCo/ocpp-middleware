package it.besmart.ocpp;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ComponentScan("it.besmart")
public class EchargeOcppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EchargeOcppApplication.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public TaskScheduler taskScheduler() {
	    TaskScheduler scheduler = new ThreadPoolTaskScheduler();

	    return scheduler;
	}
}

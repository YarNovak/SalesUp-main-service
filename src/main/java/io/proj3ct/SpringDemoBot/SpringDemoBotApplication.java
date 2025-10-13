package io.proj3ct.SpringDemoBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDemoBotApplication {

	public static void main(String[] args)  {

		//ConfigurableApplicationContext context = SpringApplication.run(SpringDemoBotApplication.class, args);
		//CartService cartService = context.getBean(CartService.class);
		//cartService.startCartCleanupJob();
		SpringApplication.run(SpringDemoBotApplication.class, args);
	}

}

package year.exp.forge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ForgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForgeApplication.class, args);
	}

}

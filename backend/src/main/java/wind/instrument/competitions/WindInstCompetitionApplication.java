package wind.instrument.competitions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"wind.instrument.competitions.data"})
public class WindInstCompetitionApplication {

	public static void main(String[] args) {
		SpringApplication.run(WindInstCompetitionApplication.class, args);
	}
}

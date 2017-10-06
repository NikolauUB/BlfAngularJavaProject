package wind.instrument.competitions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EntityScan(basePackages = {"wind.instrument.competitions.data"})
@ImportResource("classpath:private-bean-data-config.xml")
public class WindInstCompetitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(WindInstCompetitionApplication.class, args);
    }
}

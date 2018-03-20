package wind.instrument.competitions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = {"wind.instrument.competitions.data"})
@ImportResource("classpath:private-bean-data-config.xml")
@EnableTransactionManagement
@EnableCaching
public class WindInstCompetitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(WindInstCompetitionApplication.class, args);
    }
}

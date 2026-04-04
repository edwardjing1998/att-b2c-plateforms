package att.b2c.segment.outbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "att.b2c.segment")
public class OutboxServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OutboxServiceApplication.class, args);
    }
}

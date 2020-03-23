package li.selman.dershop;

import li.selman.dershop.product.Product;
import li.selman.dershop.product.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DerShopApplication {

    private static final Logger log = LoggerFactory.getLogger(DerShopApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DerShopApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(ProductRepository repo) {
        return args -> {
            repo.save(new Product("Banana"));
            repo.save(new Product("Apple"));
            repo.save(new Product("Kiwi"));

            repo.findAll()
                .forEach(it -> log.info(it.toString()));
        };
    }

}

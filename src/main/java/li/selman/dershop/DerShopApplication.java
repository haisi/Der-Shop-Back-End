/*
 * (c) Copyright 2021 Hasan Selman Kara. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package li.selman.dershop;

import li.selman.dershop.app.security.jwt.JwtProperties;
import li.selman.dershop.product.Product;
import li.selman.dershop.product.ProductRepository;
import li.selman.dershop.user.Authority;
import li.selman.dershop.user.AuthorityRepository;
import li.selman.dershop.user.DerShopUser;
import li.selman.dershop.user.UserRepository;
import li.selman.dershop.user.UserService;
import li.selman.dershop.user.dto.AdminUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class DerShopApplication {

    private static final Logger log = LoggerFactory.getLogger(DerShopApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DerShopApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(ProductRepository repo, UserRepository userRepo,
                                  UserService userService, AuthorityRepository authRepo) {
        return args -> {
            repo.save(new Product("Banana"));
            repo.save(new Product("Apple"));
            repo.save(new Product("Kiwi"));

            // TODO(#refactor): Load as part of Liquibase
            Authority admin = new Authority();
            admin.setName("admin");
            admin = authRepo.save(admin);

            AdminUserDto newUser = new AdminUserDto();
            newUser.setLogin("admin@example.com");
            newUser.setEmail("admin@example.com");
            newUser.setCreatedBy("admin");
            DerShopUser nU = userService.registerUser(newUser, "Hunter1");
            nU.getAuthorities().add(admin);
            nU.setActivated(true);
            userRepo.save(nU);

            repo.findAll()
                .forEach(it -> log.info(it.toString()));
        };
    }

}

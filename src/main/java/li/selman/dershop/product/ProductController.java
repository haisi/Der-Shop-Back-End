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
package li.selman.dershop.product;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hasan Selman Kara
 */
@RestController
@ExposesResourceFor(Product.class)
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepo;

    ProductController(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping
    CollectionModel<Product> findAll() {
        return CollectionModel.of(productRepo.findAll());
    }

    @GetMapping("{id}")
    ResponseEntity<?> findById(@PathVariable("id") Long id) {
        return productRepo.findById(id)
                          .map(EntityModel::of)
                          .map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

}

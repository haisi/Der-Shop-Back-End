package li.selman.dershop.product;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Hasan Selman Kara
 */
@RestController
@ExposesResourceFor(Product.class)
@RequestMapping("/product")
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


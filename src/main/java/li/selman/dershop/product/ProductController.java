package li.selman.dershop.product;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Hasan Selman Kara
 */
@RestController
public class ProductController {

    private final ProductRepository productRepo;

    ProductController(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping("product")
    CollectionModel<Product> findAll() {
        return CollectionModel.of(productRepo.findAll());
    }

    @GetMapping("product/{id}")
    ResponseEntity<?> findById(@PathVariable("id") Long id) {
        return productRepo.findById(id)
                          .map(EntityModel::of)
                          .map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

}


package li.selman.dershop.product;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Hasan Selman Kara
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}

package li.selman.dershop.product;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

/**
 * @author Hasan Selman Kara
 */
@Component
public class ProductResourceProcessor implements RepresentationModelProcessor<EntityModel<Product>> {

    public static final String IMAGE_REL = "image";

    @Override
    public EntityModel<Product> process(EntityModel<Product> resource) {
        resource.add(Link.of("http:www.s3.amazon.com/product-image/1", IMAGE_REL));
        return resource;
    }
}

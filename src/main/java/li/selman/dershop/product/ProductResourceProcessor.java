package li.selman.dershop.product;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Hasan Selman Kara
 */
@Component
public class ProductResourceProcessor implements RepresentationModelProcessor<EntityModel<Product>> {

    public static final String IMAGE_REL = "image";

    @Override
    public EntityModel<Product> process(EntityModel<Product> resource) {
        if (resource.getContent() == null) {
            return resource;
        }

        resource.add(Link.of(geProductImageUrl(resource.getContent()), IMAGE_REL));
        return resource;
    }

    String geProductImageUrl(Product product) {
        UriTemplate template = UriTemplate.of("http:www.s3.amazon.com/product-image/{id}");
        var uriVariables = Map.entry("id", product.getId().toString());
        return template.expand(uriVariables).toString();
    }
}

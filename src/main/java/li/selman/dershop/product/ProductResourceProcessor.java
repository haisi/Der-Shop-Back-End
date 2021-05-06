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

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

/**
 * @author Hasan Selman Kara
 */
@Component
public class ProductResourceProcessor implements RepresentationModelProcessor<EntityModel<Product>> {

    public static final String IMAGE_REL = "image";

    @Override
    public @NotNull EntityModel<Product> process(EntityModel<Product> resource) {
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

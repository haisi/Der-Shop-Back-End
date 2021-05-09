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

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import li.selman.dershop.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Hasan Selman Kara
 */
@IntegrationTest // TODO(#optimization): Do not start the whole application context
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class ProductControllerTest {

    @Autowired
    private WebApplicationContext context;

    /*
    objectMapper.writeValueAsString(someObj) for posting stuff
     */
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    ProductRepository productRepo;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {

        // @formatter:off
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                    .withRequestDefaults(prettyPrint(), removeHeaders("Host", "Content-Length"))
                    .withResponseDefaults(prettyPrint(), removeHeaders("Content-Length")))
            .alwaysDo(document("{method-name}"))
            .build();
        // @formatter:on
    }

    @Test
    void findAll() throws Exception {
        // given
        when(productRepo.findAll()).thenReturn(List.of(
            new Product(1L, "Product A"),
            new Product(2L, "Product B"),
            new Product(3L, "Product C")
        ));

        FieldDescriptor[] productDescriptor = getProductFieldDescriptor();

        // when
        ResultActions result = this.mockMvc.perform(
            get("/api/products").header("Accept", "application/hal+json"));

        // then
        result
            .andExpect(status().isOk())
            .andExpect(jsonPath("$..products[0].name").value("Product A"))
            .andDo(document("products-findAll", responseFields(productDescriptor)));
    }

    private FieldDescriptor[] getProductFieldDescriptor() {
        return new FieldDescriptor[]{
            fieldWithPath("_links").ignored(),
            fieldWithPath("_links.self").ignored(),
            fieldWithPath("_links.self.href").ignored(),
            fieldWithPath("_embedded").ignored(),
            fieldWithPath("_embedded.products").ignored(),
            fieldWithPath("_embedded.products[].id")
                .description("The unique id of the product entity").type(Long.class.getSimpleName()),
            fieldWithPath("_embedded.products[].name")
                .description("The name of the product").type(String.class.getSimpleName()),

            fieldWithPath("_embedded.products[]._links").ignored(),

            fieldWithPath("_embedded.products[]._links.self")
                .description("Links to the entity itself").type(String.class.getSimpleName()),
            fieldWithPath("_embedded.products[]._links.self.href").ignored(),

            fieldWithPath("_embedded.products[]._links.products")
                .description("Links to all available products").type(String.class.getSimpleName()),
            fieldWithPath("_embedded.products[]._links.products.href").ignored(),

            fieldWithPath("_embedded.products[]._links.image")
                .description("Links to the articles image").type(String.class.getSimpleName()),
            fieldWithPath("_embedded.products[]._links.image.href").ignored(),
        };
    }
}

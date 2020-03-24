package li.selman.dershop.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Hasan Selman Kara
 */
@WebMvcTest(ProductController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class ProductControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    ProductRepository productRepo;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                      .apply(documentationConfiguration(restDocumentation)
                                          .operationPreprocessors()
                                            .withRequestDefaults(prettyPrint(), removeHeaders("Host", "Content-Length"))
                                            .withResponseDefaults(prettyPrint(), removeHeaders("Content-Length")))
                                      .alwaysDo(document("{method-name}"))
                                      .build();
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
        ResultActions result = this.mockMvc.perform(get("/product"));

        // then
        result
            .andExpect(status().isOk())
            .andExpect(jsonPath("$..productList[0].name").value("Product A"))
            .andDo(document("sample", responseFields(productDescriptor)));
    }

    private FieldDescriptor[] getProductFieldDescriptor() {
        return new FieldDescriptor[]{
//            subsectionWithPath("_embedded").ignored(),
            fieldWithPath("_embedded").ignored(),
            fieldWithPath("_embedded.productList").ignored(),
            fieldWithPath("_embedded.productList[].id").description("The unique id of the product entity").type(Long.class.getSimpleName()),
            fieldWithPath("_embedded.productList[].name").description("The name of the product").type(String.class.getSimpleName())
        };
    }
}

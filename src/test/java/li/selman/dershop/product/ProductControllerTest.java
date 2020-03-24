package li.selman.dershop.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Hasan Selman Kara
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ProductRepository productRepo;

    @Test
    void findAll() throws Exception {
        // given
        when(productRepo.findAll()).thenReturn(List.of(
            new Product(1L, "Product A"),
            new Product(2L, "Product B"),
            new Product(3L, "Product C")
        ));

        // when
        ResultActions result = this.mockMvc.perform(get("/product"));

        // then
        result
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Product A")));
    }

}

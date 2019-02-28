package propra2.storage;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import propra2.database.Customer;
import propra2.database.Product;
import propra2.model.Address;
import propra2.repositories.ProductRepository;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileUploadTests {

	@Autowired
	private ProductRepository productRepository;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StorageService storageService;

    @Test
	@WithMockUser(username="tester", password = "passwordtest")
    public void shouldSaveUploadedFile() throws Exception {

    	Product product = new Product();
    	product = productRepository.save(product);

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.png",
                "text/plain", "Spring Framework".getBytes());
        this.mvc.perform(fileUpload("/upload/")
				.file(multipartFile)
				.param("fileName", "test")
				.param("productId", product.getId().toString()))
                .andExpect(status().isOk());

        then(this.storageService).should().store(multipartFile,"test.png", 1L);
    }

    @SuppressWarnings("unchecked")
    @Test
    @WithMockUser(username="tester", password = "passwordtest")
    public void should404WhenMissingFile() throws Exception {
        given(this.storageService.loadAsResource("1",1L))
                .willThrow(StorageFileNotFoundException.class);

        this.mvc.perform(get("/files/1/1")).andExpect(status().isNotFound());
    }

}

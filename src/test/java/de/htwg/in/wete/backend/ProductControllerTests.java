package de.htwg.in.wete.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.in.wete.backend.controller.ProductController;
import de.htwg.in.wete.backend.model.Category;
import de.htwg.in.wete.backend.model.Product;
import de.htwg.in.wete.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setTitle("Test Caciocavallo");
        testProduct.setDescription("Ein leckerer italienischer Käse");
        testProduct.setCategory(Category.KAESE);
        testProduct.setPrice(12.99);
        testProduct.setImageUrl("https://example.com/kaese.jpg");
        testProduct.setImageUrlDetails("https://example.com/kaese-details.jpg");
        testProduct.setIngredients("Milch, Lab, Salz");
    }

    // ========== GET /api/product - List all products ==========

    @Test
    void getProducts_shouldReturnAllProducts() throws Exception {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setTitle("Salsiccia");
        product2.setCategory(Category.SALAMI);
        product2.setPrice(15.99);

        List<Product> products = Arrays.asList(testProduct, product2);
        when(productRepository.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Test Caciocavallo")))
                .andExpect(jsonPath("$[1].title", is("Salsiccia")));

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProducts_shouldReturnEmptyListWhenNoProducts() throws Exception {
        when(productRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productRepository, times(1)).findAll();
    }

    // ========== GET /api/product/{id} - Get single product ==========

    @Test
    void getProductById_shouldReturnProductWhenExists() throws Exception {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/api/product/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Caciocavallo")))
                .andExpect(jsonPath("$.description", is("Ein leckerer italienischer Käse")))
                .andExpect(jsonPath("$.category", is("KAESE")))
                .andExpect(jsonPath("$.price", is(12.99)))
                .andExpect(jsonPath("$.imageUrl", is("https://example.com/kaese.jpg")));

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_shouldReturn404WhenNotFound() throws Exception {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/product/999"))
                .andExpect(status().isNotFound());

        verify(productRepository, times(1)).findById(999L);
    }

    // ========== POST /api/product - Create product ==========

    @Test
    void createProduct_shouldReturn201AndCreatedProduct() throws Exception {
        Product newProduct = new Product();
        newProduct.setTitle("Neuer Käse");
        newProduct.setDescription("Beschreibung");
        newProduct.setCategory(Category.KAESE);
        newProduct.setPrice(9.99);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setTitle("Neuer Käse");
        savedProduct.setDescription("Beschreibung");
        savedProduct.setCategory(Category.KAESE);
        savedProduct.setPrice(9.99);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Neuer Käse")))
                .andExpect(jsonPath("$.category", is("KAESE")))
                .andExpect(jsonPath("$.price", is(9.99)));

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_shouldIgnoreProvidedIdAndCreateNewProduct() throws Exception {
        Product productWithId = new Product();
        productWithId.setId(999L); // ID should be ignored
        productWithId.setTitle("Produkt mit ID");
        productWithId.setCategory(Category.BROT);
        productWithId.setPrice(4.99);

        Product savedProduct = new Product();
        savedProduct.setId(1L); // New generated ID
        savedProduct.setTitle("Produkt mit ID");
        savedProduct.setCategory(Category.BROT);
        savedProduct.setPrice(4.99);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productWithId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1))); // Should have new ID, not 999

        verify(productRepository, times(1)).save(any(Product.class));
    }

    // ========== PUT /api/product/{id} - Update product ==========

    @Test
    void updateProduct_shouldReturnUpdatedProductWhenExists() throws Exception {
        Product updateDetails = new Product();
        updateDetails.setTitle("Updated Title");
        updateDetails.setDescription("Updated Description");
        updateDetails.setCategory(Category.SALAMI);
        updateDetails.setPrice(19.99);
        updateDetails.setImageUrl("https://example.com/updated.jpg");

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setTitle("Original Title");
        existingProduct.setCategory(Category.KAESE);
        existingProduct.setPrice(12.99);

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setTitle("Updated Title");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setCategory(Category.SALAMI);
        updatedProduct.setPrice(19.99);
        updatedProduct.setImageUrl("https://example.com/updated.jpg");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.category", is("SALAMI")))
                .andExpect(jsonPath("$.price", is(19.99)));

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_shouldReturn404WhenNotFound() throws Exception {
        Product updateDetails = new Product();
        updateDetails.setTitle("Updated Title");
        updateDetails.setCategory(Category.KAESE);
        updateDetails.setPrice(9.99);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/product/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isNotFound());

        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    // ========== DELETE /api/product/{id} - Delete product ==========

    @Test
    void deleteProduct_shouldReturn204WhenSuccessful() throws Exception {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(any(Product.class));

        mockMvc.perform(delete("/api/product/1"))
                .andExpect(status().isNoContent());

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    void deleteProduct_shouldReturn404WhenNotFound() throws Exception {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/product/999"))
                .andExpect(status().isNotFound());

        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).delete(any(Product.class));
    }
}

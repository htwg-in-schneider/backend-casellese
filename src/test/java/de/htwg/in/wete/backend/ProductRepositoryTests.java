package de.htwg.in.wete.backend;

import de.htwg.in.wete.backend.model.Category;
import de.htwg.in.wete.backend.model.Product;
import de.htwg.in.wete.backend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("local") // Use H2 in-memory database for tests
class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSaveProduct() {
        Product product = new Product();
        product.setTitle("Test Caciocavallo");
        product.setDescription("Ein Test-Käse für Unit-Tests");
        product.setCategory(Category.KAESE);
        product.setPrice(7.99);
        product.setImageUrl("https://example.com/kaese.jpg");

        Product savedProduct = productRepository.save(product);

        assertNotNull(savedProduct.getId());
        assertEquals("Test Caciocavallo", savedProduct.getTitle());
        assertEquals(Category.KAESE, savedProduct.getCategory());
    }

    @Test
    void testFindById() {
        Product product = new Product();
        product.setTitle("Find Me Salsiccia");
        product.setDescription("Eine Salsiccia zum Finden");
        product.setCategory(Category.SALAMI);
        product.setPrice(15.99);
        product.setImageUrl("https://example.com/salami.jpg");

        Product savedProduct = productRepository.save(product);

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals("Find Me Salsiccia", foundProduct.get().getTitle());
    }

    @Test
    void testFindAll() {
        Product product1 = new Product();
        product1.setTitle("Käse 1");
        product1.setCategory(Category.KAESE);
        product1.setPrice(7.99);

        Product product2 = new Product();
        product2.setTitle("Brot 1");
        product2.setCategory(Category.BROT);
        product2.setPrice(4.99);

        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> allProducts = productRepository.findAll();

        assertTrue(allProducts.size() >= 2);
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product();
        product.setTitle("Delete Me");
        product.setCategory(Category.BROT);
        product.setPrice(4.99);

        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();

        productRepository.deleteById(productId);

        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    void testUpdateProduct() {
        Product product = new Product();
        product.setTitle("Original Title");
        product.setCategory(Category.KAESE);
        product.setPrice(7.99);

        Product savedProduct = productRepository.save(product);
        savedProduct.setTitle("Updated Title");
        savedProduct.setPrice(9.99);

        Product updatedProduct = productRepository.save(savedProduct);

        assertEquals("Updated Title", updatedProduct.getTitle());
        assertEquals(9.99, updatedProduct.getPrice());
    }
}

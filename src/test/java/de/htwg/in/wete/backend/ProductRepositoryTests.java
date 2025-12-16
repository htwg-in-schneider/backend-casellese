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

    // ========== Search and Filter Tests ==========

    @Test
    void testFindByCategory() {
        Product kaese1 = new Product();
        kaese1.setTitle("Caciocavallo");
        kaese1.setCategory(Category.KAESE);
        kaese1.setPrice(12.99);

        Product kaese2 = new Product();
        kaese2.setTitle("Mozzarella");
        kaese2.setCategory(Category.KAESE);
        kaese2.setPrice(8.99);

        Product salami = new Product();
        salami.setTitle("Salsiccia");
        salami.setCategory(Category.SALAMI);
        salami.setPrice(15.99);

        productRepository.save(kaese1);
        productRepository.save(kaese2);
        productRepository.save(salami);

        List<Product> kaeseProducts = productRepository.findByCategory(Category.KAESE);

        assertEquals(2, kaeseProducts.size());
        assertTrue(kaeseProducts.stream().allMatch(p -> p.getCategory() == Category.KAESE));
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        Product product1 = new Product();
        product1.setTitle("Caciocavallo Silano");
        product1.setCategory(Category.KAESE);
        product1.setPrice(12.99);

        Product product2 = new Product();
        product2.setTitle("Mozzarella di Bufala");
        product2.setCategory(Category.KAESE);
        product2.setPrice(8.99);

        productRepository.save(product1);
        productRepository.save(product2);

        // Test case-insensitive search
        List<Product> results = productRepository.findByTitleContainingIgnoreCase("cacio");
        assertEquals(1, results.size());
        assertEquals("Caciocavallo Silano", results.get(0).getTitle());

        // Test partial match
        List<Product> mozResults = productRepository.findByTitleContainingIgnoreCase("mozzarella");
        assertEquals(1, mozResults.size());
        assertEquals("Mozzarella di Bufala", mozResults.get(0).getTitle());
    }

    @Test
    void testFindByTitleContainingIgnoreCaseAndCategory() {
        Product kaese1 = new Product();
        kaese1.setTitle("Caciocavallo Silano");
        kaese1.setCategory(Category.KAESE);
        kaese1.setPrice(12.99);

        Product kaese2 = new Product();
        kaese2.setTitle("Pecorino Romano");
        kaese2.setCategory(Category.KAESE);
        kaese2.setPrice(18.99);

        Product salami = new Product();
        salami.setTitle("Salsiccia Calabrese");
        salami.setCategory(Category.SALAMI);
        salami.setPrice(15.99);

        productRepository.save(kaese1);
        productRepository.save(kaese2);
        productRepository.save(salami);

        // Search for "cacio" in KAESE category
        List<Product> results = productRepository.findByTitleContainingIgnoreCaseAndCategory("cacio", Category.KAESE);
        assertEquals(1, results.size());
        assertEquals("Caciocavallo Silano", results.get(0).getTitle());

        // Search for "Sal" in SALAMI category
        List<Product> salamiResults = productRepository.findByTitleContainingIgnoreCaseAndCategory("Sal", Category.SALAMI);
        assertEquals(1, salamiResults.size());
        assertEquals("Salsiccia Calabrese", salamiResults.get(0).getTitle());

        // Search for non-matching combination
        List<Product> emptyResults = productRepository.findByTitleContainingIgnoreCaseAndCategory("cacio", Category.SALAMI);
        assertTrue(emptyResults.isEmpty());
    }
}

// Iteration 8: 3 neue Tests für Repository

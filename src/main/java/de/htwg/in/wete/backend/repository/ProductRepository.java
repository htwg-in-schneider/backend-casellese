package de.htwg.in.wete.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.htwg.in.wete.backend.model.Category;
import de.htwg.in.wete.backend.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategory(Category category);
    
    List<Product> findByTitleContainingIgnoreCase(String name);
    
    List<Product> findByTitleContainingIgnoreCaseAndCategory(String name, Category category);
}

// Iteration 8: 3 neue Suchmethoden
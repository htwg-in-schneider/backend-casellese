// src/main/java/de/htwg/in/wete/backend/repository/FavoriteRepository.java
package de.htwg.in.wete.backend.repository;

import de.htwg.in.wete.backend.model.Favorite;
import de.htwg.in.wete.backend.model.User;
import de.htwg.in.wete.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<Favorite> findByUserAndProduct(User user, Product product);
    
    boolean existsByUserAndProduct(User user, Product product);
    
    void deleteByUserAndProduct(User user, Product product);
    
    @Query("SELECT f.product.id FROM Favorite f WHERE f.user = :user")
    List<Long> findProductIdsByUser(User user);
    
    long countByProduct(Product product);
}
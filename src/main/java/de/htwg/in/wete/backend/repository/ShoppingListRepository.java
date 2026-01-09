// src/main/java/de/htwg/in/wete/backend/repository/ShoppingListRepository.java
package de.htwg.in.wete.backend.repository;

import de.htwg.in.wete.backend.model.ShoppingListItem;
import de.htwg.in.wete.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingListItem, Long> {
    
    List<ShoppingListItem> findByUserOrderByCheckedAscCreatedAtDesc(User user);
    
    List<ShoppingListItem> findByUserAndCheckedFalseOrderByCreatedAtDesc(User user);
    
    @Modifying
    @Query("DELETE FROM ShoppingListItem s WHERE s.user = :user AND s.checked = true")
    void deleteCheckedByUser(User user);
    
    @Modifying
    @Query("DELETE FROM ShoppingListItem s WHERE s.user = :user")
    void deleteAllByUser(User user);
    
    long countByUserAndCheckedFalse(User user);
}
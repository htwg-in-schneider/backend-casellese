package de.htwg.in.wete.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.htwg.in.wete.backend.model.Recipe;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByProductId(Long productId);
}

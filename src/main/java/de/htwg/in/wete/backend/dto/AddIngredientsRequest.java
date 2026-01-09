// src/main/java/de/htwg/in/wete/backend/dto/AddIngredientsRequest.java
package de.htwg.in.wete.backend.dto;

import java.util.List;

public class AddIngredientsRequest {
    private Long productId;
    private List<String> ingredients;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
}
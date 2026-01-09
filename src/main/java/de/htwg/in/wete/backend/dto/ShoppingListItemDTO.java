// src/main/java/de/htwg/in/wete/backend/dto/ShoppingListItemDTO.java
package de.htwg.in.wete.backend.dto;

public class ShoppingListItemDTO {
    private Long id;
    private String ingredient;
    private String category;
    private Long sourceProductId;
    private String sourceProductTitle;
    private boolean checked;

    // Konstruktoren
    public ShoppingListItemDTO() {}

    public ShoppingListItemDTO(Long id, String ingredient, String category, 
                               Long sourceProductId, String sourceProductTitle, boolean checked) {
        this.id = id;
        this.ingredient = ingredient;
        this.category = category;
        this.sourceProductId = sourceProductId;
        this.sourceProductTitle = sourceProductTitle;
        this.checked = checked;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIngredient() { return ingredient; }
    public void setIngredient(String ingredient) { this.ingredient = ingredient; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getSourceProductId() { return sourceProductId; }
    public void setSourceProductId(Long sourceProductId) { this.sourceProductId = sourceProductId; }

    public String getSourceProductTitle() { return sourceProductTitle; }
    public void setSourceProductTitle(String sourceProductTitle) { this.sourceProductTitle = sourceProductTitle; }

    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
}
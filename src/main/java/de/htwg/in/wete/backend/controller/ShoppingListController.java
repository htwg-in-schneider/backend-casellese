// src/main/java/de/htwg/in/wete/backend/controller/ShoppingListController.java
package de.htwg.in.wete.backend.controller;

import de.htwg.in.wete.backend.dto.AddIngredientsRequest;
import de.htwg.in.wete.backend.dto.ShoppingListItemDTO;
import de.htwg.in.wete.backend.model.Product;
import de.htwg.in.wete.backend.model.ShoppingListItem;
import de.htwg.in.wete.backend.model.User;
import de.htwg.in.wete.backend.repository.ProductRepository;
import de.htwg.in.wete.backend.repository.ShoppingListRepository;
import de.htwg.in.wete.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingListController.class);

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private Optional<User> getUserFromJwt(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            return Optional.empty();
        }
        return userRepository.findByOauthId(jwt.getSubject());
    }

    private ShoppingListItemDTO toDTO(ShoppingListItem item) {
        return new ShoppingListItemDTO(
            item.getId(),
            item.getIngredient(),
            item.getCategory(),
            item.getSourceProduct() != null ? item.getSourceProduct().getId() : null,
            item.getSourceProduct() != null ? item.getSourceProduct().getTitle() : null,
            item.isChecked()
        );
    }

    // GET /api/shopping-list - Alle Items abrufen
    @GetMapping
    public ResponseEntity<List<ShoppingListItemDTO>> getShoppingList(@AuthenticationPrincipal Jwt jwt) {
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        List<ShoppingListItemDTO> items = shoppingListRepository
            .findByUserOrderByCheckedAscCreatedAtDesc(userOpt.get())
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(items);
    }

    // POST /api/shopping-list - Einzelnes Item hinzufügen
    @PostMapping
    public ResponseEntity<ShoppingListItemDTO> addItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> request) {
        
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        String ingredient = request.get("ingredient");
        if (ingredient == null || ingredient.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ShoppingListItem item = new ShoppingListItem();
        item.setUser(userOpt.get());
        item.setIngredient(ingredient.trim());
        item.setCategory(request.get("category"));

        ShoppingListItem saved = shoppingListRepository.save(item);
        LOG.info("User {} added item to shopping list: {}", userOpt.get().getId(), ingredient);

        return ResponseEntity.ok(toDTO(saved));
    }

    // POST /api/shopping-list/from-product - Zutaten aus Produkt hinzufügen
    @PostMapping("/from-product")
    public ResponseEntity<Map<String, Object>> addFromProduct(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AddIngredientsRequest request) {
        
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        Product product = productOpt.get();
        List<ShoppingListItemDTO> addedItems = new ArrayList<>();

        for (String ingredient : request.getIngredients()) {
            if (ingredient != null && !ingredient.trim().isEmpty()) {
                ShoppingListItem item = new ShoppingListItem();
                item.setUser(user);
                item.setIngredient(ingredient.trim());
                item.setSourceProduct(product);
                
                ShoppingListItem saved = shoppingListRepository.save(item);
                addedItems.add(toDTO(saved));
            }
        }

        LOG.info("User {} added {} items from product {}", user.getId(), addedItems.size(), product.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", addedItems.size() + " Zutaten hinzugefügt");
        response.put("items", addedItems);
        return ResponseEntity.ok(response);
    }

    // PUT /api/shopping-list/{id}/toggle - Item abhaken/abwählen
    @PutMapping("/{id}/toggle")
    public ResponseEntity<ShoppingListItemDTO> toggleItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Optional<ShoppingListItem> itemOpt = shoppingListRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ShoppingListItem item = itemOpt.get();
        
        // Sicherheitscheck: Gehört Item dem User?
        if (!item.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(403).build();
        }

        item.setChecked(!item.isChecked());
        ShoppingListItem saved = shoppingListRepository.save(item);

        return ResponseEntity.ok(toDTO(saved));
    }

    // DELETE /api/shopping-list/{id} - Einzelnes Item löschen
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Optional<ShoppingListItem> itemOpt = shoppingListRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ShoppingListItem item = itemOpt.get();
        if (!item.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(403).build();
        }

        shoppingListRepository.delete(item);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/shopping-list/checked - Alle abgehakten Items löschen
    @DeleteMapping("/checked")
    @Transactional
    public ResponseEntity<Map<String, String>> deleteChecked(@AuthenticationPrincipal Jwt jwt) {
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        shoppingListRepository.deleteCheckedByUser(userOpt.get());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Abgehakte Items gelöscht");
        return ResponseEntity.ok(response);
    }

    // DELETE /api/shopping-list/all - Gesamte Liste löschen
    @DeleteMapping("/all")
    @Transactional
    public ResponseEntity<Map<String, String>> deleteAll(@AuthenticationPrincipal Jwt jwt) {
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        shoppingListRepository.deleteAllByUser(userOpt.get());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Einkaufsliste geleert");
        return ResponseEntity.ok(response);
    }

    // GET /api/shopping-list/count - Anzahl offener Items
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCount(@AuthenticationPrincipal Jwt jwt) {
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        long count = shoppingListRepository.countByUserAndCheckedFalse(userOpt.get());
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}

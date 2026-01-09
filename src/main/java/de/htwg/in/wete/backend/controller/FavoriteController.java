// src/main/java/de/htwg/in/wete/backend/controller/FavoriteController.java
package de.htwg.in.wete.backend.controller;

import de.htwg.in.wete.backend.model.Favorite;
import de.htwg.in.wete.backend.model.Product;
import de.htwg.in.wete.backend.model.User;
import de.htwg.in.wete.backend.repository.FavoriteRepository;
import de.htwg.in.wete.backend.repository.ProductRepository;
import de.htwg.in.wete.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteController.class);

    @Autowired
    private FavoriteRepository favoriteRepository;

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

    // GET /api/favorites - Alle Favoriten des Users abrufen
    @GetMapping
    public ResponseEntity<List<Product>> getFavorites(@AuthenticationPrincipal Jwt jwt) {
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        List<Product> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(userOpt.get())
                .stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());

        return ResponseEntity.ok(favorites);
    }

    // GET /api/favorites/ids - Nur IDs der Favoriten (für schnelle Checks)
    @GetMapping("/ids")
    public ResponseEntity<List<Long>> getFavoriteIds(@AuthenticationPrincipal Jwt jwt) {
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        List<Long> ids = favoriteRepository.findProductIdsByUser(userOpt.get());
        return ResponseEntity.ok(ids);
    }

    // GET /api/favorites/check/{productId} - Prüfen ob Produkt favorisiert ist
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId) {
        
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean isFavorite = favoriteRepository.existsByUserAndProduct(userOpt.get(), productOpt.get());
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFavorite", isFavorite);
        return ResponseEntity.ok(response);
    }

    // POST /api/favorites/{productId} - Favorit hinzufügen
    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId) {
        
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        Product product = productOpt.get();

        // Prüfen ob bereits favorisiert
        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bereits in Favoriten");
            response.put("isFavorite", true);
            return ResponseEntity.ok(response);
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);

        LOG.info("User {} added product {} to favorites", user.getId(), productId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Zu Favoriten hinzugefügt");
        response.put("isFavorite", true);
        return ResponseEntity.ok(response);
    }

    // DELETE /api/favorites/{productId} - Favorit entfernen
    @DeleteMapping("/{productId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId) {
        
        Optional<User> userOpt = getUserFromJwt(jwt);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        favoriteRepository.deleteByUserAndProduct(userOpt.get(), productOpt.get());
        
        LOG.info("User {} removed product {} from favorites", userOpt.get().getId(), productId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Von Favoriten entfernt");
        response.put("isFavorite", false);
        return ResponseEntity.ok(response);
    }

    // GET /api/favorites/count/{productId} - Anzahl der Favorisierungen (öffentlich)
    @GetMapping("/count/{productId}")
    public ResponseEntity<Map<String, Long>> getFavoriteCount(@PathVariable Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        long count = favoriteRepository.countByProduct(productOpt.get());
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}
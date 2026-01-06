package de.htwg.in.wete.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import de.htwg.in.wete.backend.model.Role;
import de.htwg.in.wete.backend.model.User;
import de.htwg.in.wete.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String oauthId = jwt.getSubject();
        LOGGER.info("getProfile called for principal: {}", oauthId);
        LOGGER.debug("JWT claims: {}", jwt.getClaims());
        
        if (oauthId == null) {
            LOGGER.warn("JWT does not contain 'sub' claim");
            return ResponseEntity.badRequest().build();
        }
        
        // Suche nach existierendem User oder erstelle neuen User mit REGULAR-Rolle
        User user = userRepository.findByOauthId(oauthId)
                .orElseGet(() -> {
                    LOGGER.info("Creating new user with oauthId: {}", oauthId);
                    User newUser = new User();
                    newUser.setOauthId(oauthId);
                    // Name und Email aus JWT-Claims extrahieren
                    newUser.setName(jwt.getClaimAsString("name"));
                    newUser.setEmail(jwt.getClaimAsString("email"));
                    // Neue User bekommen standardmäßig die REGULAR-Rolle
                    newUser.setRole(Role.REGULAR);
                    return userRepository.save(newUser);
                });
        
        return ResponseEntity.ok(user);
    }
}

## Iteration 10: Admin-Privilegien für Produktverwaltung

### Übersicht
Nur Benutzer mit `ADMIN`-Rolle können Produkte erstellen, ändern oder löschen.

### Geänderte Dateien
| Datei | Änderung |
|-------|----------|
| `SecurityConfig.java` | POST/PUT/DELETE auf `/api/product/**` erfordern Authentifizierung |
| `ProductController.java` | ADMIN-Rollenprüfung für schreibende Operationen |
| `ProductControllerTests.java` | Tests mit simuliertem Admin-JWT |

### SecurityConfig - Endpoint-Schutz
```java
.requestMatchers(HttpMethod.POST, "/api/product", "/api/product/*").authenticated()
.requestMatchers(HttpMethod.PUT, "/api/product/*").authenticated()
.requestMatchers(HttpMethod.DELETE, "/api/product/*").authenticated()
.requestMatchers(HttpMethod.GET, "/api/product", "/api/product/*").permitAll()
```

### ProductController - Admin-Prüfung
```java
@Autowired
private UserRepository userRepository;

private boolean userFromJwtIsAdmin(Jwt jwt) {
    if (jwt == null || jwt.getSubject() == null) return false;
    Optional<User> user = userRepository.findByOauthId(jwt.getSubject());
    return user.isPresent() && user.get().getRole() == Role.ADMIN;
}

@PostMapping
public ResponseEntity<Product> createProduct(@AuthenticationPrincipal Jwt jwt, @RequestBody Product product) {
    if (!userFromJwtIsAdmin(jwt)) {
        return ResponseEntity.status(403).build();  // Forbidden
    }
    // ... Produkt erstellen
}
```

### Wichtige Imports (ProductController)
```java
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import de.htwg.in.wete.backend.model.Role;
import de.htwg.in.wete.backend.model.User;
import de.htwg.in.wete.backend.repository.UserRepository;
```

### Tests mit Mock-JWT
```java
mockMvc.perform(post("/api/product")
    .with(jwt().jwt(jwt -> jwt.claim("sub", "auth0|admin")))
    .contentType(MediaType.APPLICATION_JSON)
    .content(productPayload))
    .andExpect(status().isOk());
```

### Wichtige Hinweise
- `sub` Claim im JWT muss mit `oauthId` eines Users mit `Role.ADMIN` übereinstimmen
- GET-Requests bleiben öffentlich (kein Token nötig)
- Ohne Admin-Rolle → **403 Forbidden**
- Ohne Token → **401 Unauthorized**
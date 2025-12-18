package de.htwg.in.wete.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import de.htwg.in.wete.backend.model.Category;
import de.htwg.in.wete.backend.model.Product;
import de.htwg.in.wete.backend.model.Recipe;
import de.htwg.in.wete.backend.model.User;
import de.htwg.in.wete.backend.model.Role;
import de.htwg.in.wete.backend.repository.ProductRepository;
import de.htwg.in.wete.backend.repository.RecipeRepository;
import de.htwg.in.wete.backend.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@Profile("!test")
public class DataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository, ProductRepository repository, RecipeRepository recipeRepository) {
        return args -> {
            loadInitialUsers(userRepository);

            // only load products and recipes if none exist
            if (repository.count() == 0) {
                LOGGER.info("Database is empty. Loading initial data...");
                loadInitialData(repository, recipeRepository);
            } else {
                LOGGER.info("Database already contains data. Skipping data loading.");
            }
        };
    }

    /**
     * Load initial user data.
     * 
     * IMPORTANT: When copying this code, you need to:
     * 1. Create users in your Auth0 tenant first
     * 2. Replace the oauthId values with the actual 'sub' claim from Auth0
     *    Format is typically: "auth0|123456789" for Auth0 users -> auth0|6943dcd996d00876f0d33277
     */
    private void loadInitialUsers(UserRepository userRepository) {
        // Replace these with your actual Auth0 user IDs!
        // upsertUser(userRepository, "Carmine Savino", "carmine@mysavino.com", "auth0|6943dcd996d00876f0d33277", Role.ADMIN);
        // upsertUser(userRepository, "Regular User", "admin@example.com", "auth0|REPLACE_WITH_YOUR_ADMIN_ID", Role.REGULAR);
        // Für Machine-to-Machine Token 
        upsertUser(userRepository, "API Client", "api@casellese.local", "YA6xaTr1pV4JUBJsDf0SPFlzWjciue1d@clients", Role.ADMIN);
    }
    // upsertUser(userRepository, "Dein Name", "deine@email.com", "auth0|DEINE_ECHTE_ID", Role.REGULAR);

    private void upsertUser(UserRepository userRepository, String name, String email, String oauthId, Role role) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User e = existing.get();
            e.setName(name);
            e.setOauthId(oauthId);
            e.setRole(role);
            userRepository.save(e);
            LOGGER.info("Updated existing {} user with email={}", role, email);
        } else {
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setOauthId(oauthId);
            u.setRole(role);
            userRepository.save(u);
            LOGGER.info("Created new {} user with email={}", role, email);
        }
    }

    private void loadInitialData(ProductRepository repository, RecipeRepository recipeRepository) {
        // Produkt 1: Caciocavallo (Käse)
        Product caciocavallo = new Product();
        caciocavallo.setTitle("Caciocavallo");
        caciocavallo.setDescription("Caciocavallo ist ein italienischer Kult-Käse mit unverwechselbarer birnenförmiger Gestalt, der traditionell paarweise an der Schnur hängt");
        caciocavallo.setCategory(Category.KAESE);
        caciocavallo.setPrice(7.99);
        caciocavallo.setImageUrl("http://nucccio.github.io/casellese-images/caciocavallo.webp");
        caciocavallo.setImageUrlDetails("http://nucccio.github.io/casellese-images/caciocavallo-rezepte.webp");
        caciocavallo.setIngredients("Kuhmilch, Lab, Salz, Konservierungsstoff: Natriumbenzoat (E211)");

        // Produkt 2: Salsiccia (Salami)
        Product salsiccia = new Product();
        salsiccia.setTitle("Salsiccia");
        salsiccia.setDescription("Salsiccia ist eine traditionelle italienische Wurst aus fein gewürztem Schweinefleisch, oft mit Knoblauch, Fenchel oder Chili verfeinert.");
        salsiccia.setCategory(Category.SALAMI);
        salsiccia.setPrice(15.99);
        salsiccia.setImageUrl("http://nucccio.github.io/casellese-images/salsiccia.webp");
        salsiccia.setImageUrlDetails("http://nucccio.github.io/casellese-images/salsiccia-rezepte.webp");
        salsiccia.setIngredients("Schweinefleisch, Salz, Knoblauch, Fenchelsamen, Paprika, schwarzer Pfeffer");

        // Produkt 3: Brot
        Product brot = new Product();
        brot.setTitle("Brot");
        brot.setDescription("Brot ist das zeitlose Grundnahrungsmittel, frisch gebacken mit knuspriger Kruste und weichem Inneren.");
        brot.setCategory(Category.BROT);
        brot.setPrice(4.99);
        brot.setImageUrl("http://nucccio.github.io/casellese-images/brot.webp");
        brot.setImageUrlDetails("http://nucccio.github.io/casellese-images/brot-rezepte.webp");
        brot.setIngredients("Weizenmehl, Wasser, Hefe, Salz, Olivenöl");

        repository.saveAll(Arrays.asList(caciocavallo, salsiccia, brot));

        // Rezept für Caciocavallo
        Recipe caciocavalloRezept = new Recipe();
        caciocavalloRezept.setTitle("Überbackene Caciocavallo-Scheiben");
        caciocavalloRezept.setText("""
## Zutaten
- 200g Caciocavallo
- 2 EL Olivenöl
- Frischer Oregano
- 1 Knoblauchzehe

## Zubereitung
1. Käse in ca. 1cm dicke Scheiben schneiden
2. Olivenöl in einer Pfanne erhitzen
3. Käsescheiben von beiden Seiten goldbraun braten (ca. 2 Min. pro Seite)
4. Mit gehacktem Knoblauch und Oregano bestreuen
5. Sofort servieren, solange der Käse noch warm und cremig ist

## Tipp
Dazu passt frisches Brot und ein Glas Rotwein!
            """);
        caciocavalloRezept.setProduct(caciocavallo);

        // Rezept für Salsiccia
        Recipe salsicciRezept = new Recipe();
        salsicciRezept.setTitle("Pasta mit Salsiccia");
        salsicciRezept.setText("""
## Zutaten
- 400g Pasta (Rigatoni oder Penne)
- 300g Salsiccia
- 400g passierte Tomaten
- 1 Zwiebel
- 2 Knoblauchzehen
- Frischer Basilikum
- Parmesan

## Zubereitung
1. Salsiccia aus der Haut drücken und in kleine Stücke zerteilen
2. Zwiebel und Knoblauch fein hacken
3. In Olivenöl die Salsiccia anbraten bis sie goldbraun ist
4. Zwiebel und Knoblauch hinzufügen und glasig dünsten
5. Passierte Tomaten hinzufügen und 15 Min. köcheln lassen
6. Pasta al dente kochen und mit der Sauce vermischen
7. Mit Parmesan und frischem Basilikum servieren
            """);
        salsicciRezept.setProduct(salsiccia);

        // Rezept für Brot
        Recipe brotRezept = new Recipe();
        brotRezept.setTitle("Bruschetta Classica");
        brotRezept.setText("""
## Zutaten
- 4 Scheiben Brot
- 4 reife Tomaten
- 2 Knoblauchzehen
- Frischer Basilikum
- Olivenöl extra vergine
- Salz und Pfeffer

## Zubereitung
1. Brotscheiben im Ofen oder auf dem Grill rösten
2. Tomaten würfeln und mit gehacktem Basilikum, Salz und Olivenöl mischen
3. Geröstetes Brot mit einer halbierten Knoblauchzehe einreiben
4. Tomatenmischung großzügig auf das Brot geben
5. Mit einem Schuss Olivenöl beträufeln und sofort servieren
            """);
        brotRezept.setProduct(brot);

        recipeRepository.saveAll(Arrays.asList(caciocavalloRezept, salsicciRezept, brotRezept));

        LOGGER.info("Initial data loaded successfully.");
    }
}
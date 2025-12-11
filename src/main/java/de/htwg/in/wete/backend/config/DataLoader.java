package de.htwg.in.wete.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.htwg.in.wete.backend.model.Category;
import de.htwg.in.wete.backend.model.Product;
import de.htwg.in.wete.backend.model.Recipe;
import de.htwg.in.wete.backend.repository.ProductRepository;
import de.htwg.in.wete.backend.repository.RecipeRepository;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    public CommandLineRunner loadData(ProductRepository repository, RecipeRepository recipeRepository) {
        return args -> {
            if (repository.count() == 0) {
                LOGGER.info("Database is empty. Loading initial data...");
                loadInitialData(repository, recipeRepository);
            } else {
                LOGGER.info("Database already contains data. Skipping data loading.");
            }
        };
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
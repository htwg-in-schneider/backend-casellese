package de.htwg.in.wete.backend.controller;

import org.springframework.web.bind.annotation.*;

import de.htwg.in.wete.backend.model.Category;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @GetMapping
    public List<CategoryDTO> getCategories() {
        return Arrays.stream(Category.values())
                .map(cat -> new CategoryDTO(cat.name(), cat.getGermanName()))
                .collect(Collectors.toList());
    }

    public static class CategoryDTO {
        private String name;
        private String germanName;

        public CategoryDTO(String name, String germanName) {
            this.name = name;
            this.germanName = germanName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGermanName() {
            return germanName;
        }

        public void setGermanName(String germanName) {
            this.germanName = germanName;
        }
    }
}

// Iteration 8: Neuer Controller für /api/category
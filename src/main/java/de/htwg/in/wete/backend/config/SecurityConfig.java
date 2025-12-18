// Echte Auth0 Authentifizierung

package de.htwg.in.wete.backend.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .authorizeHttpRequests((authorize) -> authorize
                        // Profile endpoint requires authentication
                        .requestMatchers("/api/profile").authenticated()
                        // Product write operations require authentication
                        .requestMatchers(HttpMethod.POST, "/api/product", "/api/product/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/product/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/product/*").authenticated()
                        // Product read operations are public
                        .requestMatchers(HttpMethod.GET, "/api/product", "/api/product/*").permitAll()
                        // All other API endpoints are public
                        .requestMatchers("/api/**").permitAll())
                            //.anyRequest().permitAll())               // für lokale Auth0 Tests -> http://localhost:8081/api/product
                            //.requestMatchers("/api/**").permitAll()) // für echte Auth0 Authentifizierung
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(withDefaults()))
                .build();
    }
}


/*

// Lokale Auth0 Tests
package de.htwg.in.wete.backend.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll())
                .build();
    }
}

*/

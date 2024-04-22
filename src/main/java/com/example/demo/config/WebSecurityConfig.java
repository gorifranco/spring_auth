package com.example.demo.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;


@Configuration
public class WebSecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests((authorize) -> authorize
        .anyRequest().fullyAuthenticated()
      )
      .formLogin(Customizer.withDefaults());
      

    return http.build();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth
          .inMemoryAuthentication()
          .withUser("admin")
          .password("{bcrypt}$2a$10$MHEtjV/agkmHsfcK899gvuE83KGNk.vcTwxxW.It0OQ.yW14MbuvS")
          .roles("ADMIN");
  }
}
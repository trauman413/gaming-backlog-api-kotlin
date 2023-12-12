package com.gamingbacklog.api.gamingbacklogapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class GamingBacklogApiApplication

fun main(args: Array<String>) {
	runApplication<GamingBacklogApiApplication>(*args)
}

@Configuration
class CorsConfig {

  @Bean
  fun corsConfigurer(): WebMvcConfigurer {
    return object : WebMvcConfigurer {
      override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
          .allowedOrigins("http://localhost:3000")
          .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
          .allowedHeaders("*")
      }
    }
  }
}

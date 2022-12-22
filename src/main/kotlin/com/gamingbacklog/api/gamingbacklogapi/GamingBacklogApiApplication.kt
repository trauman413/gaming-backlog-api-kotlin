package com.gamingbacklog.api.gamingbacklogapi

import com.gamingbacklog.api.gamingbacklogapi.repositories.LibraryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GamingBacklogApiApplication

fun main(args: Array<String>) {
	runApplication<GamingBacklogApiApplication>(*args)
}

package com.gamingbacklog.api.gamingbacklogapi.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController {
    /**
     * Hello world
     */
    @GetMapping("/")
    fun helloWorld(
            @RequestParam(value = "name", defaultValue = "World") name: String
    ) : String {
        return "Hello ${name}!"
    }
}
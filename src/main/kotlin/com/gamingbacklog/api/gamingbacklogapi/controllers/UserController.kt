package com.gamingbacklog.api.gamingbacklogapi.controllers

import com.gamingbacklog.api.gamingbacklogapi.models.User
import com.gamingbacklog.api.gamingbacklogapi.models.requests.UserRequest
import com.gamingbacklog.api.gamingbacklogapi.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

  @CrossOrigin(origins = ["http://localhost:3000"])
  @GetMapping("/")
  fun getAllUsers(): ResponseEntity<List<User>> {
    val users = userService.getAll()
    return ResponseEntity.ok(users)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @GetMapping("/{id}")
  fun getSingleUser(
    @PathVariable("id") id: String
  ): ResponseEntity<User> {
    val user = userService.getSingle(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
    return ResponseEntity.ok(user)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @PostMapping("/")
  fun createUser(
    @RequestBody userRequest: UserRequest
  ): ResponseEntity<User> {
    val user = userService.create(userRequest)
    return ResponseEntity<User>(user, HttpStatus.CREATED)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @PatchMapping("/{id}")
  fun updateUserInfo(
    @PathVariable("id") id: String,
    @RequestBody userRequest: Map<String, String>
  ): ResponseEntity<User> {
    val user = userService.updateUserFields(id, userRequest) ?: return ResponseEntity<User>(null, HttpStatus.NOT_FOUND)
    return ResponseEntity<User>(user, HttpStatus.OK)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @DeleteMapping("/{id}")
  fun deleteUser(
    @PathVariable("id") id: String,
  ): ResponseEntity<String> {
    userService.delete(id)
    return ResponseEntity(HttpStatus.NO_CONTENT)
  }
}
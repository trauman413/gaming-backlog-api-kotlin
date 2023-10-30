package com.gamingbacklog.api.gamingbacklogapi.controllers

import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.models.requests.UserRequest
import com.gamingbacklog.api.gamingbacklogapi.models.responses.LibraryResponse
import com.gamingbacklog.api.gamingbacklogapi.models.responses.UserResponse
import com.gamingbacklog.api.gamingbacklogapi.services.UserService
import org.springframework.data.repository.query.Param
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

  @CrossOrigin(origins = ["http://localhost:3000"])
  @GetMapping("/")
  fun getAllUsers(
  ): ResponseEntity<List<UserResponse>> {
    val users = userService.getAll().map { user -> userService.convertUserToResponse(user) }
    return ResponseEntity.ok(users)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @GetMapping("/{id}")
  fun getSingleUser(
    @PathVariable("id") id: String,
    @RequestParam(name = "includePassword", defaultValue = "false") includePassword: Boolean
  ): ResponseEntity<UserResponse> {
    val user = userService.getSingle(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
    return ResponseEntity.ok(userService.convertUserToResponse(user, includePassword))
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @PostMapping("/")
  fun createUser(
    @RequestBody userRequest: UserRequest
  ): ResponseEntity<UserResponse> {
    val user = userService.create(userRequest) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
    return ResponseEntity<UserResponse>(userService.convertUserToResponse(user), HttpStatus.CREATED)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @PostMapping("/login")
  fun authenticateUser(
    @RequestBody userRequest: UserRequest
  ): ResponseEntity<UserResponse> {
    val user = userRequest.email?.let { userService.getSingleByEmail(it) } ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
    val authenticatedUser = userService.authenticateUser(userRequest, user) ?:
    return ResponseEntity(HttpStatus.BAD_REQUEST)
    return ResponseEntity<UserResponse>(userService.convertUserToResponse(authenticatedUser), HttpStatus.OK)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @PatchMapping("/{id}")
  fun updateUserInfo(
    @PathVariable("id") id: String,
    @RequestBody userRequest: UserRequest
  ): ResponseEntity<UserResponse> {
    val user = userService.updateUserFields(id, userRequest) ?: return ResponseEntity<UserResponse>(null, HttpStatus.NOT_FOUND)
    return ResponseEntity<UserResponse>(userService.convertUserToResponse(user), HttpStatus.OK)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @DeleteMapping("/{id}")
  fun deleteUser(
    @PathVariable("id") id: String,
  ): ResponseEntity<String> {
    userService.delete(id)
    return ResponseEntity(HttpStatus.NO_CONTENT)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @GetMapping("/{userId}/libraries")
  fun getAllUserLibraries(
    @PathVariable("userId") userId: String,
  ): ResponseEntity<List<LibraryResponse>> {
    val user = userService.getSingle(userId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
    return ResponseEntity.ok(userService.convertUserToResponse(user).libraries)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @PostMapping("/{userId}/libraries")
  fun createUserLibrary(
    @PathVariable("userId") userId: String,
    @RequestBody libraryRequest: LibraryRequest
  ): ResponseEntity<UserResponse> {
    val userResponse = userService.createUserLibrary(userId, libraryRequest)
      ?: return ResponseEntity(HttpStatus.NOT_FOUND)
    return ResponseEntity.ok(userResponse)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @PatchMapping("/{userId}/libraries/{libraryId}")
  fun deleteUserLibrary(
    @PathVariable("userId") userId: String,
    @PathVariable("libraryId") libraryId: String
  ): ResponseEntity<UserResponse> {
    val userResponse = userService.deleteUserLibrary(userId, libraryId)
      ?: return ResponseEntity(HttpStatus.NOT_FOUND)
    return ResponseEntity.ok(userResponse)
  }
}

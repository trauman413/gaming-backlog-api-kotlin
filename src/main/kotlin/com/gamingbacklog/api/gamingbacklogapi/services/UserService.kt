package com.gamingbacklog.api.gamingbacklogapi.services

import com.gamingbacklog.api.gamingbacklogapi.models.User
import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.models.requests.Request
import com.gamingbacklog.api.gamingbacklogapi.models.requests.UserRequest
import com.gamingbacklog.api.gamingbacklogapi.models.responses.UserResponse
import com.gamingbacklog.api.gamingbacklogapi.repositories.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class UserService(
  private val userRepository: UserRepository,
  private val libraryService: LibraryService
): IService<User> {
  override fun getAll(): List<User> {
    return userRepository.findAll()
  }

  override fun getSingle(id: String): User? {
    return userRepository.findOneById(ObjectId(id))
  }

  override fun getSingleByName(name: String): User? {
    return userRepository.findByDisplayName(name)
  }

  override fun create(request: Request): User? {
    val userRequest = request as UserRequest
    if (userRequest.displayName == null || userRequest.password == null || userRequest.email == null) {
      return null
    }
    val defaultLibrary = libraryService.create(LibraryRequest("All Games", ArrayList())) // revisit name, we can call it "Default" or just "All" if we want
    val user = User(
      displayName = userRequest.displayName,
      password = userRequest.password,
      email = userRequest.email,
      libraries = arrayListOf(defaultLibrary.id)
    )
    userRepository.save(user)
    return user
  }

  override fun delete(id: String) {
    userRepository.deleteById(id)
  }

  override fun update(model: User) {
    userRepository.save(model)
  }

  fun updateUserFields(id: String, newFields: UserRequest): User? {
    val user = userRepository.findOneById(ObjectId(id))
    if (user != null) {
      newFields.displayName?.let { user.displayName = newFields.displayName }
      newFields.password?.let { user.password = newFields.password }
      newFields.email?.let { user.email = newFields.email }
      update(user)
    }
    return user
  }

  fun convertUserToResponse(user: User): UserResponse {
    // might want to have better error handling later without !!
    val userLibraries = user.libraries.map { libraryService.convertLibraryToResponse(libraryService.getSingle(it)!!) }
    return UserResponse(user.id, user.displayName, user.email, userLibraries)
  }

  fun createUserLibrary(userId: String, libraryRequest: LibraryRequest): UserResponse? {
    val newLibrary = libraryService.create(libraryRequest)
    val user = getSingle(userId)
    if (user != null) {
      user.libraries.add(newLibrary.id)
      update(user)
      return convertUserToResponse(user)
    }
    return null
  }

  fun deleteUserLibrary(userId: String, libraryId: String): UserResponse? {
    val user = getSingle(userId)
    if (user != null) {
      libraryService.delete(libraryId)
      user.libraries.remove(libraryId)
      update(user)
      return convertUserToResponse(user)
    }
    return null

  }

 }
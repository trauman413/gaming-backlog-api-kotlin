package com.gamingbacklog.api.gamingbacklogapi.services

import com.gamingbacklog.api.gamingbacklogapi.models.User
import com.gamingbacklog.api.gamingbacklogapi.models.requests.Request
import com.gamingbacklog.api.gamingbacklogapi.models.requests.UserRequest
import com.gamingbacklog.api.gamingbacklogapi.repositories.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class UserService(
  private val userRepository: UserRepository
): IService<User> {
  override fun getAll(): List<User> {
    return userRepository.findAll()
  }

  override fun getSingle(id: String): User? {
    return userRepository.findOneById(ObjectId(id))
  }

  override fun getSingleByName(name: String): User? {
    return userRepository.findByUsername(name)
  }

  override fun create(request: Request): User? {
    val userRequest = request as UserRequest
    val user = User(
      username = userRequest.username,
      password = userRequest.password,
      email = userRequest.email
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

  fun updateUserFields(id: String, newFields: Map<String, String>): User? {
    val user = userRepository.findOneById(ObjectId(id))
    if (user != null) {
      if (newFields.containsKey("username")) {
        user.username = newFields["username"] as String
      }
      if (newFields.containsKey("password")) {
        user.password = newFields["password"] as String
      }
      if (newFields.containsKey("email")) {
        user.email = newFields["email"] as String
      }
      update(user)
    }
    return user
  }
}
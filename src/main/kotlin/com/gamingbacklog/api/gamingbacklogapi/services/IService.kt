package com.gamingbacklog.api.gamingbacklogapi.services

import com.gamingbacklog.api.gamingbacklogapi.models.requests.Request

interface IService<T> {
  fun getAll(): List<T>
  fun getSingle(id: String): T?
  fun getSingleByName(name: String): T?
  fun create(request: Request): T?
  fun delete(id: String)
  fun update(model: T)
}

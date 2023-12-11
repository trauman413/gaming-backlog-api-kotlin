package com.gamingbacklog.api.gamingbacklogapi.unit.services

import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.models.User
import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.models.requests.UserRequest
import com.gamingbacklog.api.gamingbacklogapi.models.responses.LibraryResponse
import com.gamingbacklog.api.gamingbacklogapi.repositories.UserRepository
import com.gamingbacklog.api.gamingbacklogapi.services.LibraryService
import com.gamingbacklog.api.gamingbacklogapi.services.UserService
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

@Import(UserService::class)
@TestPropertySource(properties = ["CLIENT_ID=test_id", "CLIENT_SECRET=test_secret"])
class UserServiceTests {

  private val userRepository = mock(UserRepository::class.java)
  private val libraryService = mock(LibraryService::class.java)
  private val libraryId = "70b664a416135a6e967fadc6"


  private val userService = UserService(userRepository, libraryService)

  private val mockUserDb = HashMap<String, User>()

  fun mockSave(user: User) {
    given(userRepository.save(user)).will {
      mockUserDb[user.id] = user
        user
    }
  }

  fun mockGetOne(userId: String) {
    given(userRepository.findOneById(ObjectId(userId))).willReturn(mockUserDb[userId])
  }

  fun mockCreateLibrary(libraryName: String) {
    given(libraryService.create(any())).willReturn(Library(id = libraryId, name = libraryName, games = ArrayList()))
  }

  fun mockDeleteLibrary() {
    given(libraryService.delete(any())).willAnswer { }
  }

  fun mockGetSingleLibrary(libraryName: String) {
    given(libraryService.getSingle(libraryId)).willReturn(Library(id = libraryId, name = libraryName, games = ArrayList()))
  }



  @BeforeEach
  fun initialize() {
    given(libraryService.convertLibraryToResponse(any())).willCallRealMethod()
  }

  @AfterEach
  fun tearDown() {
    mockUserDb.clear()
  }

  @Nested
  @DisplayName("Tests for createUser")
  inner class CreateUser {
    @Test
    fun shouldCreateNewUser() {
      val user = User("1772a862dcb22c5d5356b5ec", "AwesomeUser", "xxx", "user@user.com", arrayListOf(libraryId))
      mockCreateLibrary("All Games")
      mockSave(user)
      val result = userService.create(UserRequest("AwesomeUser", "xxx", "user@user.com"))
      assertNotNull(result)
      assertEquals(user.displayName, result?.displayName)
      assertEquals(user.email, result?.email)
      assertEquals(user.password, result?.password)
      assertEquals(user.libraries, result?.libraries)
    }

    @Test
    fun shouldNotCreateUserWithNullFields() {
      val result = userService.create(UserRequest(null, null, null))
      assertNull(result)
    }

    @Test
    fun shouldNotCreateUserWithOneNullField() {
      val result = userService.create(UserRequest("something", null, "email@email.com"))
      assertNull(result)
    }
  }

  @Nested
  @DisplayName("Tests for createUserLibrary")
  inner class CreateUserLibrary {

    @Test
    fun shouldHaveNullUser() {
      val user = User("1772a862dcb22c5d5356b5ec", "userName", "123", "test@test.com")
      mockCreateLibrary("Backlog")
      mockGetOne(user.id)
      val result = userService.createUserLibrary(user.id, LibraryRequest("Backlog", ArrayList()))
      assertNull(result)
    }

    @Test
    fun shouldSuccessfullyCreateUserLibrary() {
      val user = User("1772a862dcb22c5d5356b5ec", "userName", "123", "test@test.com")
      mockUserDb[user.id] = user
      mockSave(user)
      mockGetOne(user.id)
      mockCreateLibrary("Backlog")
      mockGetSingleLibrary("Backlog")
      val result = userService.createUserLibrary(user.id, LibraryRequest("Backlog", ArrayList()))
      assertNotNull(result)
      assertEquals(mockUserDb[user.id]?.libraries?.get(0), result?.libraries?.get(0)?.id)
    }

  }

  @Nested
  @DisplayName("Tests for deleteUserLibrary")
  inner class DeleteUserLibrary {
    @Test
    fun shouldHaveNullUser() {
      val user = User("1772a862dcb22c5d5356b5ec", "userName", "123", "test@test.com")
      mockGetOne(user.id)
      val result = userService.deleteUserLibrary(user.id, libraryId)
      assertNull(result)
    }

    @Test
    fun shouldHaveSuccessfullyDeletedLibraryFromUser() {
      val user = User("1772a862dcb22c5d5356b5ec", "userName", "123", "test@test.com", arrayListOf(libraryId))
      mockUserDb[user.id] = user
      mockGetOne(user.id)
      mockGetSingleLibrary("Backlog")
      mockDeleteLibrary()
      mockSave(user)
      val result = userService.deleteUserLibrary(user.id, libraryId)
      assertNotNull(result)
      assertEquals(ArrayList<LibraryResponse>(), result?.libraries)
    }
  }

  @Nested
  @DisplayName("Tests for updateUserFields")
  inner class UpdateUserFields {

    private fun createMockUserDB(userId: String, displayName: String, password: String, email: String, libraryIds: ArrayList<String>): User {
      val ogUser = User(userId, displayName, password, email, libraryIds)
      mockUserDb[ogUser.id] = User(ogUser.id, "user2", "456", "newtest@test.com")
      mockGetOne(ogUser.id)
      return ogUser
    }

    @Test
    fun shouldUpdateAllUserFields() {
      val ogUser = createMockUserDB("1772a862dcb22c5d5356b5ec", "userName", "123", "test@test.com", arrayListOf(libraryId))
      val updateRequest = UserRequest("user2", "456", "newtest@test.com")
      val result = userService.updateUserFields(ogUser.id, updateRequest)
      assertEquals(mockUserDb[ogUser.id]?.displayName, result?.displayName)
      assertEquals(mockUserDb[ogUser.id]?.email, result?.email)
      assertEquals(mockUserDb[ogUser.id]?.password, result?.password)
      assertEquals(mockUserDb[ogUser.id]?.libraries, result?.libraries)
    }

    @Test
    fun shouldUpdatePartialUserFields() {
      val ogUser = createMockUserDB("1772a862dcb22c5d5356b5ec", "userName", "123", "test@test.com", arrayListOf(libraryId))
      val updateRequest = UserRequest(displayName = "user2", password = null, email = "newtest@test.com")
      val result = userService.updateUserFields(ogUser.id, updateRequest)
      assertEquals(mockUserDb[ogUser.id]?.displayName, result?.displayName)
      assertEquals(mockUserDb[ogUser.id]?.email, result?.email)
      assertEquals(mockUserDb[ogUser.id]?.password, result?.password)
      assertEquals(mockUserDb[ogUser.id]?.libraries, result?.libraries)
    }

    @Test
    fun shouldFindNoUser() {
      val user = userService.updateUserFields("1772a862dcb22c5d5356b5ec", UserRequest(displayName = "user2", password = null, email = "newtest@test.com"))
      assertNull(user)
    }
  }
}
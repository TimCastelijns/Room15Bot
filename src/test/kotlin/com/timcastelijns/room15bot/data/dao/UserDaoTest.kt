package com.timcastelijns.room15bot.data.dao

import com.timcastelijns.room15bot.data.db.Database
import com.timcastelijns.room15bot.data.db.UserDao
import com.timcastelijns.room15bot.data.repositories.ConfigRepository
import org.junit.*
import kotlin.test.*

class UserDaoTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            val isCiBuild = System.getenv("CI")?.toBoolean() ?: false
            Assume.assumeFalse(isCiBuild)
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
        }
    }

    private val userDao = UserDao()

    @Before
    fun before() {
        val config = ConfigRepository().getDatabaseConfig(test = true)
        Database.connect(config, andInitialize = false)

        Database.wipe()
        Database.initialize()
    }

    @After
    fun after() {
        Database.wipe()
    }

    @Test
    fun `can create user`() {
        givenOneUser()
    }

    @Test
    fun `can find created user by id`() {
        givenOneUser()

        val user = userDao.getById(1L)
        assertNotNull(user)
    }

    @Test
    fun `unknown user is null`() {
        val user = userDao.getById(1L)
        assertNull(user)
    }

    @Test
    fun `can find user by name`() {
        givenOneUserWithName()

        val users = userDao.getByName("John")
        assertTrue { users.isNotEmpty() }
        assertEquals("John", users.first().name)
    }

    @Test
    fun `can find all users with same name`() {
        givenTwoUsersWithSameName()

        val users = userDao.getByName("John")
        assertTrue { users.size == 2 }
        assertTrue { users.all { it.name == "John" } }
    }

    private fun givenOneUser() {
        userDao.create(1L)
    }

    private fun givenOneUserWithName() {
        userDao.create(1L, "John")
    }

    private fun givenTwoUsersWithSameName() {
        userDao.create(1L, "John")
        userDao.create(2L, "John")
    }

}
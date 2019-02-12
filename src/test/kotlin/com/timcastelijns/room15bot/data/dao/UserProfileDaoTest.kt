package com.timcastelijns.room15bot.data.dao

import com.timcastelijns.room15bot.data.db.Database
import com.timcastelijns.room15bot.data.db.UserDao
import com.timcastelijns.room15bot.data.db.UserProfileDao
import com.timcastelijns.room15bot.data.repositories.ConfigRepository
import org.junit.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserProfileDaoTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            val isCiBuild = System.getenv("CI")?.toBoolean() ?: false
            Assume.assumeFalse(isCiBuild)

            val config = ConfigRepository().getDatabaseConfig(test = true)
            Database.connect(config, andInitialize = false)
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {

        }
    }

    private val userProfileDao = UserProfileDao()
    private val userDao = UserDao()

    @Before
    fun before() {
        Database.wipe()
        Database.initialize()
    }

    @After
    fun after() {
        Database.wipe()
    }

    @Test
    fun `can create profile for user`() {
        givenOneUser()
        val user = userDao.getById(1L)!!

        userProfileDao.create(user, "J dawg", 25)

        val profile = userDao.getProfile(user)
        assertNotNull(profile)

        assertEquals("J dawg", profile.nickname)
        assertEquals(25, profile.age)
    }

    private fun givenOneUser() {
        userDao.create(1L)
    }
}

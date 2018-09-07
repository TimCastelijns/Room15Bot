package com.timcastelijns.room15bot.data.repositories

import com.timcastelijns.room15bot.data.BuildConfig
import com.timcastelijns.room15bot.data.Credentials
import com.timcastelijns.room15bot.data.DatabaseConfig
import java.io.FileInputStream
import java.util.*


private const val CONFIG_FILE_PATH = "config.properties"

private const val PROPERTY_VERSION = "version"
private const val PROPERTY_BRANCH = "branch"
private const val PROPERTY_COMMIT = "commit"
private const val PROPERTY_BUILD_TIME = "buildtime"

private const val BUILD_CONFIG_FILE_PATH = "gen/buildconfig.properties"

private const val PROPERTY_BOT_EMAIL = "botemail"
private const val PROPERTY_BOT_PASSWORD = "botpassword"

private const val PROPERTY_DB_USER = "dbuser"
private const val PROPERTY_DB_PASSWORD = "dbpassword"
private const val PROPERTY_DB_URL = "dburl"
private const val PROPERTY_DB_DRIVER = "dbdriver"

class ConfigRepository {

    fun getBotCredentials(): Credentials {
        val properties = Properties()

        FileInputStream(CONFIG_FILE_PATH).use {
            properties.load(it)
        }

        val email = properties.getProperty(PROPERTY_BOT_EMAIL)
        val password = properties.getProperty(PROPERTY_BOT_PASSWORD)

        return Credentials(email, password)
    }

    fun getDatabaseConfig(): DatabaseConfig {
        val properties = Properties()

        FileInputStream(CONFIG_FILE_PATH).use {
            properties.load(it)
        }

        val user = properties.getProperty(PROPERTY_DB_USER)
        val password = properties.getProperty(PROPERTY_DB_PASSWORD)
        val url = properties.getProperty(PROPERTY_DB_URL)
        val driver = properties.getProperty(PROPERTY_DB_DRIVER)

        return DatabaseConfig(user, password, url, driver)
    }

    fun getBuildConfig(): BuildConfig {
        val properties = Properties()

        FileInputStream(BUILD_CONFIG_FILE_PATH).use {
            properties.load(it)
        }

        val version = properties.getProperty(PROPERTY_VERSION)
        val branch = properties.getProperty(PROPERTY_BRANCH)
        val commit = properties.getProperty(PROPERTY_COMMIT)
        val buildTime = properties.getProperty(PROPERTY_BUILD_TIME)

        return BuildConfig(version, branch, commit, buildTime)
    }

}

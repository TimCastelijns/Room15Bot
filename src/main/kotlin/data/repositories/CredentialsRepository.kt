package data.repositories

import data.Credentials
import java.io.FileInputStream
import java.util.*

private const val PROPERTY_CREDENTIAL_EMAIL = "email"
private const val PROPERTY_CREDENTIAL_PASSWORD = "password"

class CredentialsRepository {

    fun getCredentials(): Credentials {
        val properties = Properties()

        FileInputStream("credentials.properties").use {
            properties.load(it)
        }

        val email = properties.getProperty(PROPERTY_CREDENTIAL_EMAIL)
        val password = properties.getProperty(PROPERTY_CREDENTIAL_PASSWORD)

        return Credentials(email, password)
    }

}

package com.km.exchange.storage

import com.km.exchange.model.User
import com.km.exchange.storage.dao.UserEntity
import com.km.exchange.storage.schema.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.LocalDateTime

class H2UserStorage(
    val connection: Database = Database.connect(
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver"
    )
) : UserStorage {
    constructor(dir: File) : this(
        Database.connect("jdbc:h2:file:${dir.canonicalFile.absolutePath}", driver = "org.h2.Driver")
    )

    init {
        transaction {
            SchemaUtils.create(UserTable)
        }
    }

    override fun get(id: Long): User? {
        return transaction {
            UserEntity.findById(id)?.let { u -> User(u.email, u.name, u.city, u.regDate) }
        }
    }

    override fun update(userId: Long, user: User) {
        transaction {
            val userEntity = UserEntity.findById(userId) ?: throw RuntimeException("User with id $userId does not exist")
            userEntity.email = user.email
            userEntity.name = user.name
            userEntity.city = user.city
        }
    }

    override fun create(user: User): Long {
        val offerEntity = transaction {
            UserEntity.new {
                email = user.email
                name = user.name
                city = user.city
                regDate = LocalDateTime.now().toString()
            }
        }

        return offerEntity.id.value
    }

}
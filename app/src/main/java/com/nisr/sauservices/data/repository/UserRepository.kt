package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.User
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    private val postgrest = SupabaseClient.client.postgrest

    suspend fun registerUser(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["users"].insert(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(uid: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val user = postgrest["users"].select {
                filter { eq("id", uid) }
            }.decodeSingle<User>()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["users"].update(user) {
                filter { eq("id", user.id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.User
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    fun getCurrentUser() = auth.currentUserOrNull()

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, userData: User): Result<Unit> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val userId = auth.currentUserOrNull()?.id ?: throw Exception("User creation failed")
            val userWithId = userData.copy(id = userId)
            
            withContext(Dispatchers.IO) {
                postgrest["users"].insert(userWithId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserData(uid: String): Result<User?> {
        return try {
            val user = withContext(Dispatchers.IO) {
                postgrest["users"].select {
                    filter {
                        eq("id", uid)
                    }
                }.decodeSingleOrNull<User>()
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUserData(user: User): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                postgrest["users"].upsert(user)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        auth.signOut()
    }
}

package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.model.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext




    fun listenToOrders(): Flow<List<OrderModel>> {
    }

    fun listenToBookings(): Flow<List<BookingModel>> {
    }

    }
    }

    // --- ACTIONS ---

    } catch (e: Exception) { Result.failure(e) }
        }
    } catch (e: Exception) { Result.failure(e) }

                filter { eq("id", orderId) }
            }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
        }

                filter { eq("id", userId) }
        Result.success(user)
    } catch (e: Exception) { Result.failure(e) }
            }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
        }
}

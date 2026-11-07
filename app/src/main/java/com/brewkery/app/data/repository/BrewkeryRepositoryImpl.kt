package com.brewkery.app.data.repository

import com.brewkery.app.data.mapper.toDomain
import com.brewkery.app.data.remote.BrewkeryApiService
import com.brewkery.app.di.IoDispatcher
import com.brewkery.app.domain.model.Menu
import com.brewkery.app.domain.model.MenuItem
import com.brewkery.app.domain.repository.BrewkeryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrewkeryRepositoryImpl @Inject constructor(
    private val api: BrewkeryApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BrewkeryRepository {

    private val cacheMutex = Mutex()
    private var cachedMenu: Menu? = null

    override suspend fun getMenu(): Result<Menu> = withContext(ioDispatcher) {
        cachedMenu?.let { return@withContext Result.success(it) }
        cacheMutex.withLock {
            cachedMenu?.let { return@withLock Result.success(it) }
            safeCall { api.getMenu().toDomain() }
                .also { result -> result.getOrNull()?.let { cachedMenu = it } }
        }
    }

    override suspend fun getItem(id: Int): Result<MenuItem> = withContext(ioDispatcher) {
        val remote = safeCall {
            api.getItem(id).toDomain() ?: error("Item $id was missing required fields")
        }
        if (remote.isSuccess) return@withContext remote

        val cached = getMenu().getOrNull()?.items?.find { it.id == id }
        if (cached != null) Result.success(cached) else remote
    }

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (io: IOException) {
        Result.failure(
            IOException(
                "You appear to be offline. Check your connection and try again.",
                io,
            ),
        )
    } catch (http: HttpException) {
        Result.failure(
            IllegalStateException(
                "The menu service returned an error (${http.code()}).",
                http,
            ),
        )
    } catch (error: Exception) {
        Result.failure(error)
    }
}

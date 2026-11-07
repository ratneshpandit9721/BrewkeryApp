package com.brewkery.app.data.remote

import com.brewkery.app.data.model.MenuItemDto
import com.brewkery.app.data.model.MenuResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Paths are relative on purpose. A leading slash would resolve against
 * https://vivekshah138.github.io/ and drop the /Brewkery/ project path.
 */
interface BrewkeryApiService {
    @GET("data.json")
    suspend fun getMenu(): MenuResponseDto

    @GET("api/items/{id}.json")
    suspend fun getItem(@Path("id") id: Int): MenuItemDto
}

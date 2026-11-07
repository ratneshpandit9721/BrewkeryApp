package com.brewkery.app

import com.brewkery.app.data.mapper.toDomain
import com.brewkery.app.data.model.MenuItemDto
import com.brewkery.app.data.remote.BrewkeryApiService
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MenuContractTest {

    @Test
    fun gson_maps_missing_nested_prices_and_null_badge() {
        val json = """
            {
              "id": 1,
              "name": "Latte",
              "base_price": 3.5,
              "badge": null,
              "customizations": {
                "sizes": [{ "id": "s", "label": "Tall" }],
                "sugar_levels": ["Standard"],
                "milk_options": [{ "id": "oat", "name": "Oat" }]
              }
            }
        """.trimIndent()

        val domain = Gson().fromJson(json, MenuItemDto::class.java).toDomain()
        assertNotNull(domain)
        assertNull(domain!!.badge)
        assertEquals(0.0, domain.customizations.sizes.first().extraPrice, 0.0)
        assertEquals(0.0, domain.customizations.milkOptions.first().extraPrice, 0.0)
        assertEquals(3.5, domain.basePrice, 0.0)
    }

    @Test
    fun item_request_keeps_the_brewkery_base_path() = runBlocking {
        val server = MockWebServer()
        server.enqueue(
            MockResponse().setBody("""{"id":7,"name":"Espresso","base_price":3.0}"""),
        )
        server.start()
        try {
            val api = retrofit(server).create(BrewkeryApiService::class.java)
            val item = api.getItem(7)
            val path = server.takeRequest().path
            assertEquals("/Brewkery/api/items/7.json", path)
            assertFalse(path!!.startsWith("/api/"))
            assertEquals(7, item.id)
            assertEquals("Espresso", item.name)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun menu_request_uses_data_json_under_the_project_path() = runBlocking {
        val server = MockWebServer()
        server.enqueue(
            MockResponse().setBody("""{"meta":{"app":"Brewkery"},"categories":[],"items":[]}"""),
        )
        server.start()
        try {
            val api = retrofit(server).create(BrewkeryApiService::class.java)
            api.getMenu()
            assertEquals("/Brewkery/data.json", server.takeRequest().path)
        } finally {
            server.shutdown()
        }
    }

    private fun retrofit(server: MockWebServer): Retrofit = Retrofit.Builder()
        .baseUrl(server.url("/Brewkery/"))
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

package com.brewkery.app.domain.repository

import com.brewkery.app.domain.model.Menu
import com.brewkery.app.domain.model.MenuItem

interface BrewkeryRepository {
    suspend fun getMenu(): Result<Menu>

    suspend fun getItem(id: Int): Result<MenuItem>
}

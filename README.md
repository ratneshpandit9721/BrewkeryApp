# Brewkery

Brewkery is an Android client for an artisanal coffee and bakery menu. It loads the live catalog, lets a guest customize a drink or bake, keeps a cart, and confirms a delivery order.

The menu comes from `https://vivekshah138.github.io/Brewkery/`.

## Features

- Menu grid with search and category filters
- Item details with size, sweetness, milk or extras, and quantity
- In-memory cart with quantity changes, removal, tax, and delivery
- Order confirmation with a reference id and a cleared back stack
- Loading, empty, and offline states

## Tech stack

| Concern | Choice |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Dagger Hilt |
| Networking | Retrofit 2, Gson, OkHttp |
| Images | Coil |
| Async | Coroutines, StateFlow, SharedFlow |
| Navigation | Navigation Compose |

## Architecture

```
ui/          Compose screens, ViewModels, navigation, theme
domain/      Menu, cart, and order models plus repository contracts
data/        Retrofit service, Gson DTOs, mappers, repository implementations
di/          Hilt modules for Gson, OkHttp, Retrofit, and repository bindings
```

ViewModels talk only to domain repository interfaces. `RepositoryModule` binds those interfaces to the data-layer implementations with `@Binds` inside `SingletonComponent`. `NetworkModule` provides one `OkHttpClient`, one `Retrofit` instance, and `BrewkeryApiService`.

The cart is a process-wide singleton (`CartRepository`). Totals are computed in `OrderPricing` from line prices, the menu tax rate, and the delivery fee. Nothing is written to disk, so a process death clears the cart.

## API

Base URL: `https://vivekshah138.github.io/Brewkery/`

| Call | Path |
| --- | --- |
| Full menu | `data.json` |
| One item | `api/items/{id}.json` |

Both paths are relative. The base URL ends with `/` so Retrofit appends them under `/Brewkery/`.

## Project setup

1. Install Android Studio with a current Android SDK and JDK 17 or newer (the Studio JBR is enough).
2. Clone this repository and open the root `BrewkeryApp` project.
3. Let Gradle sync. Dependencies are declared in `gradle/libs.versions.toml` and applied from `app/build.gradle.kts`.
4. Run the `app` configuration on an emulator or device with internet access.

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

Unit tests cover order math, Gson mapping of missing nested prices, and the Retrofit paths against MockWebServer.

## Package map

```
com.brewkery.app
├── BrewkeryApplication.kt      @HiltAndroidApp
├── MainActivity.kt             @AndroidEntryPoint
├── di/
│   ├── AppModule.kt
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
├── data/
│   ├── model/                  Gson DTOs
│   ├── remote/BrewkeryApiService.kt
│   ├── mapper/MenuMapper.kt
│   └── repository/
├── domain/
│   ├── model/
│   └── repository/
└── ui/
    ├── components/
    ├── navigation/
    ├── screen/home|details|cart|success/
    └── theme/
```

## AI tools and prompts

This app was built in Cursor with the coding agent. The prompts below are the ones that drove the implementation. They are written so someone can replay the same build.

### 1. Application brief

> Act as a Senior Android Engineer. Build the complete Brewkery Android app with Kotlin, Jetpack Compose, Material 3, and Clean Architecture + MVVM. Use Dagger Hilt only, Retrofit 2 with GsonConverterFactory and OkHttpClient, Coil, Coroutines with StateFlow, and Navigation Compose. Base URL: `https://vivekshah138.github.io/Brewkery/`. Endpoints: `data.json` and `api/items/{id}.json`. Generate Gradle, `@HiltAndroidApp`, the Hilt `NetworkModule`, then the data, domain, and UI layers, plus a README that records the prompts and one bug that was caught and fixed.

### 2. Contract check before modeling

> Fetch `data.json` and `api/items/1.json`. Model every nested field that the UI needs: meta (currency, tax, delivery fee, ETA), categories, and items with nullable badge, ingredients, sizes, sugar levels, and milk options. Keep DTOs nullable because Gson does not honor Kotlin defaults.

### 3. Layer boundaries

> Put repository interfaces and cart math in `domain`. Put Retrofit, DTOs, and mappers in `data`. Bind the implementations in a Hilt `@Module` installed in `SingletonComponent`. Expose cart lines and the last placed order as `StateFlow`.

### 4. Screens

> Home: search, category chips, a card grid, Coil images, loading and error states, `hiltViewModel()`. Details: image, options, quantity, sticky Add to Cart with haptic feedback and a snackbar. Cart: quantity, remove, subtotal, tax, delivery, total, Place Order. Success: animated confirmation, reference id, delivery summary, and Back to Menu that pops to Home.

### 5. Regression tests

> Add JVM tests that fail if the item path starts with `/`, and a Gson test where `extra_price` and `badge` are absent. Assert the mapped extra price is `0.0` and the badge is null.

## Bug caught and fixed

**Retrofit dropped `/Brewkery/` on the item call.**

The first item request was declared as `@GET("/api/items/{id}.json")`. Retrofit treats a leading slash as an absolute path on the host, so the call went to `https://vivekshah138.github.io/api/items/1.json` and returned 404. The menu call (`data.json`, no leading slash) still worked, which made the failure look like a bad item id.

The fix is in `BrewkeryApiService`:

```kotlin
@GET("data.json")
suspend fun getMenu(): MenuResponseDto

@GET("api/items/{id}.json")
suspend fun getItem(@Path("id") id: Int): MenuItemDto
```

`NetworkModule` keeps the base URL as `https://vivekshah138.github.io/Brewkery/` with the trailing slash. `MenuContractTest.item_request_keeps_the_brewkery_base_path` starts MockWebServer at `/Brewkery/` and asserts the recorded path is `/Brewkery/api/items/7.json`.

A related parsing safeguard is in the DTOs. Gson builds objects without calling Kotlin constructors, so a missing `extra_price` or a null `badge` would crash a non-null property later. Every wire field is nullable and `MenuMapper` fills safe defaults before the domain model is shown.
# BrewkeryApp

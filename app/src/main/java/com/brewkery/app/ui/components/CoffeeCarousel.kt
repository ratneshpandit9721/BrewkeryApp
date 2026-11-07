package com.brewkery.app.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private data class CoffeeSlide(
    val title: String,
    val subtitle: String,
    val imageUrl: String,
)

private val CoffeeSlides = listOf(
    CoffeeSlide(
        title = "Morning Espresso",
        subtitle = "A short, dark shot to start the day",
        imageUrl = "https://images.unsplash.com/photo-1511920170033-f8396924c348?w=1200&auto=format&fit=crop&q=80",
    ),
    CoffeeSlide(
        title = "Velvet Latte",
        subtitle = "Steamed milk over a slow espresso",
        imageUrl = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=1200&auto=format&fit=crop&q=80",
    ),
    CoffeeSlide(
        title = "Iced Cold Brew",
        subtitle = "Eighteen hours, poured over ice",
        imageUrl = "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=1200&auto=format&fit=crop&q=80",
    ),
    CoffeeSlide(
        title = "Pour Over",
        subtitle = "Single origin, bloomed by hand",
        imageUrl = "https://images.unsplash.com/photo-1442512595331-e89e73853f31?w=1200&auto=format&fit=crop&q=80",
    ),
    CoffeeSlide(
        title = "Cafe Crema",
        subtitle = "Golden foam on a long black",
        imageUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=1200&auto=format&fit=crop&q=80",
    ),
    CoffeeSlide(
        title = "Roasted Beans",
        subtitle = "House blend, freshly cracked",
        imageUrl = "https://images.unsplash.com/photo-1447933601403-0c6688de566e?w=1200&auto=format&fit=crop&q=80",
    ),
)

private const val SLIDE_INTERVAL_MS = 1_000

@Composable
fun CoffeeCarousel(
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { CoffeeSlides.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(SLIDE_INTERVAL_MS.toLong())
            val next = (pagerState.currentPage + 1) % CoffeeSlides.size
            pagerState.animateScrollToPage(
                page = next,
                animationSpec = tween(durationMillis = 450),
            )
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
        ) { page ->
            CoffeeSlideCard(slide = CoffeeSlides[page])
        }
        RowDots(
            count = CoffeeSlides.size,
            selected = pagerState.currentPage,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun CoffeeSlideCard(slide: CoffeeSlide) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp)
            .clip(RoundedCornerShape(22.dp)),
    ) {
        BrewkeryImage(
            url = slide.imageUrl,
            contentDescription = slide.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xCC1A120E),
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
        ) {
            Text(
                text = slide.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFF7F1E8),
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = slide.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFF7F1E8).copy(alpha = 0.86f),
            )
        }
    }
}

@Composable
private fun RowDots(
    count: Int,
    selected: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val active = index == selected
            Box(
                modifier = Modifier
                    .size(width = if (active) 16.dp else 6.dp, height = 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                    ),
            )
        }
    }
}

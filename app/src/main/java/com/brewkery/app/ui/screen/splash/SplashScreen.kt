package com.brewkery.app.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.brewkery.app.R
import com.brewkery.app.ui.theme.BrewkeryDisplay
import kotlinx.coroutines.delay

private val SplashInk = Color(0xFF141318)
private val SplashCream = Color(0xFFF7F1E8)
private val SplashGold = Color(0xFFD7B48A)

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(2_200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashInk),
    ) {
        Image(
            painter = painterResource(R.drawable.splash_coffee),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.48f to Color.Transparent,
                            0.68f to SplashInk.copy(alpha = 0.82f),
                            0.82f to SplashInk,
                            1f to SplashInk,
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(width = 42.dp, height = 2.dp)
                    .background(SplashGold, RoundedCornerShape(2.dp)),
            )
            Text(
                text = stringResource(R.string.splash_title),
                color = SplashCream,
                fontFamily = BrewkeryDisplay,
                fontWeight = FontWeight.SemiBold,
                fontSize = 44.sp,
                lineHeight = 48.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.splash_tagline),
                color = SplashCream.copy(alpha = 0.82f),
                fontSize = 14.sp,
                letterSpacing = 2.4.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

package com.example.ui.screens.transactions.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Slide-in animation wrapper for transaction items when loaded or inserted into lists.
 * Provides a gentle upward slide and fade-in with optional stagger for a polished fintech feel.
 */
@Composable
fun SlideInTransactionContainer(
    modifier: Modifier = Modifier,
    itemIndex: Int = 0,
    content: @Composable () -> Unit
) {
    val transitionState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    val staggeredDelay = (itemIndex * 35).coerceAtMost(280)

    AnimatedVisibility(
        visibleState = transitionState,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight / 3 },
            animationSpec = tween(
                durationMillis = 320,
                delayMillis = staggeredDelay,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 280,
                delayMillis = staggeredDelay
            )
        ),
        modifier = modifier
    ) {
        Box {
            content()
        }
    }
}

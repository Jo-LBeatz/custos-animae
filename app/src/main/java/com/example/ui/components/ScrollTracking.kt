package com.example.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.PageViewMode
import kotlin.math.abs
import kotlin.math.sqrt

private fun handleVisibilityChange(viewModel: MainViewModel, wantVisible: Boolean) {
    val mode = viewModel.pageViewMode.value
    when (mode) {
        PageViewMode.PURE_CONTENT -> {
            viewModel.setBottomNavVisible(false)
            viewModel.setTopHeaderVisible(false)
        }
        PageViewMode.STANDARD_WITH_BARS -> {
            viewModel.setBottomNavVisible(true)
            viewModel.setTopHeaderVisible(true)
        }
        PageViewMode.DYNAMIC_AUTO_HIDE -> {
            viewModel.setBottomNavVisible(wantVisible)
            viewModel.setTopHeaderVisible(wantVisible)
        }
    }
}

@Composable
fun TrackLazyListScroll(
    listState: LazyListState,
    viewModel: MainViewModel,
    minDeltaThreshold: Int = 8
) {
    val prevIndex = remember { mutableIntStateOf(listState.firstVisibleItemIndex) }
    val prevOffset = remember { mutableIntStateOf(listState.firstVisibleItemScrollOffset) }

    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val currentIndex = listState.firstVisibleItemIndex
        val currentOffset = listState.firstVisibleItemScrollOffset

        val delta = if (currentIndex != prevIndex.intValue) {
            (currentIndex - prevIndex.intValue) * 200 + (currentOffset - prevOffset.intValue)
        } else {
            currentOffset - prevOffset.intValue
        }

        if (delta > minDeltaThreshold) {
            handleVisibilityChange(viewModel, false)
        } else if (delta < -minDeltaThreshold) {
            handleVisibilityChange(viewModel, true)
        }

        prevIndex.intValue = currentIndex
        prevOffset.intValue = currentOffset

        val layoutInfo = listState.layoutInfo
        val totalItemsCount = layoutInfo.totalItemsCount
        if (totalItemsCount > 1) {
            val firstVisibleItem = layoutInfo.visibleItemsInfo.firstOrNull()
            if (firstVisibleItem != null) {
                val itemSize = firstVisibleItem.size
                val fraction = if (itemSize > 0) currentOffset.toFloat() / itemSize else 0f
                val currentPosition = currentIndex + fraction
                val progress = (currentPosition / (totalItemsCount - 1).coerceAtLeast(1)).coerceIn(0f, 1f)
                viewModel.setReadingProgress(progress)
            }
        } else {
            viewModel.setReadingProgress(0f)
        }
    }
}

@Composable
fun TrackLazyGridScroll(
    gridState: LazyGridState,
    viewModel: MainViewModel,
    minDeltaThreshold: Int = 8
) {
    val prevIndex = remember { mutableIntStateOf(gridState.firstVisibleItemIndex) }
    val prevOffset = remember { mutableIntStateOf(gridState.firstVisibleItemScrollOffset) }

    LaunchedEffect(gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset) {
        val currentIndex = gridState.firstVisibleItemIndex
        val currentOffset = gridState.firstVisibleItemScrollOffset

        val delta = if (currentIndex != prevIndex.intValue) {
            (currentIndex - prevIndex.intValue) * 200 + (currentOffset - prevOffset.intValue)
        } else {
            currentOffset - prevOffset.intValue
        }

        if (delta > minDeltaThreshold) {
            handleVisibilityChange(viewModel, false)
        } else if (delta < -minDeltaThreshold) {
            handleVisibilityChange(viewModel, true)
        }

        prevIndex.intValue = currentIndex
        prevOffset.intValue = currentOffset

        val layoutInfo = gridState.layoutInfo
        val totalItemsCount = layoutInfo.totalItemsCount
        if (totalItemsCount > 1) {
            val firstVisibleItem = layoutInfo.visibleItemsInfo.firstOrNull()
            if (firstVisibleItem != null) {
                val itemSize = firstVisibleItem.size.height
                val fraction = if (itemSize > 0) currentOffset.toFloat() / itemSize else 0f
                val currentPosition = currentIndex + fraction
                val progress = (currentPosition / (totalItemsCount - 1).coerceAtLeast(1)).coerceIn(0f, 1f)
                viewModel.setReadingProgress(progress)
            }
        } else {
            viewModel.setReadingProgress(0f)
        }
    }
}

@Composable
fun TrackScrollState(
    scrollState: ScrollState,
    viewModel: MainViewModel,
    minDeltaThreshold: Int = 8
) {
    val prevScroll = remember { mutableIntStateOf(scrollState.value) }

    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        val currentScroll = scrollState.value
        val maxScroll = scrollState.maxValue
        val delta = currentScroll - prevScroll.intValue

        if (delta > minDeltaThreshold) {
            handleVisibilityChange(viewModel, false)
        } else if (delta < -minDeltaThreshold) {
            handleVisibilityChange(viewModel, true)
        }

        prevScroll.intValue = currentScroll

        val progress = if (maxScroll > 0) currentScroll.toFloat() / maxScroll.toFloat() else 0f
        viewModel.setReadingProgress(progress)
    }
}

@Composable
fun rememberDirectionalScrollConnection(
    viewModel: MainViewModel,
    sensitivity: Float = 10f
): NestedScrollConnection {
    return remember(viewModel) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput) {
                    val deltaY = available.y
                    if (deltaY < -sensitivity) {
                        handleVisibilityChange(viewModel, false)
                    } else if (deltaY > sensitivity) {
                        handleVisibilityChange(viewModel, true)
                    }
                }
                return Offset.Zero
            }
        }
    }
}

@Composable
fun Modifier.universalScrollAndGestureTracker(viewModel: MainViewModel): Modifier {
    return this.pointerInput(viewModel) {
        awaitPointerEventScope {
            while (true) {
                val downEvent = awaitPointerEvent(PointerEventPass.Initial)
                val downPointer = downEvent.changes.firstOrNull() ?: continue
                val startY = downPointer.position.y
                val startX = downPointer.position.x
                val startTime = System.currentTimeMillis()
                var isGestureHandled = false

                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val currentPointer = event.changes.firstOrNull { it.id == downPointer.id } ?: break

                    val deltaY = currentPointer.position.y - startY
                    val deltaX = currentPointer.position.x - startX

                    if (!isGestureHandled && abs(deltaY) > 15f && abs(deltaY) > abs(deltaX)) {
                        if (deltaY < -15f) {
                            handleVisibilityChange(viewModel, false)
                            isGestureHandled = true
                        } else if (deltaY > 15f) {
                            handleVisibilityChange(viewModel, true)
                            isGestureHandled = true
                        }
                    }

                    if (!currentPointer.pressed) {
                        val endTime = System.currentTimeMillis()
                        val moveDistance = sqrt(deltaX * deltaX + deltaY * deltaY)

                        if (!isGestureHandled && moveDistance < 10f && (endTime - startTime) < 300) {
                            val navHidden = !viewModel.isBottomNavVisible.value || !viewModel.isTopHeaderVisible.value
                            if (navHidden) {
                                handleVisibilityChange(viewModel, true)
                            }
                        }
                        break
                    }
                }
            }
        }
    }
}

@Composable
fun Modifier.fullScreenToggleOnTap(viewModel: MainViewModel): Modifier {
    return this.universalScrollAndGestureTracker(viewModel)
}

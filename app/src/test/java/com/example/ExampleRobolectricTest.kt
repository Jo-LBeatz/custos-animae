package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {
    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertNotNull(appName)
    }

    @Test
    fun `verify reading text size preference persistence`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = MainViewModel(app)

        // Check default text size
        assertEquals(14f, viewModel.readingTextSize.value)

        // Update text size
        viewModel.updateReadingTextSize(18f)
        assertEquals(18f, viewModel.readingTextSize.value)

        // Verify it persists in SharedPreferences directly
        val sharedPrefs = app.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        assertEquals(18f, sharedPrefs.getFloat("reading_text_size", 14f))

        // Create a new ViewModel instance to simulate a new session/app restart
        val newViewModel = MainViewModel(app)
        assertEquals(18f, newViewModel.readingTextSize.value)
    }
}

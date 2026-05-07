package com.aplus.locknative

import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import com.aplus.locknative.ui.AplusLockApp
import com.aplus.locknative.ui.AplusLockTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dùng status bar/navigation bar thật của điện thoại thay vì status bar giả trong mockup.
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.rgb(5, 6, 10)
        window.navigationBarColor = Color.rgb(5, 6, 10)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        setContent {
            AplusLockTheme {
                AplusLockApp()
            }
        }
    }
}

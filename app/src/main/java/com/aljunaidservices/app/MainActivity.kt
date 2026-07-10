package com.aljunaidservices.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aljunaidservices.app.ui.theme.AlJunaidServicesTheme
import com.aljunaidservices.app.ui.screens.*
import com.aljunaidservices.app.ui.viewmodel.MainViewModel

/**
 * النشاط الرئيسي للتطبيق (MainActivity)
 * هذا هو نقطة الدخول الرئيسية لنظام أندرويد لتشغيل تطبيق الخدمات الطلابية والتقنية.
 * يرث من ComponentActivity لتفعيل Jetpack Compose بالكامل لتصميم الواجهات التفاعلية الحديثة.
 */
class MainActivity : ComponentActivity() {

    // إنشاء كائن الـ ViewModel باستخدام خاصية الـ delegation (by viewModels)
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            // استدعاء السمة المخصصة للتطبيق والتي تدعم الوضعين المظلم والمضيء تلقائياً
            AlJunaidServicesTheme {
                
                // فرض اتجاه النص من اليمين إلى اليسار (RTL) لدعم اللغة العربية بشكل افترافي واحترافي كامل
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // تفعيل واجهة التنقل الأساسية للتطبيق
                        AppMainScaffold(viewModel)
                    }
                }
            }
        }
    }
}

/**
 * المكون الهيكلي الرئيسي (Scaffold) الحاوي لشريط التنقل السفلي وشاشة العرض الحالية.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMainScaffold(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    Screen.Home,
                    Screen.Services,
                    Screen.Portfolio,
                    Screen.Testimonials,
                    Screen.Contact
                )
                
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title, style = MaterialTheme.typography.labelSmall) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(viewModel, navController) }
            composable(Screen.Services.route) { ServicesScreen(viewModel) }
            composable(Screen.Portfolio.route) { PortfolioScreen(viewModel) }
            composable(Screen.Testimonials.route) { TestimonialsScreen(viewModel) }
            composable(Screen.Contact.route) { ContactScreen(viewModel) }
        }
    }
}

// إضافة الشاشات المفقودة كـ Composables مؤقتة للسماح ببناء التطبيق بنجاح
@Composable
fun PortfolioScreen(viewModel: MainViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("شاشة معرض الأعمال قيد التطوير")
    }
}

@Composable
fun TestimonialsScreen(viewModel: MainViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("شاشة آراء العملاء قيد التطوير")
    }
}

@Composable
fun ContactScreen(viewModel: MainViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("شاشة اتصل بنا قيد التطوير")
    }
}

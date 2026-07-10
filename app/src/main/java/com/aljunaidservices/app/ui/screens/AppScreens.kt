package com.aljunaidservices.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aljunaidservices.app.data.network.*
import com.aljunaidservices.app.ui.viewmodel.MainViewModel
import com.aljunaidservices.app.ui.viewmodel.UiState

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "الرئيسية", Icons.Default.Home)
    object Services : Screen("services", "الخدمات", Icons.Default.List)
    object Portfolio : Screen("portfolio", "الأعمال", Icons.Default.Star)
    object Testimonials : Screen("testimonials", "الآراء", Icons.Default.Face)
    object Contact : Screen("contact", "اتصل بنا", Icons.Default.Phone)
}

@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavController) {
    val dashboardState by viewModel.dashboardState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("مرحباً بكم في منصة الجنيد", style = MaterialTheme.typography.displayLarge.copy(fontSize = 26.sp), color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("شريككم الأكاديمي والتقني المعتمد لتطوير البحوث ومشاريع التخرج بجودة احترافية متكاملة.", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray, textAlign = TextAlign.Center)
                }
            }
        }

        item { Text("إحصائيات إنجازاتنا", style = MaterialTheme.typography.titleLarge) }

        when (val state = dashboardState) {
            is UiState.Loading -> { item { Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } } }
            is UiState.Success -> {
                val settings = state.data.settings
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("مشاريع منجزة", settings.stats_completed_projects, Icons.Default.Done, Modifier.weight(1f))
                            StatCard("عملاء راضون", settings.stats_satisfied_clients, Icons.Default.Favorite, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("سنوات الخبرة", settings.stats_years_experience, Icons.Default.DateRange, Modifier.weight(1f))
                            StatCard("مستشار أكاديمي", settings.stats_academic_consultants, Icons.Default.Person, Modifier.weight(1f))
                        }
                    }
                }
            }
            is UiState.Error -> { item { Text("تعذر جلب الإحصائيات", color = MaterialTheme.colorScheme.error) } }
        }

        item {
            Button(onClick = { navController.navigate(Screen.Services.route) }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.List, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تصفح كافة الخدمات المتاحة")
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text(title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        }
    }
}

@Composable
fun ServicesScreen(viewModel: MainViewModel) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    var selectedService by remember { mutableStateOf<ServiceModel?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (val state = dashboardState) {
            is UiState.Loading -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            is UiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item { Text("خدماتنا الأكاديمية والتقنية", style = MaterialTheme.typography.displayLarge.copy(fontSize = 24.sp)) }
                    items(state.data.services) { service ->
                        ServiceListItem(service, onClick = { selectedService = service })
                    }
                }
            }
            is UiState.Error -> { Text("حدث خطأ", color = MaterialTheme.colorScheme.error) }
        }

        selectedService?.let { service ->
            ServiceDetailsDialog(service, (dashboardState as? UiState.Success)?.data?.settings?.whatsapp_number ?: "", onDismiss = { selectedService = null })
        }
    }
}

@Composable
fun ServiceListItem(service: ServiceModel, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(service.title, style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp))
                Text(service.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(onClick = {}, label = { Text(service.price) })
                    SuggestionChip(onClick = {}, label = { Text(service.duration) })
                }
            }
        }
    }
}

@Composable
fun ServiceDetailsDialog(service: ServiceModel, whatsappNumber: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(service.title) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(service.description)
                Spacer(modifier = Modifier.height(8.dp))
                service.features.split(",").forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, null, tint = Color.Green)
                        Text(it.trim())
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=${whatsappNumber}&text=${Uri.encode("طلب خدمة: ${service.title}")}"))
                context.startActivity(intent)
            }) { Text("اطلب الآن عبر واتساب") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } }
    )
}
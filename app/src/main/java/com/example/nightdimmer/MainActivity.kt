package com.example.nightdimmer

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            var brightness by remember { mutableStateOf(150f) }
            var isNightMode by remember { mutableStateOf(false) }
            var isRunning by remember { mutableStateOf(false) }
            var isDarkMode by remember { mutableStateOf(true) }

            MaterialTheme(
                colorScheme = if (isDarkMode)
                    darkColorScheme()
                else
                    lightColorScheme()
            ) {

                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Night Dimmer") })
                    }
                ) { padding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Dark Mode", modifier = Modifier.weight(1f))
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { isDarkMode = it }
                            )
                        }

                        Divider()

                        Column {
                            Text("Brightness")

                            Spacer(modifier = Modifier.height(8.dp))

                            Slider(
                                value = brightness,
                                onValueChange = {
                                    brightness = it

                                    if (isRunning) {
                                        val intent = Intent(context, OverlayService::class.java)
                                        intent.putExtra("alpha", brightness.toInt())
                                        intent.putExtra("night", isNightMode)
                                        context.startService(intent)
                                    }
                                },
                                valueRange = 0f..255f,
                                enabled = isRunning
                            )
                        }

                        Divider()

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Night Mode", modifier = Modifier.weight(1f))
                            Switch(
                                checked = isNightMode,
                                onCheckedChange = {
                                    isNightMode = it

                                    if (isRunning) {
                                        val intent = Intent(context, OverlayService::class.java)
                                        intent.putExtra("alpha", brightness.toInt())
                                        intent.putExtra("night", isNightMode)
                                        context.startService(intent)
                                    }
                                },
                                enabled = isRunning
                            )
                        }

                        Divider()

                        Button(
                            onClick = {
                                if (!Settings.canDrawOverlays(context)) {
                                    context.startActivity(
                                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                    )
                                } else {
                                    isRunning = true

                                    val intent = Intent(context, OverlayService::class.java)
                                    intent.putExtra("alpha", brightness.toInt())
                                    intent.putExtra("night", isNightMode)
                                    context.startService(intent)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Dim")
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(context, OverlayService::class.java)
                                intent.putExtra("stop", true)
                                context.startService(intent)

                                isRunning = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Stop Dim")
                        }
                    }
                }
            }
        }
    }
}
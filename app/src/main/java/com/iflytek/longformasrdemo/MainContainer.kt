package com.iflytek.longformasrdemo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    videModel: MainViewModel = viewModel()
) {

    val audioPermissionState = rememberPermissionState(
        android.Manifest.permission.RECORD_AUDIO
    )

    if (!audioPermissionState.status.isGranted) {
        LaunchedEffect(key1 = Unit) {
            audioPermissionState.launchPermissionRequest()
        }
    }

    val state = videModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "讯飞实时语音转写Demo", fontWeight = FontWeight.Bold)
                })
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = state.value.asrText,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Gray.copy(alpha = 0.2f))
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(12.dp)
            )
            Button(onClick = {
                videModel.openAsr()
            }) {
                Text("开启转写")
            }
            Spacer(Modifier.height(15.dp))
            Button(onClick = {
                videModel.stopAsr()
            }) {
                Text("结束转写")
            }
        }
    }

}
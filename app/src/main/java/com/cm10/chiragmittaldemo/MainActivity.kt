package com.cm10.chiragmittaldemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cm10.chiragmittaldemo.presentation.ui.screens.PortfolioScreen
import com.cm10.chiragmittaldemo.presentation.viewmodel.PortfolioViewModel
import com.cm10.chiragmittaldemo.presentation.viewmodel.PortfolioViewModelFactory
import com.cm10.chiragmittaldemo.ui.theme.ChiragMittalDemoTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PortfolioViewModel by viewModels { PortfolioViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(0xFF00FF))
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            ChiragMittalDemoTheme {
                Scaffold (
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .height(innerPadding.calculateTopPadding())
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        )

                        PortfolioScreen(
                            uiState = uiState,
                            modifier = Modifier.padding(innerPadding)
                                .fillMaxSize(),
                            onRetryClick = {
                                viewModel.loadPortfolio()
                            },
                        )
                    }
                }
            }
        }
    }

}
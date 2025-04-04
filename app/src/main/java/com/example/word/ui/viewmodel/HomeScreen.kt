package com.example.word.ui.viewmodel

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.word.R
import com.example.word.room.Word
import com.example.word.ui.ViewMode
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navigateToWordEntry: () -> Unit,
    navigateToWordUpdate: (Long) -> Unit,
    navigateToSettings: () -> Unit,
    viewModel: WordViewModel
) {
    val wordList by viewModel.allWords.collectAsState(initial = emptyList())
    val viewMode by viewModel.viewMode.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                ),
                actions = {
                    IconButton(
                        onClick = {navigateToSettings()}
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },

        ){ innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            if (wordList.isEmpty()) {
                HomeScreen(
                    wordList = listOf(Word(id = 0, english = "монгол үг", mongolian = "англи үг")),
                    onEdit = navigateToWordUpdate,
                    onAdd = navigateToWordEntry,
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            } else {
                when (viewMode) {
                    ViewMode.BOTH -> {
                        HomeScreen(
                            wordList = wordList,
                            onEdit = navigateToWordUpdate,
                            onAdd = navigateToWordEntry,
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                    ViewMode.ENGLISH_ONLY -> {
                        HomeScreen(
                            wordList = wordList.map { it.copy(mongolian = "") },
                            onEdit = navigateToWordUpdate,
                            onAdd = navigateToWordEntry,
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                    ViewMode.MONGOLIAN_ONLY -> {
                        HomeScreen(
                            wordList = wordList.map { it.copy(english = "") },
                            onEdit = navigateToWordUpdate,
                            onAdd = navigateToWordEntry,
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    wordList: List<Word>,
    onEdit: (Long) -> Unit,
    onAdd: () -> Unit,
    viewModel: WordViewModel,
    modifier: Modifier = Modifier
) {
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    val currentWord = wordList.getOrNull(currentIndex)

    Surface(
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            currentWord?.let { word ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    color = Color.White,
                ) {
                    Text(
                        text = word.mongolian,
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable {
                                viewModel.toggleViewMode(currentIndex)
                            },
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.padding(vertical = 16.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    color = Color.White,
                ) {
                    Text(
                        text = word.english,
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable {
                                viewModel.toggleViewMode(currentIndex)
                            },
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onAdd,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Нэмэх")
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    Button(
                        onClick = { onEdit(word.id) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Шинэчлэх")
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    Button(
                        onClick = {
                            viewModel.delete(word)
                            val updatedIndex = if (currentIndex == wordList.size - 1) {
                                0
                            } else {
                                currentIndex
                            }
                            currentIndex = updatedIndex
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Устгах")
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            currentIndex = (currentIndex - 1 + wordList.size) % wordList.size
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Дараа")
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 16.dp))
                    Button(
                        onClick = {
                            currentIndex = (currentIndex + 1) % wordList.size
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Өмнөх")
                    }
                }
            }
        }
    }
}
package com.bay.wordlesolver

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bay.wordlesolver.domain.*
import com.bay.wordlesolver.ui.theme.WordleSolverTheme
import java.lang.StringBuilder
import java.util.ArrayList

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordleSolverTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var data = mutableListOf<String>()
                    var context = LocalContext.current
                    Solver.setDict(data as ArrayList<String>?)
                    context.assets.open("data.txt").bufferedReader().forEachLine { data.add(it) }
                    PageColumn()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Preview(showBackground = true)
@Composable
fun PageColumn() {
    Card(
        modifier = Modifier.fillMaxSize()
            .padding(1.dp),
        shape = RoundedCornerShape(3.dp),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            WordleTopAppBar()
            var text1 = remember {TextFieldState(0)}
            var text2 = remember {TextFieldState(1)}
            var text3 = remember {TextFieldState(2)}
            var text4 = remember {TextFieldState(3)}
            var text5 = remember {TextFieldState(4)}

            val textInputStates = mutableListOf<TextFieldState>(
                text1,
                text2,
                text3,
                text4,
                text5)
            StepText("Enter a word")
            WordleTextInputRow(textInputStates)
            StepText("Press the letter to select the correct color")
            GenerateGuessesButton(textInputStates)

        }
    }
}

@Composable
fun WordleTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "WORDLE SOLVER",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        backgroundColor = MaterialTheme.colors.background,
    )
}

@Composable
fun WordleTextInputRow(textInputStates: MutableList<TextFieldState>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        WordleInputFields(textInputStates)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WordleInputFields(textInputStates: MutableList<TextFieldState>){
    val focusManager = LocalFocusManager.current
    LazyRow{
        itemsIndexed(textInputStates){index, item ->
            var color = when (item.color) {
                LetterColor.YELLOW -> Color.Yellow
                LetterColor.GREEN -> Color.Green
                else -> Color.Gray
            }
            Box(
                modifier = Modifier
                    .height(75.dp)
                    .width(75.dp)
                    .padding(4.dp)
                    .background(color.copy(alpha = 0.6f))

            ) {
                var maxChar = 1
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed: Boolean by interactionSource.collectIsPressedAsState()

                if (isPressed) {
                    when (item.color) {
                        LetterColor.GRAY -> item.color = LetterColor.YELLOW
                        LetterColor.YELLOW -> item.color = LetterColor.GREEN
                        else -> {
                            item.color = LetterColor.GRAY
                        }
                    }
                }

                OutlinedTextField(
                    onValueChange = {if (it.length == maxChar) {
                        item.text = it.uppercase()
                        focusManager.moveFocus(FocusDirection.Right)
                    }
                    else if (it.length < maxChar){
                        item.text = it.uppercase()
                        focusManager.moveFocus(FocusDirection.Left)
                    }
                                    },
                    interactionSource = interactionSource,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxSize(),
                    value = item.text,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                )
            }
        }
    }
}

@Composable
fun StepText(text: String) {
    Text(
        text = text,
        fontSize = 17.sp
    )
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun GenerateGuessesButton(textInputStates: List<TextFieldState>) {
    var guesses = remember {
        mutableStateListOf<String>()
    }
    guesses.swapList(DictionaryData.guesses)
    OutlinedButton(
        onClick = {
            generateGuesses(textInputStates)
            guesses.swapList(DictionaryData.guesses)
                  },
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .size(60.dp)
        ) {
        Text(text = "Generate Guesses",
        fontSize = 30.sp,
        color = Color.Black)
    }
    StepText("Press New Game when you have found the correct word.")
    NewWordleGameButton()
    LazyColumn (
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        items(guesses) {item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                elevation = 10.dp

            ) {
                Text(
                    text = item.uppercase(),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
            }
        }
    }
}

@Composable
fun NewWordleGameButton() {
    OutlinedButton(
        onClick = {
            Solver.clearLists()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .size(60.dp)
    ) {
        Text(
            text = "New Game",
            fontSize = 30.sp,
            color = Color.Black
        )
    }
}

fun <T> SnapshotStateList<T>.swapList(newList: List<T>){
    clear()
    addAll(newList)
}

class TextFieldState(index: Int){
    var text: String by mutableStateOf("")
    var color: LetterColor by mutableStateOf(LetterColor.GRAY)
    var index = index
}

@RequiresApi(Build.VERSION_CODES.N)
fun generateGuesses(textInputStates: List<TextFieldState>) {
    Solver.clearLists()
    var word = Word("", mutableListOf<Letter>())
    var sb = StringBuilder()
    var inCorrectPos: Boolean
    var inIncorrectPos: Boolean
    for (letter in textInputStates) {
        inCorrectPos = when(letter.color) {
            LetterColor.GREEN -> true
            else -> false
        }
        inIncorrectPos = when(letter.color) {
            LetterColor.YELLOW -> true
            else -> false
        }
        var l = Letter(
            letter.text.lowercase().toCharArray()[0],
            letter.index,
            inCorrectPos,
            inIncorrectPos
        )
        sb.append(l.letter)
        word.add(l)
    }
    word.word = sb.toString()
    println(word)
    DictionaryData.guesses = Solver.generateGuesses(word)
}
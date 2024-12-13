import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import quiz.QuizGame
import quiz.QuizView
import quiz.WelcomeScreen
import quiz.DifficultySelector

fun main() = application {
    var currentScreen by remember { mutableStateOf("welcome") }
    var game by remember { mutableStateOf<QuizGame?>(null) }

    Window(onCloseRequest = ::exitApplication, title = "Quiz Capitales du Monde") {
        when (currentScreen) {
            "welcome" -> WelcomeScreen(onStart = { currentScreen = "difficulty" })
            "difficulty" -> DifficultySelector { selectedDifficulty ->
                game = QuizGame(selectedDifficulty)
                currentScreen = "game"
            }
            "game" -> QuizView(game!!) {
                currentScreen = "difficulty" // Revenir à l'écran de sélection de difficulté après le jeu
            }
        }
    }
}

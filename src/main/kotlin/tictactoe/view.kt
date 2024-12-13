package quiz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import javax.imageio.ImageIO

// Fonction pour charger une image à partir du dossier resources en fonction du nom d'un pays
fun loadImageFromResources(countryName: String): ImageBitmap? {
    return try {
        // Obtenir le flux de données (InputStream) de l'image correspondant au pays
        // `getResourceAsStream` cherche le fichier "/$countryName.jpg" dans le dossier resources
        val resourceStream = object {}.javaClass.getResourceAsStream("/$countryName.jpg")
        if (resourceStream != null) {
            // Si le fichier est trouvé, lire l'image à partir du flux
            val bufferedImage = ImageIO.read(resourceStream)
            // Convertir l'image en un format compatible avec Jetpack Compose (ImageBitmap)
            bufferedImage?.toComposeImageBitmap()
        } else {
            println("Image non trouvée pour le pays : $countryName")
            null
        }
    } catch (e: Exception) {
        println("Erreur lors du chargement de l'image pour le pays : $countryName")
        e.printStackTrace() // Afficher les détails de l'erreur
        null
    }
}


// Écran de bienvenue
@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF4CAF50), Color(0xFFA5D6A7))
                )
            )
            .padding(16.dp)
    ) {
        Text(
            "🌍 Bienvenue au Quiz des Capitales du Monde 🌍",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Testez vos connaissances en géographie tout en vous amusant !",
            style = TextStyle(
                fontSize = 18.sp,
                color = Color.White
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF388E3C))
        ) {
            Text("Commencer à jouer 🚀", color = Color.White, fontSize = 18.sp)
        }
    }
}

// Sélecteur de difficulté avec animation
@Composable
fun DifficultySelector(onSelect: (Difficulty) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF512DA8), Color(0xFF9575CD))
                )
            )
            .padding(16.dp)
    ) {
        Text(
            "👋 Choisissez une difficulté",
            style = TextStyle(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            DifficultyButton("🌟 Facile", Color(0xFF43A047)) { onSelect(Difficulty.EASY) }
            DifficultyButton("⭐ Moyen", Color(0xFFFFC107)) { onSelect(Difficulty.MEDIUM) }
            DifficultyButton("🔥 Difficile", Color(0xFFD32F2F)) { onSelect(Difficulty.HARD) }
        }
    }
}

// Nouveau bouton de difficulté stylé
@Composable
fun DifficultyButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        modifier = Modifier
            .padding(8.dp)
            .height(50.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
// Vue principale du quiz
@Composable
fun QuizView(game: QuizGame, onRestart: () -> Unit) {
    // Variables d'état pour gérer l'entrée utilisateur, le temps restant, et les messages de retour
    var userAnswer by remember { mutableStateOf("") } // Réponse de l'utilisateur
    var remainingTime by remember { mutableStateOf(10) } // Temps restant pour répondre
    var feedbackMessage by remember { mutableStateOf("") } // Message à afficher après chaque réponse
    val incorrectQuestions = remember { mutableStateListOf<Pair<String, String>>() } // Questions incorrectes

    // Obtenir le pays actuel de la question
    val currentCountry = game.getCurrentQuestion().country
    val countryImage = loadImageFromResources(currentCountry) // Charger l'image du drapeau

    // Déterminer la couleur du chronomètre en fonction du temps restant
    val timerColor = when {
        remainingTime <= 3 -> Color.Red // Rouge si moins de 3 secondes
        remainingTime <= 6 -> Color(0xFFFFA000) // Orange si entre 4 et 6 secondes
        else -> Color(0xFF43A047) // Vert sinon
    }

    // Effet lancé lorsque l'index de la question change
    LaunchedEffect(game.currentQuestionIndex) {
        userAnswer = "" // Réinitialiser la réponse
        remainingTime = 10 // Réinitialiser le temps
        feedbackMessage = "" // Réinitialiser le message
        while (remainingTime > 0 && !game.isGameOver) { // Compte à rebours
            delay(1000L) // Attendre 1 seconde
            remainingTime-- // Réduire le temps restant
        }
        if (remainingTime == 0) { // Temps écoulé
            feedbackMessage = "⏱️ Temps écoulé ! Réponse incorrecte."
            incorrectQuestions.add(
                game.getCurrentQuestion().country to game.getCurrentQuestion().capital
            )
            if (!game.nextQuestion()) {
                // Marquer le jeu comme terminé si aucune autre question
            }
        }
    }

    if (game.isGameOver) {
        // Afficher l'écran de fin de jeu
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                "🎉 Jeu Terminé ! 🎉",
                style = TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Text(
                    "Score : ${game.score} / ${game.score + incorrectQuestions.size}",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "🔴 Réponses Incorrectes ou Non Répondues :",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                if (incorrectQuestions.isEmpty()) {
                    Text(
                        "🎉 Toutes vos réponses étaient correctes ! 🎉",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF388E3C)
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn {
                        items(incorrectQuestions) { (country, capital) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Canvas(modifier = Modifier.size(24.dp).padding(end = 8.dp)) {
                                    drawCircle(
                                        color = Color.Red,
                                        radius = size.minDimension / 2
                                    )
                                }
                                Text(
                                    text = "Pays : $country, Correct : $capital",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1E88E5)),
                modifier = Modifier
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text("🔄 Rejouer", color = Color.White, fontSize = 20.sp)
            }
        }
    } else {
        // Afficher l'écran du jeu
        val question = game.getCurrentQuestion()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9))
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                "🌍 Quel est la capitale de ${question.country} ?",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
            )
            countryImage?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Image de ${question.country}",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(16.dp)
                )
            } ?: Text("Image non disponible", color = Color.Red)

            Text(
                "⏳ Temps restant : $remainingTime secondes",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = timerColor)
            )
            OutlinedTextField(
                value = userAnswer,
                onValueChange = { userAnswer = it },
                label = { Text("Votre réponse") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF1E88E5),
                    unfocusedBorderColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    validateAnswer(game, userAnswer, incorrectQuestions)
                })
            )
            Button(
                onClick = {
                    validateAnswer(game, userAnswer, incorrectQuestions)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1E88E5))
            ) {
                Text("Valider", color = Color.White, fontSize = 16.sp)
            }

            if (feedbackMessage.isNotEmpty()) {
                Text(
                    feedbackMessage,
                    color = if (feedbackMessage.contains("correcte")) Color(0xFF388E3C) else Color.Red,
                    fontSize = 16.sp
                )
            }
        }
    }
}


// Fonction pour valider la réponse
private fun validateAnswer(
    game: QuizGame,
    userAnswer: String,
    incorrectQuestions: MutableList<Pair<String, String>>
) {
    if (!game.checkAnswer(userAnswer)) {
        incorrectQuestions.add(game.getCurrentQuestion().country to game.getCurrentQuestion().capital)
    }
    if (!game.nextQuestion()) {
        // Pas besoin de gérer `game.isGameOver` ici, il est déjà mis à jour dans `nextQuestion()`
    }
}

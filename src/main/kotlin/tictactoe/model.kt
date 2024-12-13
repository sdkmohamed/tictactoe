package quiz

// Classe représentant une question avec un pays et sa capitale
// Cette classe est une simple structure de données qui contient deux propriétés :
// - country : le nom du pays
// - capital : la capitale associée au pays
data class Question(val country: String, val capital: String)

// La classe QuizGame représente la logique principale du jeu de quiz
class QuizGame(private val difficulty: Difficulty) {

    // Liste des questions selon le niveau de difficulté sélectionné
    // Ces listes sont définies en fonction de l'énumération Difficulty (EASY, MEDIUM, HARD)
    private val questions = when (difficulty) {
        Difficulty.EASY -> listOf(
            Question("France", "Paris"),
            Question("Allemagne", "Berlin"),
            Question("Italie", "Rome"),
            Question("Espagne", "Madrid"),
            Question("États-Unis", "Washington D.C.")
        )
        Difficulty.MEDIUM -> listOf(
            Question("Brésil", "Brasília"),
            Question("Canada", "Ottawa"),
            Question("Australie", "Canberra"),
            Question("Inde", "New Delhi"),
            Question("Japon", "Tokyo")
        )
        Difficulty.HARD -> listOf(
            Question("Mongolie", "Oulan-Bator"),
            Question("Bhoutan", "Thimphou"),
            Question("Malawi", "Lilongwe"),
            Question("Fidji", "Suva"),
            Question("Suriname", "Paramaribo")
        )
    }

    // Index de la question actuelle dans la liste
    var currentQuestionIndex = 0
        private set // La valeur ne peut être modifiée que dans cette classe

    // Score actuel du joueur (nombre de bonnes réponses)
    var score = 0
        private set // La valeur ne peut être modifiée que dans cette classe

    // Booléen indiquant si le jeu est terminé
    var isGameOver = false
        private set // La valeur ne peut être modifiée que dans cette classe

    // Fonction pour obtenir la question actuelle
    fun getCurrentQuestion(): Question = questions[currentQuestionIndex]

    // Fonction pour vérifier si la réponse donnée est correcte
    // - `answer` : la réponse saisie par l'utilisateur
    // Retourne `true` si la réponse est correcte, sinon `false`
    fun checkAnswer(answer: String): Boolean {
        // Comparer la capitale attendue avec la réponse utilisateur, en ignorant la casse et les espaces
        val isCorrect = getCurrentQuestion().capital.equals(answer.trim(), ignoreCase = true)
        if (isCorrect) {
            // Augmenter le score si la réponse est correcte
            score++
        }
        return isCorrect
    }

    // Fonction pour passer à la question suivante
    // Retourne `true` s'il y a une autre question, sinon marque le jeu comme terminé et retourne `false`
    fun nextQuestion(): Boolean {
        return if (currentQuestionIndex < questions.size - 1) {
            // Passer à la prochaine question
            currentQuestionIndex++
            true
        } else {
            // Marquer le jeu comme terminé car toutes les questions ont été répondues
            isGameOver = true
            false
        }
    }
}

// Enumération représentant les niveaux de difficulté du quiz
// Les niveaux disponibles sont : EASY, MEDIUM et HARD
enum class Difficulty {
    EASY, MEDIUM, HARD
}

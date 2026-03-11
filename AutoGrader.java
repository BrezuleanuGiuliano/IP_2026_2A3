import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class AutoGrader {

    public float grade(QuizAttempt attempt) {
        if (attempt.getAnswers().isEmpty()) {
            System.out.println("[AutoGrader] No answers to grade.");
            return 0f;
        }

        long correct = attempt.getAnswers().stream()
                .filter(StudentAnswer::isCorrect)
                .count();

        float raw           = (correct * 100f) / attempt.getAnswers().size();
        int penaltySwitches = Math.max(0, attempt.getTabSwitchCount() - 1);
        float penalty       = penaltySwitches * 2f;
        float finalScore    = Math.max(0f, raw - penalty);

        long secondsTaken = Duration.between(
                attempt.getStartedAt(), attempt.getFinishedAt()).getSeconds();

        // Statistici
        printStats(attempt, correct, raw, penalty, finalScore, secondsTaken);

        return finalScore;
    }

    private void printStats(QuizAttempt attempt, long correct,
                            float raw, float penalty,
                            float finalScore, long secondsTaken) {
        System.out.println("[AutoGrader] === Statistici ===");
        System.out.println("  Student         : " + attempt.getStudent().getName());
        System.out.println("  Quiz            : " + attempt.getQuiz().getTitle());
        System.out.println("  Correct answers : " + correct + " / " + attempt.getAnswers().size());
        System.out.println("  Raw score       : " + raw + "%");
        System.out.println("  Tab penalty     : -" + penalty + "%");
        System.out.println("  Final score     : " + finalScore + "%");
        System.out.println("  Time taken      : " + secondsTaken + " seconds");
        System.out.println("  Tab switches    : " + attempt.getTabSwitchCount());
        System.out.println("  Passed          : " +
                (finalScore >= attempt.getQuiz().getPassingScore() ? "YES ✓" : "NO ✗"));
    }

    public String getFeedback(float score, float passingScore) {
        if (score >= passingScore) {
            return "Felicitări! Ai promovat quiz-ul cu " + score + "%.";
        } else {
            return "Nu ai promovat. Scorul tău: " + score + "%. Necesar: " + passingScore + "%.";
        }
    }

    public QuizStatistics getStatistics(QuizAttempt attempt) {
        float score = grade(attempt);
        boolean passed = score >= attempt.getQuiz().getPassingScore();
        long timeTaken = Duration.between(
                attempt.getStartedAt(), attempt.getFinishedAt()).getSeconds();
        return new QuizStatistics(score, passed, timeTaken, attempt.getTabSwitchCount());
    }

    // Clasa pentru statistici returnate
    public static class QuizStatistics {
        private final float score;
        private final boolean passed;
        private final long timeTakenSeconds;
        private final int tabSwitches;

        public QuizStatistics(float score, boolean passed, long timeTakenSeconds, int tabSwitches) {
            this.score            = score;
            this.passed           = passed;
            this.timeTakenSeconds = timeTakenSeconds;
            this.tabSwitches      = tabSwitches;
        }

        public float getScore()            { return score; }
        public boolean isPassed()          { return passed; }
        public long getTimeTakenSeconds()  { return timeTakenSeconds; }
        public int getTabSwitches()        { return tabSwitches; }

        @Override
        public String toString() {
            return "QuizStatistics{score=" + score + "%, passed=" + passed
                    + ", time=" + timeTakenSeconds + "s, tabSwitches=" + tabSwitches + "}";
        }
    }
}
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Float score;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "tab_switch_count")
    private Integer tabSwitchCount;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    private List<StudentAnswer> answers;

    public enum AttemptStatus {
        NOT_STARTED,
        IN_PROGRESS,
        SUBMITTED,
        TIMED_OUT
    }

    public QuizAttempt() {}

    public QuizAttempt(User student, Quiz quiz) {
        this.id             = UUID.randomUUID();
        this.student        = student;
        this.quiz           = quiz;
        this.score          = 0f;
        this.tabSwitchCount = 0;
        this.status         = AttemptStatus.NOT_STARTED;
        this.answers        = new ArrayList<>();
    }

    public QuizAttempt start() {
        if (status != AttemptStatus.NOT_STARTED) {
            throw new IllegalStateException("Attempt already started or completed.");
        }
        this.startedAt = LocalDateTime.now();
        this.status    = AttemptStatus.IN_PROGRESS;
        System.out.println("[EduConnect] Quiz attempt started:");
        System.out.println("  Attempt ID : " + id);
        System.out.println("  Student    : " + student.getName());
        System.out.println("  Quiz       : " + quiz.getTitle());
        System.out.println("  Started at : " + startedAt);
        return this;
    }

    public QuizAttempt submit() {
        if (status != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot submit: attempt is not in progress.");
        }
        this.finishedAt = LocalDateTime.now();
        this.status     = AttemptStatus.SUBMITTED;
        this.score      = calculateScore();
        System.out.println("[EduConnect] Attempt submitted. Score: " + score + "%");
        return this;
    }

    public void recordTabSwitch() {
        if (status != AttemptStatus.IN_PROGRESS) return;
        tabSwitchCount++;
        System.out.println("[EduConnect] ⚠ Tab switch detected! Count: "
                + tabSwitchCount + " / " + quiz.getMaxTabSwitches());
        if (tabSwitchCount >= quiz.getMaxTabSwitches()) {
            System.out.println("[EduConnect] ✖ Max tab switches exceeded — auto-submitting.");
            this.finishedAt = LocalDateTime.now();
            this.status     = AttemptStatus.TIMED_OUT;
            this.score      = calculateScore();
        }
    }

    public Float calculateScore() {
        if (answers.isEmpty()) return 0f;
        long correct = answers.stream()
                .filter(StudentAnswer::isCorrect)
                .count();
        float raw = (correct * 100f) / answers.size();
        int penaltySwitches = Math.max(0, tabSwitchCount - 1);
        float penalty       = penaltySwitches * 2f;
        return Math.max(0f, raw - penalty);
    }

    public void addAnswer(StudentAnswer answer) {
        if (status != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot add answers: attempt is not in progress.");
        }
        answers.add(answer);
    }

    // Getters
    public UUID getId()                     { return id; }
    public Float getScore()                 { return score; }
    public LocalDateTime getStartedAt()     { return startedAt; }
    public LocalDateTime getFinishedAt()    { return finishedAt; }
    public Integer getTabSwitchCount()      { return tabSwitchCount; }
    public AttemptStatus getStatus()        { return status; }
    public User getStudent()                { return student; }
    public Quiz getQuiz()                   { return quiz; }
    public List<StudentAnswer> getAnswers() { return answers; }

    @Override
    public String toString() {
        return "QuizAttempt{id=" + id + ", student='" + student.getName()
                + "', quiz='" + quiz.getTitle() + "', score=" + score
                + "%, status=" + status + ", tabSwitches=" + tabSwitchCount + "}";
    }
}
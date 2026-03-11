package org.example;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
public class Enrollment {
    private UUID id;
    private UUID userID;
    private UUID courseID;
    private float progressPercent;
    private LocalDate enrolledAt;
    private List<String> commentIDs;

    private int totalLessonsCount;
    private int solvedLessonsCount;

    /**
     *
     * @param userID - which user
     * @param courseID - which course
     * @param totalLessonsCount - how many lessons are in the course
     */
    public Enrollment(UUID userID, UUID courseID, int totalLessonsCount) {
        this.id = UUID.randomUUID();
        this.userID = userID;
        this.courseID = courseID;
        this.progressPercent = 0.0f;
        this.totalLessonsCount = totalLessonsCount;
        this.enrolledAt = LocalDate.now();
        this.commentIDs = new ArrayList<>();
        this.solvedLessonsCount = 0;
    }

    public Enrollment enroll(){
        System.out.println("User accepted");
        System.out.println("Enrolled at: " + enrolledAt);
        return this;
    }

    public void updateProgress(){
        if(solvedLessonsCount < totalLessonsCount){
            solvedLessonsCount++;
        }
        this.progressPercent = calculateProgress();
        System.out.println(this.progressPercent);
        if(solvedLessonsCount == totalLessonsCount){
            System.out.println("Course completed congrats!");
        }
    }

    public float calculateProgress(){
        if(totalLessonsCount == 0){
            return 0.0f;
        }

        return ((float) solvedLessonsCount / totalLessonsCount) * 100;
    }

    public void addComment(String commentID){
        commentIDs.add(commentID);
    }
}

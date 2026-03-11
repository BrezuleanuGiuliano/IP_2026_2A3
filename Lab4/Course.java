package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Course {
    private final UUID id;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private CourseStatus status;
    private LocalDate createdAt;
    private List<String> lessonIDs;
    private List<String> enrollmentIDs;

    /**
     *
     * @param title = titlul cursului
     * @param description = descrierea cursului
     * @param category = categoria cursului
     * @param difficulty = dificultatea cursului
     */
    public Course(String title, String description, String category, String difficulty) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.status = CourseStatus.DRAFT;
        this.createdAt = LocalDate.now();
        this.lessonIDs = new ArrayList<>();
        this.enrollmentIDs = new ArrayList<>();
    }

    public void publish(){
        if(!this.getStatus().equals(CourseStatus.DRAFT)){
            System.out.println("Cannot publish courses which are not in draft phase");
            return;
        }
        if(this.lessonIDs.isEmpty()){
            System.out.println("Please add a lesson before publishing a course!!");
            return;
        }

        this.setStatus(CourseStatus.PUBLISHED);
        System.out.println("Course was published");
    }

    public void archive(){
        if(this.getStatus().equals(CourseStatus.ARCHIVED)){
            System.out.println("Course is already archived");
            return;
        }
        this.setStatus(CourseStatus.ARCHIVED);
    }

    /**
     * Add a lesson based on the ID
     * @param lessonID = id of the lesson which is to be added to the course
     */
    public void addLesson(String lessonID){
        for(String currentLesson : lessonIDs){
            if(currentLesson.equals(lessonID)){
                System.out.println("Lesson already exists");
                return;
            }
        }
        lessonIDs.add(lessonID);
    }


    /**
     * Helper enum
     */
    private enum CourseStatus{
        DRAFT,
        PUBLISHED,
        ARCHIVED
    }
}

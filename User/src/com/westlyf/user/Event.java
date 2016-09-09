package com.westlyf.user;

import com.westlyf.controller.PracticalExerciseMakerController;
import com.westlyf.domain.exercise.practical.PracticalExercise;
import com.westlyf.domain.exercise.quiz.Exam;
import com.westlyf.domain.exercise.quiz.QuizExercise;
import com.westlyf.domain.lesson.Lesson;
import com.westlyf.domain.lesson.TextLesson;
import com.westlyf.domain.lesson.VideoLesson;

/**
 * Created by robertoguazon on 08/09/2016.
 */
public class Event {

    private final String lessonId;

    private int totalItems;
    private int totalScores;
    private int views;
    private EventType eventType;

    public Event(Lesson lesson, int score, int items) {
        this.lessonId = lesson.getLessonId();
        update(score,items);
        eventTypeOf(lesson);
    }

    public void update(int score, int items) {
        totalScores += score;
        totalItems += items;
        views++;
    }

    public String getLessonId() {
        return lessonId;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getTotalScores() {
        return totalScores;
    }

    public double getTotalPercentage() {
        return (double)(Math.round((totalScores/totalItems) * 100.0) / 100.0);
    }

    public EventType eventTypeOf(Lesson lesson) {
        if (lesson instanceof Exam) {
            return EventType.EXAM;
        }else if (lesson instanceof TextLesson) {
            return EventType.TEXT_LESSON;
        } else if (lesson instanceof VideoLesson) {
            return EventType.VIDEO_LESSON;
        } else if (lesson instanceof QuizExercise) {
            return EventType.QUIZ_EXERCISE;
        } else if (lesson instanceof PracticalExercise) {
            return EventType.PRACTICAL_EXERCISE;
        }

        return null;
    }

    public EventType getEventType() {
        return eventType;
    }

    public int getViews() {
        return views;
    }
}

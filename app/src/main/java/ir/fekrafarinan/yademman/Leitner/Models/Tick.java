package ir.fekrafarinan.yademman.Leitner.Models;

public class Tick {
    private int id;
    private int subjectId;
    private int year;
    private int lessonId;

    public Tick(int subjectId, int year, int lessonId) {
        this.subjectId = subjectId;
        this.year = year;
        this.lessonId = lessonId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    @Override
    public boolean equals(Object obj) {
        Tick tick = (Tick) obj;
        if (tick.getYear() == this.getYear() && tick.getSubjectId() == getSubjectId() &&
                tick.getLessonId() == getLessonId())
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "subjectId: " + subjectId + " id: " + id + " year: " + year + " lessonId: " + lessonId;
    }
}

package ir.fekrafarinan.yademman.Leitner.Models;

import java.util.Date;

public class Notification {
    private int id = 0;
    private String title = " ";
    private String message = " ";
    private Date date;

    public Notification(String title, String message, Date date) {
        this.title = title;
        this.message = message;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

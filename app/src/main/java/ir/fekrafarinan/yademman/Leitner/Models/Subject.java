package ir.fekrafarinan.yademman.Leitner.Models;

import java.io.Serializable;


public class Subject implements Serializable {
    private int id;
    private String name;

    public Subject(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Subject){
            return id==((Subject) obj).getId();
        }else {
            return false;
        }
    }
}

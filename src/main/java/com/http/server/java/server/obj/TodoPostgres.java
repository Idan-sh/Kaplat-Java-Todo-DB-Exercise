package com.http.server.java.server.obj;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "todos")
public class TodoPostgres implements Serializable, Todo {
    @Id
    @Column(name = "rawid", nullable = false)
    private int rawId;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "duedate", nullable = false)
    private long dueDate;
    @Column(name = "state", nullable = false)
    private String state = "PENDING";

    public TodoPostgres() {
    }

    public TodoPostgres(int rawId, String title, String content, long dueDate, String state) {
        this.rawId = rawId;
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        this.state = state;
    }

    public void setRawId(int rawId) {
        this.rawId = rawId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public int getRawId() {
        return rawId;
    }

    public String getTitle() {
        return title;
    }

    public long getDueDate() {
        return dueDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getContent() { return content; }
}

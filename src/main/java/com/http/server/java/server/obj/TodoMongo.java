package com.http.server.java.server.obj;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "todos")
public class TodoMongo implements Serializable {
    @Id
    private int rawId;
    private String title;
    private String content;
    private long dueDate;
    private String state = "PENDING";

    public TodoMongo() {
    }

    public TodoMongo(int rawId, String title, String content, long dueDate, String state) {
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

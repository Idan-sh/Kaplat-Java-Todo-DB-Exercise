package com.http.server.java.server.obj;

public interface Todo {
    public static int getNofTodos() {
        return -1; // todo - implement
    }

    public void setRawId(int rawId);

    public void setTitle(String title);

    public void setContent(String content);

    public void setDueDate(long dueDate);

    public int getRawId();

    public String getTitle();

    public long getDueDate();

    public String getState();

    public void setState(String state);

    public String getContent();
}

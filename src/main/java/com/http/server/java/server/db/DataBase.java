package com.http.server.java.server.db;

import com.http.server.java.server.obj.Todo;

import java.util.ArrayList;
import java.util.Arrays;

public class DataBase {
    ArrayList<Todo> db = new ArrayList<>(); // TODO - delete this and save into the new databases containers
    private int nofTodos = 0;

    public int getNofTodos() {
        return nofTodos;
    }

    /**
     * Inserts a to-do to the database.
     * If the to-do is not valid, throws exception with a corresponding message.
     */
    // TODO- insert to both databases, if a logical error occurred, then don't save into any of them
    public void insertToDb(Todo todo) throws Exception {
        if(isTitleTaken(todo.getTitle())) {
//            Todo.decreaseIdGenerator();
            throw new Exception("Error: TODO with the title [" + todo.getTitle() +
                    "] already exists in the system");
        }

        if(isDateInPast(todo.getDueDate())) {
//            Todo.decreaseIdGenerator();
            throw new Exception("Error: Can’t create new TODO that " +
                    "its due date is in the past");
        }

        // to-do is valid. Adding it to database.
        db.add(todo);
        nofTodos++;
    }

    /**
     * Checks if there is a to-do in the database with the given title.
     */
    public Boolean isTitleTaken(String title) {
        for(Todo todo : db) {
            if(title.equals(todo.getTitle()))
                return true;
        }
        return false;
    }

    /**
     * Checks if a given date is in the past.
     */
    public Boolean isDateInPast(long date) {
        return date < System.currentTimeMillis();
    }

    /**
     * Checks if a given status is one of the following:
     * {"PENDING", "LATE", "DONE"}
     */
    private Boolean isStatusValid(String status) {
        return (status.equals("PENDING") || status.equals("LATE") || status.equals("DONE"));
    }

    private Boolean isIdValid(int id) {
        return (id <= Todo.getNofTodos() && id > 0);
    }

    private Boolean isSortByValid(String sortBy) {
        return (sortBy == null || sortBy.equals("ID") || sortBy.equals("DUE_DATE") || sortBy.equals("TITLE"));
    }

    /**
     * Returns the number of TODOs with the given filter.
     */
    // TODO- get the nof todos using the new query param, that will indicate from which DB we should get the nof todos.
    public Integer getNofTodos(String filter, String persistenceMethod) throws Exception {
        if(filter.equals("ALL"))
            return nofTodos;
        if(!isStatusValid(filter))
            throw new Exception("Error: invalid filter");

        Integer counter = 0;
        for(Todo todo : db) {
            if(filter.equals(todo.getState()))
                counter++;
        }

        return counter;
    }

    /**
     * Updates the status of a to-do with the given id.
     * @return Previous status before updating
     */
    // TODO- update todo status in both DBs, if logical error occurred, do nothing.
    public String updateTodoStatus(int id, String status) throws Exception {
        if(!isIdValid(id))
            throw new Exception("Error: no such TODO with id " + id);
        if(!isStatusValid(status))
            throw new Exception("Error: status given is invalid");

        String res;
        for(Todo todo : db) {
            if(todo.getRawId() == id) { // found to-do with the id given.
                res = todo.getState(); // Get previous status.
                todo.setState(status); // Set new stats.
                return res; // Return the previous status.
            }
        }
        throw new Exception("Error: internal error in updateTodoStatus");
    }


    /**
     * Deletes to-do with the given id.
     * @return number of TODOs after deletion
     */
    // TODO- delete the todo from both DBs, don't delete from neither if error occurred.
    public int deleteTodo(int id) throws Exception {
        // Try to remove to-do from database.
        if(isIdValid(id) && db.removeIf(todo -> todo.getRawId() == id))
            return --nofTodos;

        throw new Exception("Error: no such TODO with id " + id);
    }


    /**
     * Returns array of TODOs that have a given status,
     * sorted by ID / Due Date / Title (ID by default if no "sortBy" was given).
     */
    // TODO- get the content with the new query param, that indicates from which DB the todo should be read.
    public Todo[] getContent(String status, String sortBy, String persistenceMethod) throws Exception {
        if(!isSortByValid(sortBy))
            throw new Exception("Error: Invalid sortBy- " + sortBy);

        int nofTodos = getNofTodos(status, persistenceMethod);
        Todo[] res = new Todo[nofTodos];

        int logSize = 0;
        for(Todo todo : db) {
            if (status.equals(todo.getState()))
                res[logSize++] = todo;
        }

        Arrays.sort(res, (o1, o2) -> {
            if (sortBy == null || sortBy.equals("ID"))
                return (o1.getRawId() - o2.getRawId());
            if (sortBy.equals("DUE_DATE"))
                return (int) (o1.getDueDate() - o2.getDueDate());
            if (sortBy.equals("TITLE"))
                return o1.getTitle().compareTo(o2.getTitle());
            return 0;
        });

        return res;
    }
}

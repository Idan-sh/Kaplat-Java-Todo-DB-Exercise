package com.http.server.java.server.service;

import ch.qos.logback.classic.Level;
import com.http.server.java.server.db.DataBase;
import jakarta.servlet.http.HttpServletRequest;
import com.http.server.java.server.obj.Response;
import com.http.server.java.server.obj.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
public class ServerLogic {
    private static DataBase db = new DataBase();
    private static final Logger requestLogger = LoggerFactory.getLogger("request-logger");
    private static final Logger todoLogger = LoggerFactory.getLogger("todo-logger");
    private int requestCounter = 0;


    /**
     * Sends response to the client that the server is up and running.
     * @return 'OK' message
     */
    @GetMapping("/todo/health")
    public ResponseEntity<String> getHealth(HttpServletRequest request) {
        db = new DataBase(); // Reset the database for new clean use (for multiple GCP users)
        long currentTime = System.currentTimeMillis();

        logRequest(request, currentTime);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }


    /**
     * Creates a to-do given by {title, content, dueDate}
     * @return The id of the new TO-DO
     */
    @PostMapping(value="/todo", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Response<Integer>> createTodo(HttpServletRequest request, @RequestBody Todo todo) {
        long currentTime = System.currentTimeMillis();

        try {
            db.insertToDb(todo); // Try to insert the to-do into the database.

            // Successfully added to-do into the database. Send response to client.
            logRequest(request, currentTime);
            logCreateTodoRequest(todo);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(todo.getRawId(), null));
        }
        catch(Exception e) {
            logRequest(request, currentTime);
            logRequestError(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(409, e.getMessage()));
        }
    }


    /**
     * Updates the status of a TO-DO with a given ID.
     * @param id ID of the TO-DO to update
     * @param status new status to update to
     * @return the precious status of the TO-DO
     */
    @PutMapping(value="/todo")
    public ResponseEntity<Response<String>> updateTodo(HttpServletRequest request, @RequestParam int id, @RequestParam String status) {
        long currentTime = System.currentTimeMillis();
        String endStr = " | request #" + (requestCounter + 1) + " ";
        todoLogger.info("Update TODO id [" + id + "] state to " + status + endStr);

        try {
            String res = db.updateTodoStatus(id, status); // Try to update the to-do.

            // Successfully updated to-do in the database. Send response to client.
            logRequest(request, currentTime);
            todoLogger.debug("Todo id [" + id + "] state change: " + res + " --> " + status + endStr);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(res, null));
        }
        catch(Exception e) {
            logRequest(request, currentTime);
            logRequestError(e.getMessage());

            if(e.getMessage().contains("no such TODO"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>("404", e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>("400", e.getMessage()));
        }
    }


    /**
     * Deletes a to-do from the database.
     * @param id ID of the TO-DO to delete
     * @return number of TODOs in the database after deletion
     */
    @DeleteMapping(value="/todo")
    public ResponseEntity<Response<Integer>> deleteTodo(HttpServletRequest request, @RequestParam int id) {
        long currentTime = System.currentTimeMillis();

        try {
            int res = db.deleteTodo(id); // Try to delete to-do

            // Successfully deleted to-do from the database. Send response to client.
            logRequest(request, currentTime);
            logDeleteTodoRequest(id);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(res, null));
        }
        catch(Exception e) {
            logRequest(request, currentTime);
            logRequestError(e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(404, e.getMessage()));
        }
    }



    /**
     * Get the total number of TODOs in the database, by a given status filter.
     * @param status ALL / PENDING / DONE
     * @return number of TODOs in the database
     */
    @GetMapping("/todo/size")
    public ResponseEntity<Response<Integer>> getCount(HttpServletRequest request,
                                                      @RequestParam String status,
                                                      @RequestParam String persistenceMethod)
    {
        long currentTime = System.currentTimeMillis();

        try {
            int res = db.getNofTodos(status, persistenceMethod); // Try to get the number of TODOs with the given status.

            // Successfully got the number of TODOs with the given status.
            logRequest(request, currentTime);
            logTodoCountRequest(status);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(res, null));
        }
        catch(Exception e) {
            logRequest(request, currentTime);
            logRequestError(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(400, e.getMessage()));
        }
    }


    /**
     * Returns array of TODOs that have the given status.
     * @param sortBy optional, the array can be sorted by ID / Due Date / Title (ID by default if "sortBy" was not given)
     */
    @GetMapping("/todo/content")
    public ResponseEntity<Response<Todo[]>> getContent(HttpServletRequest request, @RequestParam String status,
                                                       @RequestParam(required = false) String sortBy,
                                                       @RequestParam String persistenceMethod)
    {
        long currentTime = System.currentTimeMillis();

        try {
            Todo[] res = db.getContent(status, sortBy, persistenceMethod); // Try to get array of TODOs with the given status, sorted by "sortBy".
            int nofReturnedTodos = res.length;

            // Successfully got the TODOs with the given status.
            logRequest(request, currentTime);
            if(sortBy == null)
                logTodoContentRequest(status, "ID", nofReturnedTodos);
            else
                logTodoContentRequest(status, sortBy, nofReturnedTodos);

            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(res, null));
        }
        catch(Exception e) {
            logRequest(request, currentTime);
            logRequestError(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(null, e.getMessage()));
        }
    }


    /**
     * Sends the current time in milliseconds as a response to the client.
     */
    @GetMapping("/currTime")
    public String getCurrentTime(HttpServletRequest request) {
        long currentTime = System.currentTimeMillis();
        logRequest(request, currentTime);
        return "The Current Time In Millis is: " + System.currentTimeMillis();
    }


    /** LOGGING REQUESTS **/

    /**
     * Return a logger's level, if the logger exists.
     * @param loggerName name of the desired logger to be used
     * @return  the received logger's level
     */
    @GetMapping("/logs/level")
    public ResponseEntity<String> getLogLvl(HttpServletRequest request, @RequestParam("logger-name") String loggerName)
    {
        long currentTime = System.currentTimeMillis();
        String res;

        switch(loggerName){

            case "request-logger":
                res = "Success: " + ((ch.qos.logback.classic.Logger)requestLogger).getLevel().toString();
                logRequest(request, currentTime);
                return ResponseEntity.status(HttpStatus.OK).body(res);

            case "todo-logger":
                res = "Success: " + ((ch.qos.logback.classic.Logger)todoLogger).getLevel().toString();
                logRequest(request, currentTime);
                return ResponseEntity.status(HttpStatus.OK).body(res);

            default:
                res = "Failure: logger received is not valid!";
                logRequest(request, currentTime);
                logRequestError(res);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }


    /**
     * Set a logger's level, if the logger exists.
     * @param loggerName name of the desired logger to be used
     * @return  the new logger's level
     */
    @PutMapping("/logs/level")
    public ResponseEntity<String> setLogLvl(HttpServletRequest request, @RequestParam("logger-name") String loggerName,
                                            @RequestParam("logger-level") String loggerLevel)
    {
        long currentTime = System.currentTimeMillis();
        String res;
        String[] validLevels = {"ERROR", "INFO", "DEBUG"};

        // Check if logger level is valid
        if(!Arrays.asList(validLevels).contains(loggerLevel)) {
            res = "Failure: logger level received is not valid!";
            logRequest(request, currentTime);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        switch(loggerName){
            case "request-logger":
                ((ch.qos.logback.classic.Logger)requestLogger).setLevel(Level.toLevel(loggerLevel));
                res = "Success: " + loggerLevel;
                logRequest(request, currentTime);
                return ResponseEntity.status(HttpStatus.OK).body(res);

            case "todo-logger":
                ((ch.qos.logback.classic.Logger)todoLogger).setLevel(Level.toLevel(loggerLevel));
                res = "Success: " + ((ch.qos.logback.classic.Logger)todoLogger).getLevel().toString();
                logRequest(request, currentTime);
                return ResponseEntity.status(HttpStatus.OK).body(res);

            default:
                res = "Failure: logger received is not valid!";
                logRequest(request, currentTime);
                logRequestError(res);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }


    /** LOGGER METHODS **/

    private void logRequest(HttpServletRequest request, long time) {
        requestCounter++;
        requestLogger.info("Incoming request | #" + requestCounter + " | resource: " + request.getRequestURI()
                + " | HTTP Verb " + request.getMethod() + getEndStr());
        requestLogger.debug("request #" + requestCounter +
                " duration: "+ (System.currentTimeMillis() - time) +"ms" + getEndStr());
    }

    private void logCreateTodoRequest(Todo todo) {
        todoLogger.info("Creating new TODO with Title [" + todo.getTitle() + "]" + getEndStr());
        todoLogger.debug("Currently there are " + (db.getNofTodos() - 1)
                + " TODOs in the system. New TODO will be assigned with id " + todo.getRawId() + getEndStr());
    }

    private void logTodoCountRequest(String state) {
        todoLogger.info("Total TODOs count for state " + state + " is " + db.getNofTodos() + getEndStr());
    }

    private void logTodoContentRequest(String filter, String sortBy, int returnedTodos) {
        todoLogger.info("Extracting todos content. Filter: " + filter + " | Sorting by: " + sortBy + getEndStr());
        todoLogger.debug("There are a total of " + db.getNofTodos() + " todos in the system." +
                " The result holds " + returnedTodos + " todos" + getEndStr());
    }

    private void logDeleteTodoRequest(int id) {
        todoLogger.info("Removing todo id " + id + getEndStr());
        todoLogger.debug("After removing todo id [" + id + "] there are " + db.getNofTodos()
                + " TODOs in the system" + getEndStr());
    }


    private void logRequestError(String eMessage) {
        todoLogger.error(eMessage + getEndStr());
    }

    private String getEndStr() {
        return " | request #" + requestCounter + " ";
    }


}

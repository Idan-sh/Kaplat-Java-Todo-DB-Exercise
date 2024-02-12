package com.http.server.java.server.repositories;

import com.http.server.java.server.obj.TodoMongo;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface MongoTodoRepository extends MongoRepository<TodoMongo, Long> {
}

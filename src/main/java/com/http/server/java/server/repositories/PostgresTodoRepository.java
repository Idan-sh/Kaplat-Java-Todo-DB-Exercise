package com.http.server.java.server.repositories;

import com.http.server.java.server.obj.TodoPostgres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresTodoRepository extends JpaRepository<TodoPostgres, Long> {
}

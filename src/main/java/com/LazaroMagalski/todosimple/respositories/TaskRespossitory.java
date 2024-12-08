package com.LazaroMagalski.todosimple.respositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.LazaroMagalski.todosimple.models.Task;

public interface TaskRespossitory extends JpaRepository<Task, Long>{
    
    List<Task> findByUser_Id(Long id); // User usar id, _ para acessar atributo dentro da classe(Spring Boot)

    //@Query(value = "SELECT t FROM Task t WHERE t.user.id = :id")
    //List<Task> findByUser_Id(@Param("id") Long id);

    //@Query(value = "SLECT * FROM task t WHERE t.user_id = :id", nativeQuery = true)
    //List<Task> findByUser_Id(@Param("id") Long id);
}

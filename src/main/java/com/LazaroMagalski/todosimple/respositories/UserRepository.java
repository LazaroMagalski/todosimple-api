package com.LazaroMagalski.todosimple.respositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LazaroMagalski.todosimple.models.User;

@Repository
public interface UserRespository extends JpaRepository<User, Long>{ // CrudRepository


    
}

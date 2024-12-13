package com.LazaroMagalski.todosimple.respositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.LazaroMagalski.todosimple.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{ // CrudRepository

    @Transactional(readOnly = true)
    User findByUsername(String username);
    
}

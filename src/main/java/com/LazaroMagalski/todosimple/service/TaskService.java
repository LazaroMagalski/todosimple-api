package com.LazaroMagalski.todosimple.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LazaroMagalski.todosimple.models.Task;
import com.LazaroMagalski.todosimple.models.User;
import com.LazaroMagalski.todosimple.models.enums.ProfileEnum;
import com.LazaroMagalski.todosimple.models.projection.TaskProjection;
import com.LazaroMagalski.todosimple.respositories.TaskRepository;
import com.LazaroMagalski.todosimple.security.UserSpringSecurity;
import com.LazaroMagalski.todosimple.service.exceptions.AuthorizationException;
import com.LazaroMagalski.todosimple.service.exceptions.DataBindingViolationException;
import com.LazaroMagalski.todosimple.service.exceptions.ObjectNotFoundException;

@Service
public class TaskService {
    
    @Autowired //Instancia a interface
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
            "Tarefa não encontrado! Id: " + id + ", Tipo: " + Task.class.getName()));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if(Objects.isNull(userSpringSecurity) 
        || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !userHasTak(userSpringSecurity, task))
            throw new AuthorizationException("Acesso Negado");

        return task;
    }

    public List<TaskProjection> findAllByUser(){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if(Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso Negado");
        List<TaskProjection> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    @Transactional
    public Task create(Task obj){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if(Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso Negado");

        User user = this.userService.findById(userSpringSecurity.getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(Long id){
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possivel excluir pois há entidades relacionadas!");
        }
    }

    public Boolean userHasTak(UserSpringSecurity userSpringSecurity, Task task){
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }
}

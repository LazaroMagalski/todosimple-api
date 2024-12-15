package com.LazaroMagalski.todosimple.service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.LazaroMagalski.todosimple.models.User;
import com.LazaroMagalski.todosimple.models.dto.UserCreateDTO;
import com.LazaroMagalski.todosimple.models.dto.UserUpdateDTO;
import com.LazaroMagalski.todosimple.models.enums.ProfileEnum;
import com.LazaroMagalski.todosimple.respositories.TaskRepository;
import com.LazaroMagalski.todosimple.respositories.UserRepository;
import com.LazaroMagalski.todosimple.security.UserSpringSecurity;
import com.LazaroMagalski.todosimple.service.exceptions.AuthorizationException;
import com.LazaroMagalski.todosimple.service.exceptions.DataBindingViolationException;
import com.LazaroMagalski.todosimple.service.exceptions.ObjectNotFoundException;

@Service
public class UserService {
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id) {
        UserSpringSecurity userSpringSecurity = authenticated();
        if(!Objects.nonNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) 
        && !id.equals(userSpringSecurity.getId()))
            throw new AuthorizationException("Acesso Negado !");
        Optional<User> user = this.userRepository.findById(id);
            return user.orElseThrow(() -> new ObjectNotFoundException(
            "Usuário não encontrado! Id: " + id + ", Tipo: " + User.class.getName()));
    }

    @Transactional //persistencia no banco Create/Update
    public User create(User obj){
        obj.setId(null);//segurança, o usuario sempre criar nulo
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));//salva senha encriptad
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        obj = this.userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj){
        User newObj =findById(obj.getId());
        newObj.setPassword(obj.getPassword());
        newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        return this.userRepository.save(newObj);
    }

    public void delete(Long id){
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possivel excluir pois há entidades relacionadas!");
        }
    }

    public static UserSpringSecurity authenticated(){
        try {
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }
    
    public User fromDTO(@Valid UserCreateDTO obj){
        User user = new User();
        user.setUsername(obj.getUsername());
        user.setPassword(obj.getPassword());
        return user;
    }

    public User fromDTO(@Valid UserUpdateDTO obj){
        User user = new User();
        user.setId(obj.getId());
        user.setPassword(obj.getPassword());
        return user;
    }
}

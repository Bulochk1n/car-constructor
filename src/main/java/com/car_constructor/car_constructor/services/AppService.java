package com.car_constructor.car_constructor.services;


import com.car_constructor.car_constructor.models.MyUser;
import com.car_constructor.car_constructor.repositories.MyUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AppService {

    @Autowired
    private MyUserRepository myUserRepository;
    private PasswordEncoder passwordEncoder;


    public void addUser(MyUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        myUserRepository.save(user);
    }
}

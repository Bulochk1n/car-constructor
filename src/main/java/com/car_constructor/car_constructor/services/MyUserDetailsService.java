package com.car_constructor.car_constructor.services;

import com.car_constructor.car_constructor.config.MyUserDetailsConfig;
import com.car_constructor.car_constructor.models.MyUser;
import com.car_constructor.car_constructor.repositories.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private MyUserRepository myUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        return myUser.map(MyUserDetailsConfig::new)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    }


    public Long getUserId(String username) throws UsernameNotFoundException {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if (myUser.isPresent()) {
            return myUser.get().getId();
        }
        throw new UsernameNotFoundException(username + " not found");
    }



}

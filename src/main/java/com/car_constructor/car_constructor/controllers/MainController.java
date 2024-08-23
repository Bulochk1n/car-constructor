package com.car_constructor.car_constructor.controllers;


import com.car_constructor.car_constructor.models.MyUser;
import com.car_constructor.car_constructor.services.AppService;
import com.car_constructor.car_constructor.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    @Autowired
    private AppService appService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @GetMapping("/")
    public String home(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();


        if(authentication.isAuthenticated() && !"anonymousUser".equals(currentUserName)) {
            model.addAttribute("username", currentUserName);
            Long currentUserId = myUserDetailsService.getUserId(currentUserName);
            model.addAttribute("id", currentUserId);
        }

        model.addAttribute("title", "Car Constructor");
        return "home";
    }

    @GetMapping("/new-user")
    public String newUser(Model model) {
        model.addAttribute("title", "New User");
        return "new-user";
    }

    @PostMapping("/new-user")
    public String newUserForm(@ModelAttribute MyUser user) {
        appService.addUser(user);
        return "redirect:/";
    }


    //JSON requests
//    @PostMapping("/new-user")
//    public String newUserJson(@RequestBody MyUser user) {
//        appService.addUser(user);
//        return "redirect:/";
//    }


}

package com.car_constructor.car_constructor.controllers;


import com.car_constructor.car_constructor.models.Car;
import com.car_constructor.car_constructor.models.Order;
import com.car_constructor.car_constructor.repositories.CarRepository;
import com.car_constructor.car_constructor.repositories.OrderRepository;
import com.car_constructor.car_constructor.services.CarService;
import com.car_constructor.car_constructor.services.OrderService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

@Controller

public class CarController {

    private static final String UPLOADED_FOLDER = "C:/Users/Danila/Desktop/car-constructor/src/main/resources/static/pictures";

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarService carService;


    @GetMapping("/cars")
    public String cars(Model model) {
        model.addAttribute("cars", carService.getAllCars());
        return "cars-main";
    }

    @GetMapping("/cars/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String CarsAdd(Model model) {
        return "add-car";
    }

    @PostMapping("/cars/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addCar(@RequestParam("name") String name,
                         @RequestParam("specifications") String specifications,
                         @RequestParam("price") int price,
                         @RequestParam("image") MultipartFile image, Model model){


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Car car = new Car(name, specifications, price);
        carRepository.save(car);

        if(!image.isEmpty()){
            try{
                String fileName = car.getId() + "_" + car.getName() + ".jpg";
                Path filePath = Paths.get(UPLOADED_FOLDER, fileName);

                Files.createDirectories(filePath.getParent());

                Files.write(filePath, image.getBytes());

                car.setFullImagePath("C:/Users/Danila/Desktop/car-constructor/src/main/resources/static/pictures/" + fileName);
                car.setShortImagePath("/pictures/" + fileName);
                carRepository.save(car);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/";
    }


    @GetMapping("/cars/{id}")
    public String showCar(@PathVariable(value = "id") long id, Model model) {
        Car car = carService.getCarById(id);

        if (car == null) {
            return "redirect:/cars";
        }

        model.addAttribute("car", car);
        return "show-car";
    }


    @PostMapping("/cars/{id}/remove")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String removeCar(@PathVariable(value = "id") long id){
        carService.deleteCar(id);
        return "redirect:/cars";
    }



}

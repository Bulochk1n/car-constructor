package com.car_constructor.car_constructor.services;

import com.car_constructor.car_constructor.models.Car;
import com.car_constructor.car_constructor.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public Iterable<Car> getAllCars(){
        return carRepository.findAll();
    }

    public void addCar(String name, String specifications, int price){
        Car car = new Car(name, specifications, price);
        carRepository.save(car);
    }

    public Car getCarById(long id){
        return carRepository.findById(id).get();
    }

    public void deleteCar(long id){
        Car car = carRepository.findById(id).orElseThrow();
        try{
            Files.delete(Path.of(car.getFullImagePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        carRepository.delete(car);
    }

    public boolean existsById(long id){
        return carRepository.existsById(id);
    }


}

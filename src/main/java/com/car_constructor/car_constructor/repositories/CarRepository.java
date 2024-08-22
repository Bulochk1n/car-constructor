package com.car_constructor.car_constructor.repositories;

import com.car_constructor.car_constructor.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}

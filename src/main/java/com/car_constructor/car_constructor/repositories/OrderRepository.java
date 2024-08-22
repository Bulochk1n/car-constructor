package com.car_constructor.car_constructor.repositories;

import com.car_constructor.car_constructor.models.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}

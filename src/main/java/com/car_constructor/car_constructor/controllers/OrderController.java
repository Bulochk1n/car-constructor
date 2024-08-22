package com.car_constructor.car_constructor.controllers;

import com.car_constructor.car_constructor.models.Car;
import com.car_constructor.car_constructor.models.Order;
import com.car_constructor.car_constructor.services.CarService;
import com.car_constructor.car_constructor.services.OrderService;
import com.car_constructor.car_constructor.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderController {

    @Autowired
    private CarService carService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TelegramService telegramService;

    @GetMapping("/cars/{id}/make-order")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String makeOrder(Model model, @PathVariable(value = "id") long id){
        Car car = carService.getCarById(id);

        if (car == null) {
            return "redirect:/cars";
        }

        model.addAttribute("car", car);
        return "make-order";
    }


    @PostMapping("/cars/{id}/make-order")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String makeOrder(@PathVariable(value = "id") long id,
                            @ModelAttribute Order order
    ){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        Car car = carService.getCarById(id);
        if (car == null) {
            return "redirect:/cars";
        }
        order.setUsername(authentication.getName());
        order.setCarId(car.getId());
        order.setCarName(car.getName());
        order.setPrice(car.getPrice());
        order.setSpecifications(car.getSpecifications());

        String orderMessage =
                        "New order placed: order id " +
                        order.getId() + ", Car name: " +
                        order.getCarName() + ", Price: " +
                        order.getPrice() + ", Username: " +
                        order.getUsername() + ", Customer name: " +
                        order.getCustomerName() + ", Customer address: " +
                        order.getCustomerAddress() + ", Customer phone number: " +
                        order.getCustomerPhone() + ", Customer Email: " +
                        order.getCustomerEmail();


        telegramService.sendOrderNotification(orderMessage);



        orderService.addOrder(order);
        return "redirect:/cars";
    }

    @GetMapping("/cars/orders")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showOrders(Model model){
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders-main";
    }

    @GetMapping("/cars/orders/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showOrder(@PathVariable(value = "id") long id, Model model){
        Order order = orderService.getOrderById(id);

        if (order == null) {
            return "redirect:/cars";
        }

        model.addAttribute("order", order);

        return "show-order";
    }

    @PostMapping("/cars/orders/{id}/remove")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String removeOrder(@PathVariable(value = "id") long id){
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/cars";
        }

        String orderNotification = "Order with id " + id + " was removed from orders list ";

        telegramService.sendOrderNotification(orderNotification);

        orderService.deleteOrder(order);
        return "redirect:/cars/orders";

    }

    @PostMapping("/cars/orders/{id}/complete")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String completeOrder(@PathVariable(value = "id") long id){
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/cars";
        }
        Car car = carService.getCarById(order.getCarId());
        if (car == null) {
            return "redirect:/cars";
        }

        String orderNotification = "Order with id " + id + " has been completed, car " + car.getName() +
                                    " with id " + order.getCarId() + " was removed from cars list ";

        telegramService.sendOrderNotification(orderNotification);

        orderService.deleteOrder(order);
        carService.deleteCar(car.getId());
        return "redirect:/cars";
    }
}

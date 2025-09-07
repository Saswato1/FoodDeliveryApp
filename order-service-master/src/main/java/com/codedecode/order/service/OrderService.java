package com.codedecode.order.service;

import com.codedecode.order.dto.*;
import com.codedecode.order.entity.Order;
import com.codedecode.order.mapper.OrderMapper;
import com.codedecode.order.repo.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    SequenceGenerator sequenceGenerator;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;



    public OrderDTO saveOrderInDb(OrderDTOFromFE orderDetails) {
        Integer newOrderID = sequenceGenerator.generateNextOrderId();
        UserDTO userDTO = fetchUserDetailsFromUserId(orderDetails.getUserId());
        Order orderToBeSaved = new Order(newOrderID, orderDetails.getFoodItemsList(), orderDetails.getRestaurant(), userDTO );
        orderRepo.save(orderToBeSaved);

        // Publish order event to JMS
        jmsTemplate.convertAndSend("order.queue", orderToBeSaved);

        // Assuming FoodItemsDTO has a getName() method
        List<String> foodItemNames = orderDetails.getFoodItemsList()
                .stream()
                .map(FoodItemsDTO::getItemName)
                .collect(Collectors.toList());

        OrderEventDTO event = new OrderEventDTO();
        event.setOrderId(newOrderID);
        event.setRestaurantId(orderDetails.getRestaurant().getId());
        event.setFoodItems(foodItemNames);
        event.setUserId(orderDetails.getUserId());

        // Send DTO via JMS
        jmsTemplate.convertAndSend("order.queue", event);

        return OrderMapper.INSTANCE.mapOrderToOrderDTO(orderToBeSaved);
    }

    private UserDTO fetchUserDetailsFromUserId(Integer userId) {
       return restTemplate.getForObject("http://USERINFO/user/fetchUserById/" + userId, UserDTO.class);
    }
}

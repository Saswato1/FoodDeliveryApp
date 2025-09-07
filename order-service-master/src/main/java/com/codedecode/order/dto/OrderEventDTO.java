package com.codedecode.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEventDTO implements Serializable {
    private Integer orderId;
    private Integer restaurantId;
    private List<String> foodItems;
    private Integer userId;

    // Getters and Setters


}
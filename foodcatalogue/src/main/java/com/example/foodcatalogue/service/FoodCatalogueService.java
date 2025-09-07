package com.example.foodcatalogue.service;

import com.example.foodcatalogue.dto.FoodCataloguePage;
import com.example.foodcatalogue.dto.FoodItemDTO;
import com.example.foodcatalogue.dto.Restaurant;
import com.example.foodcatalogue.entity.FoodItem;
import com.example.foodcatalogue.mapper.FoodItemMapper;
import com.example.foodcatalogue.repo.FoodItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FoodCatalogueService {

    @Autowired
    FoodItemRepo foodItemRepo;

    @Autowired
    RestTemplate restTemplate;


    public FoodItemDTO addFoodItem(FoodItemDTO foodItemDTO) {
        FoodItem foodItemSavedInDB = foodItemRepo.save(FoodItemMapper.INSTANCE.mapFoodItemDTOToFoodItem(foodItemDTO));
        return FoodItemMapper.INSTANCE.mapFoodItemToFoodItemDto(foodItemSavedInDB);
    }

    public FoodCataloguePage fetchFoodCataloguePageDetails(Integer restaurantId) {
        List<FoodItem> foodItemList =  fetchFoodItemList(restaurantId);
        Restaurant restaurant = fetchRestaurantDetailsFromRestaurantMS(restaurantId);
        return createFoodCataloguePage(foodItemList, restaurant);
    }

    private FoodCataloguePage createFoodCataloguePage(List<FoodItem> foodItemList, Restaurant restaurant) {
        FoodCataloguePage foodCataloguePage = new FoodCataloguePage();
        foodCataloguePage.setFoodItemsList(foodItemList);
        foodCataloguePage.setRestaurant(restaurant);
        return foodCataloguePage;
    }

    private Restaurant fetchRestaurantDetailsFromRestaurantMS(Integer restaurantId) {
        return restTemplate.getForObject("http://RESTAURANTLISTING/restaurant/fetchById/"+restaurantId, Restaurant.class);
    }
    // in the above URL, use the application name "RESTAURANTLISTING" from EUREKA not from application.yml file

    private List<FoodItem> fetchFoodItemList(Integer restaurantId) {
        return foodItemRepo.findByRestaurantId(restaurantId);
    }
}
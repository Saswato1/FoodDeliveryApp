package com.example.restaurantlisting.controller;

import com.example.restaurantlisting.dto.RestaurantDTO;
import com.example.restaurantlisting.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestaurantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController restaurantController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController).build();
    }

    @Test
    public void testFetchAllRestaurants() throws Exception {
        RestaurantDTO restaurant1 = new RestaurantDTO();
        restaurant1.setId(1);
        restaurant1.setName("Restaurant 1");

        RestaurantDTO restaurant2 = new RestaurantDTO();
        restaurant2.setId(2);
        restaurant2.setName("Restaurant 2");

        List<RestaurantDTO> allRestaurants = Arrays.asList(restaurant1, restaurant2);

        when(restaurantService.findAllRestaurants()).thenReturn(allRestaurants);

        mockMvc.perform(get("/restaurant/fetchAllRestaurants"))
                .andExpect(status().isOk());

        ResponseEntity<List<RestaurantDTO>> response = restaurantController.fetchAllRestaurants();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testSaveRestaurant() throws Exception {
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        restaurantDTO.setId(1);
        restaurantDTO.setName("New Restaurant");

        when(restaurantService.addRestaurantInDB(any(RestaurantDTO.class))).thenReturn(restaurantDTO);

        mockMvc.perform(post("/restaurant/addRestaurant")
                        .contentType("application/json")
                        .content("{\"id\":1,\"name\":\"New Restaurant\"}"))
                .andExpect(status().isCreated());

        ResponseEntity<RestaurantDTO> response = restaurantController.saveRestaurant(restaurantDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Restaurant", response.getBody().getName());
    }

    @Test
    public void testFindRestaurantById() throws Exception {
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        restaurantDTO.setId(1);
        restaurantDTO.setName("Restaurant 1");

        when(restaurantService.fetchRestaurantById(1)).thenReturn(new ResponseEntity<>(restaurantDTO, HttpStatus.OK));

        mockMvc.perform(get("/restaurant/fetchById/1"))
                .andExpect(status().isOk());

        ResponseEntity<RestaurantDTO> response = restaurantController.findRestaurantById(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Restaurant 1", response.getBody().getName());
    }
}
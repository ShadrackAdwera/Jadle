package dao;


import models.Foodtype;
import models.Restaurant;
import models.Review;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class Sql2oRestaurantDaoTest {
    private static Connection conn;
    private static Sql2oReviewDao reviewDao;
    private static Sql2oRestaurantDao restaurantDao;
    private static Sql2oFoodTypeDao foodTypeDao;

    @BeforeClass
    public static void setUp() throws Exception {
        String connectionString = "jdbc:postgresql://localhost:5432/jadle_test"; //connect to postgres test database
        Sql2o sql2o = new Sql2o(connectionString, null, null); //changed user and pass to null for mac users...Linux & windows need strings
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodTypeDao = new Sql2oFoodTypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();
    }


    @After
    public void tearDown() throws Exception {
        System.out.println("clearing database");
        restaurantDao.clearAll(); //clear all restaurants after every test
        foodTypeDao.clearAll(); //clear all restaurants after every test
        reviewDao.clearAll();
    }
    @AfterClass
    public static void shutDown() throws Exception{ //changed to static
        conn.close(); // close connection once after this entire test file is finished
        System.out.println("connection closed");
    }

    @Test
    public void add_restaurantAddedSuccessfully() throws Exception {
        Restaurant restaurant = setUpRestaurant();
        assertEquals(1, restaurantDao.getAll().size());
    }

//    @Test
//    public void update() {
//        Restaurant restaurant = setUpRestaurant();
//        restaurantDao.update(1,"kwa madhe","22","44","2ws","eee","ccc");
//
//    }

    @Test
    public void getAll_returnsAllRestaurant() {
        Restaurant restaurant = setUpRestaurant();
        Restaurant restaurant1 = setUpRestaurant();
        assertEquals(2, restaurantDao.getAll().size());
    }

    @Test
    public void findById() {
        Restaurant restaurant = setUpRestaurant();
        int idOfRestaurant = restaurant.getId();
        assertEquals(idOfRestaurant, restaurantDao.findById(restaurant.getId()).getId());
    }

    @Test
    public void deleteById() {
        Restaurant restaurant = setUpRestaurant();
        int foundRestaurant = restaurantDao.findById(restaurant.getId()).getId();
        restaurantDao.deleteById(foundRestaurant);
        assertEquals(0, restaurantDao.getAll().size());
    }

    @Test
    public void clearAll() {
        Restaurant restaurant = setUpRestaurant();
        restaurantDao.clearAll();
        assertEquals(0, restaurantDao.getAll().size());
    }
    @Test
    public void RestaurantReturnsFoodTypesCorrectly() throws Exception {
        Foodtype testFoodtype  = new Foodtype("Seafood");
        foodTypeDao.add(testFoodtype);

        Foodtype otherFoodtype  = new Foodtype("Bar Food");
        foodTypeDao.add(otherFoodtype);

        Restaurant testRestaurant = setUpRestaurant();
        restaurantDao.add(testRestaurant);
        restaurantDao.addRestaurantToFoodType(testRestaurant,testFoodtype);
        restaurantDao.addRestaurantToFoodType(testRestaurant,otherFoodtype);

        Foodtype[] foodtypes = {testFoodtype, otherFoodtype}; //oh hi what is this? Observe how we use its assertion below.

        assertEquals(Arrays.asList(foodtypes), restaurantDao.getAllFoodTypesByRestaurant(testRestaurant.getId()));
    }

    //helper method
    public Restaurant setUpRestaurant(){
        Restaurant restaurant = new Restaurant("Ankole","Kilimani","112-33","07654321","ankole.com","ankole@com");
        restaurantDao.add(restaurant);
        return restaurant;
    }
    public Foodtype setUpFoodType(){
        Foodtype foodtype = new Foodtype("Fish");
        foodTypeDao.add(foodtype);
        return foodtype;
    }
    public Foodtype setupNewFoodtype(){
        Foodtype foodtype = new Foodtype("Chicken");
        foodTypeDao.add(foodtype);
        return foodtype;
    }
    public Review setupReview() {
        Review review = new Review("Kim", 3,"Great", 555);
        reviewDao.add(review);
        return review;
    }

    public Review setupReviewForRestaurant(Restaurant restaurant) {
        Review review = new Review("Kim", 4,"Great", restaurant.getId());
        reviewDao.add(review);
        return review;
    }

    public Restaurant setupAltRestaurant(){
        Restaurant restaurant = new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
        restaurantDao.add(restaurant);
        return restaurant;
    }
}

package dao;


import models.Restaurant;
import models.Review;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Sql2oRestaurantDaoTest {
    private Connection conn;
    private Sql2oReviewDao reviewDao;
    private Sql2oRestaurantDao restaurantDao;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:DB/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        reviewDao = new Sql2oReviewDao(sql2o);
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
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

    //helper method
    public Restaurant setUpRestaurant(){
        Restaurant restaurant = new Restaurant("Ankole","Kilimani","112-33","07654321","ankole.com","ankole@com");
        restaurantDao.add(restaurant);
        return restaurant;
    }
}

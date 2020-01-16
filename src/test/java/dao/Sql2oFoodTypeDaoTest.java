package dao;

import models.Foodtype;
import models.Restaurant;
import models.Review;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class Sql2oFoodTypeDaoTest {
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
    public void shutDown() throws Exception { //changed to static
        System.out.println("clearing database");
        restaurantDao.clearAll(); //clear all restaurants after every test
        foodTypeDao.clearAll(); //clear all restaurants after every test
        reviewDao.clearAll();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        conn.close(); // close connection once after this entire test file is finished
        System.out.println("connection closed");

    }

    @Test
    public void add_savesFoodTypeSuccessfully() {
        Foodtype foodtype = setUpFoodType();
        assertEquals(1, foodTypeDao.getAll().size());
    }

    @Test
    public void getAll() {
        Foodtype foodtype = setUpFoodType();
        Foodtype foodtype1 = setUpFoodType();
        assertEquals(2, foodTypeDao.getAll().size());
    }

    @Test
    public void deleteById() {
        Foodtype foodtype = setUpFoodType();
        assertEquals(1, foodTypeDao.getAll().size());
        foodTypeDao.deleteById(foodtype.getId());
        assertEquals(0, foodTypeDao.getAll().size());
    }

    @Test
    public void clearAll() {
        Foodtype foodtype = setUpFoodType();
        Foodtype foodtype1 = setUpFoodType();
        assertEquals(2, foodTypeDao.getAll().size());
        foodTypeDao.clearAll();
        assertEquals(0, foodTypeDao.getAll().size());


    }

    @Test
    public void addFoodTypeToRestaurantAddsTypeCorrectly() throws Exception {

        Restaurant testRestaurant = setupRestaurant();
        Restaurant altRestaurant = setupAltRestaurant();

        restaurantDao.add(testRestaurant);
        restaurantDao.add(altRestaurant);

        Foodtype testFoodtype = setupNewFoodtype();

        foodTypeDao.add(testFoodtype);

        foodTypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodTypeDao.addFoodtypeToRestaurant(testFoodtype, altRestaurant);

        assertEquals(2, foodTypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }

    @Test
    public void deleteingRestaurantAlsoUpdatesJoinTable() throws Exception {
        Foodtype testFoodtype = new Foodtype("Seafood");
        foodTypeDao.add(testFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);

        Restaurant altRestaurant = setupAltRestaurant();
        restaurantDao.add(altRestaurant);

        restaurantDao.addRestaurantToFoodType(testRestaurant, testFoodtype);
        restaurantDao.addRestaurantToFoodType(altRestaurant, testFoodtype);

        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(0, restaurantDao.getAllFoodTypesByRestaurant(testRestaurant.getId()).size());
    }

    //helper
    public Foodtype setUpFoodType() {
        Foodtype foodtype = new Foodtype("Fish");
        foodTypeDao.add(foodtype);
        return foodtype;
    }

    public Foodtype setupNewFoodtype() {
        Foodtype foodtype = new Foodtype("Chicken");
        foodTypeDao.add(foodtype);
        return foodtype;
    }

    public Review setupReview() {
        Review review = new Review("Deez Nuts", 3, "HA!", 3);
        reviewDao.add(review);
        return review;
    }

    public Review setupReviewForRestaurant(Restaurant restaurant) {
        Review review = new Review("Kim", 4, "Great", restaurant.getId());
        reviewDao.add(review);
        return review;
    }

    public Restaurant setupRestaurant() {
        Restaurant restaurant = new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
        restaurantDao.add(restaurant);
        return restaurant;
    }

    public Restaurant setupAltRestaurant() {
        Restaurant restaurant = new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
        restaurantDao.add(restaurant);
        return restaurant;
    }
}

package dao;

import models.Foodtype;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class Sql2oFoodTypeDaoTest {
    private Connection conn;
    private Sql2oReviewDao reviewDao;
    private Sql2oRestaurantDao restaurantDao;
    private Sql2oFoodTypeDao foodTypeDao;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:DB/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        reviewDao = new Sql2oReviewDao(sql2o);
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodTypeDao = new Sql2oFoodTypeDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
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


//helper
    public Foodtype setUpFoodType(){
    Foodtype foodtype = new Foodtype("Fish");
    foodTypeDao.add(foodtype);
    return foodtype;
    }
}
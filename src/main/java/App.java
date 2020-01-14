import com.google.gson.Gson;
import dao.Sql2oFoodTypeDao;
import dao.Sql2oRestaurantDao;
import dao.Sql2oReviewDao;
import models.Foodtype;
import models.Restaurant;
import models.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static spark.Spark.*;

public class App {

    public static void main(String[] args) {
        Sql2oFoodTypeDao foodtypeDao;
        Sql2oRestaurantDao restaurantDao;
        Sql2oReviewDao reviewDao;
        Connection conn;
        Gson gson = new Gson();

        String connectionString = "jdbc:h2:~/jadle.db;INIT=RUNSCRIPT from 'classpath:DB/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");

        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodTypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();


        //CREATE RESTAURANTS
        post("/restaurants/new", "application/json", (request, response) -> {
            Restaurant restaurant = gson.fromJson(request.body(), Restaurant.class);
            restaurantDao.add(restaurant);
            response.status(201);;
            return gson.toJson(restaurant);
        });

        //READ RESTAURANTS
        get("/restaurants", "application/json", (request, response) -> {
            return gson.toJson(restaurantDao.getAll());
        });

        get("/restaurants/:id", "application/json", (request, response) -> {
            int restaurantId = Integer.parseInt(request.params("id"));
            return gson.toJson(restaurantDao.findById(restaurantId));
        });

        //CREATE FOOD TYPES
        post("/foodtypes/new", "application/json", ((request, response) -> {
            Foodtype foodtype = gson.fromJson(request.body(), Foodtype.class);
            foodtypeDao.add(foodtype);
            response.status(201);
            return gson.toJson(foodtype);
        }));
        //READ FOOD TYPES
        get("/foodtype", "application/json", (request, response) -> {
            return gson.toJson(foodtypeDao.getAll());
        });
        //FIND BY ID
        get("/restaurants/:id","application/json", (request, response) -> {
            int foodId = Integer.parseInt(request.params("foodtypeId"));
            return gson.toJson(foodtypeDao.findById(foodId));
        });
        //One to many route
        post("restaurant/:restaurantId/reviews/new", "application/json", (request, response) -> {
            int restaurantId = Integer.parseInt(request.params("restaurantId"));
            Review review = gson.fromJson(request.body(), Review.class);
            review.setRestaurantId(restaurantId);
            reviewDao.add(review);
            response.status(201);
            return gson.toJson(review);
        });
        get("/restaurants/:id/reviews", "application/json", (request, response) -> {
            int restaurantId = Integer.parseInt(request.params("id"));
            List<Review> allReviews;
            allReviews = reviewDao.getAllReviewsByRestaurant(restaurantId);
            return gson.toJson(allReviews);
        });
        //FILTERS
        after((req, res) ->{
            res.type("application/json");
        });
    }
}
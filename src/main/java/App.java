import static spark.Spark.*;

import com.google.gson.Gson;
import models.Foodtype;
import models.Restaurant;
import models.Review;
import dao.Sql2oFoodTypeDao;
import dao.Sql2oRestaurantDao;
import dao.Sql2oReviewDao;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        Sql2oFoodTypeDao foodtypeDao;
        Sql2oRestaurantDao restaurantDao;
        Sql2oReviewDao reviewDao;
        Connection conn;
        Gson gson = new Gson();

//        String connectionString = "jdbc:postgresql://localhost:5432/jadle";
//        Sql2o sql2o = new Sql2o(connectionString, "adwesh", "password");
        String connectionString = "jdbc:postgresql://ec2-174-129-33-2.compute-1.amazonaws.com:5432/dbj6tt6lfshtn";
        Sql2o sql2o = new Sql2o(connectionString, "kwfcxpkpzbxmdr", "83e153a0e7ac32fdd721072eec03cd29347970b75ec6a848377cb75dd0a83fb4");

        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodTypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();

        get("/", "application/json", (req, res) -> {
            System.out.println(restaurantDao.getAll());

            if (restaurantDao.getAll().size() > 0) {
                return gson.toJson(restaurantDao.getAll());
            } else {
                return "{\"message\":\"I'm sorry, but no restaurants are currently listed in the database.\"}";
            }

        });

        //CREATE
        post("/restaurants/:restaurantId/foodtype/:foodtypeId", "application/json", (req, res) -> {

            int restaurantId = Integer.parseInt(req.params("restaurantId"));
            int foodtypeId = Integer.parseInt(req.params("foodtypeId"));
            Restaurant restaurant = restaurantDao.findById(restaurantId);
            Foodtype foodtype = foodtypeDao.findById(foodtypeId);
                //both exist and can be associated
                foodtypeDao.addFoodtypeToRestaurant(foodtype, restaurant);
                res.status(201);
                return gson.toJson(String.format("Restaurant '%s' and Foodtype '%s' have been associated", restaurant.getName(), foodtype.getName()));
        });

        get("/restaurants/:id/foodtypes", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
                return gson.toJson(restaurantDao.getAllFoodTypesByRestaurant(restaurantId));

        });

        get("/foodtypes/:id/restaurants", "application/json", (req, res) -> {
            int foodtypeId = Integer.parseInt(req.params("id"));
            Foodtype foodtypeToFind = foodtypeDao.findById(foodtypeId);
                return gson.toJson(foodtypeDao.getAllRestaurantsForAFoodtype(foodtypeId));
        });


        post("/restaurants/:restaurantId/reviews/new", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("restaurantId"));
            Review review = gson.fromJson(req.body(), Review.class);
            review.setCreatedat();
            review.setFormattedCreatedAt();
            review.setRestaurantId(restaurantId); //comes from our route, not JSON input.
            reviewDao.add(review);
            res.status(201);
            return gson.toJson(review);
        });

        post("/foodtypes/new", "application/json", (req, res) -> {
            Foodtype foodtype = gson.fromJson(req.body(), Foodtype.class);
            foodtypeDao.add(foodtype);
            res.status(201);
            return gson.toJson(foodtype);
        });

        //READ
        get("/restaurants", "application/json", (req, res) -> {
            System.out.println(restaurantDao.getAll());

            if (restaurantDao.getAll().size() > 0) {
                return gson.toJson(restaurantDao.getAll());
            } else {
                return "{\"message\":\"I'm sorry, but no restaurants are currently listed in the database.\"}";
            }

        });

        get("/restaurants/:id", "application/json", (req, res) -> { //accept a request in format JSON from an app
            int restaurantId = Integer.parseInt(req.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            return gson.toJson(restaurantToFind);
        });

        get("/restaurants/:id/reviews", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("id"));

            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            List<Review> allReviews;
            allReviews = reviewDao.getAllReviewsByRestaurant(restaurantId);

            return gson.toJson(allReviews);
        });

        get("/restaurants/:id/sortedReviews", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            List<Review> allReviews;
            allReviews = reviewDao.getAllReviewsByRestaurantSortedNewestToOldest(restaurantId);
            return gson.toJson(allReviews);
        });

        get("/foodtypes", "application/json", (req, res) -> {
            return gson.toJson(foodtypeDao.getAll());
        });


        //CREATE
        post("/restaurants/new", "application/json", (req, res) -> {
            Restaurant restaurant = gson.fromJson(req.body(), Restaurant.class);
            restaurantDao.add(restaurant);
            res.status(201);
            return gson.toJson(restaurant);
        });

//        //FILTERS
//        exception(ApiException.class, (exception, req, res) -> {
//            ApiException err = exception;
//            Map<String, Object> jsonMap = new HashMap<>();
//            jsonMap.put("status", err.getStatusCode());
//            jsonMap.put("errorMessage", err.getMessage());
//            res.type("application/json");
//            res.status(err.getStatusCode());
//            res.body(gson.toJson(jsonMap));
//        });


        after((req, res) -> {
            res.type("application/json");
        });

    }
}
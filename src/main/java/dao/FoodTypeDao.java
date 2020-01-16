package dao;

import models.Foodtype;
import models.Restaurant;

import java.util.List;

public interface FoodTypeDao {
    //create
    void add(Foodtype foodtype);

    void addFoodtypeToRestaurant(Foodtype foodtype, Restaurant restaurant);

    //read
    Foodtype findById(int id);

    List<Foodtype> getAll();

    List<Restaurant> getAllRestaurantsForAFoodtype(int id);

    //update
    //omit for now

    //delete
    void deleteById(int id);

    void clearAll();
}

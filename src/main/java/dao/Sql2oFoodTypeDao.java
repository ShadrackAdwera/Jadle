package dao;

import models.Foodtype;
import models.Restaurant;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.ArrayList;
import java.util.List;

public class Sql2oFoodTypeDao implements FoodTypeDao {
    private final Sql2o sql2o;

    public Sql2oFoodTypeDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Foodtype foodtype) {
        try (Connection con = sql2o.open()) {
            String sql = "INSERT INTO foodtypes (name) VALUES (:name)";
            int id = (int) con.createQuery(sql, true)
                    .bind(foodtype)
                    .executeUpdate()
                    .getKey();
            foodtype.setId(id);
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public List<Foodtype> getAll() {
        try (Connection con = sql2o.open()) {
            String sql = "SELECT * FROM foodtypes";
            return con.createQuery(sql)
                    .executeAndFetch(Foodtype.class);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE from foodtypes WHERE id=:id";
        String deleteJoin = "DELETE from restaurants_foodtypes WHERE foodtypeid = :foodtypeId";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();

            con.createQuery(deleteJoin)
                    .addParameter("foodtypeId", id)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void clearAll() {
        try (Connection con = sql2o.open()) {
            String sql = "DELETE FROM foodtypes";
            con.createQuery(sql)
                    .executeUpdate();
        }
    }

    @Override
    public Foodtype findById(int id) {
        try (Connection con = sql2o.open()) {
            String sql = "SELECT * FROM foodtypes WHERE id = :id";
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Foodtype.class);
        }
    }

    @Override
    public void addFoodtypeToRestaurant(Foodtype foodtype, Restaurant restaurant) {
        try (Connection con = sql2o.open()) {
            String sql = "INSERT INTO restaurants_foodtypes (restaurantid, foodtypeid) VALUES (:restaurantId, :foodtypeId)";
            con.createQuery(sql)
                    .addParameter("restaurantId", restaurant.getId())
                    .addParameter("foodtypeId", foodtype.getId())
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }

    }

    @Override
    public List<Restaurant> getAllRestaurantsForAFoodtype(int foodtypeId) {
        List<Restaurant> restaurants = new ArrayList<>();
        String joinQuery = "SELECT restaurantid FROM restaurants_foodtypes WHERE foodtypeid = :foodtypeId";

        try (Connection con = sql2o.open()) {
            List<Integer> allRestaurantIds = con.createQuery(joinQuery)
                    .addParameter("foodtypeId", foodtypeId)
                    .executeAndFetch(Integer.class);
            for (Integer restaurantId : allRestaurantIds) {
                String restaurantQuery = "SELECT * FROM restaurants WHERE id = :restaurantId";
                restaurants.add(
                        con.createQuery(restaurantQuery)
                                .addParameter("restaurantId", restaurantId)
                                .executeAndFetchFirst(Restaurant.class));
            }
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
        return restaurants;
    }

}

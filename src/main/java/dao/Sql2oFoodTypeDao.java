package dao;
import models.Foodtype;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import java.util.List;

public class Sql2oFoodTypeDao implements FoodTypeDao {
    private final Sql2o sql2o;

    public Sql2oFoodTypeDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }
    @Override
    public void add(Foodtype foodtype){
        try (Connection con = sql2o.open()){
            String sql = "INSERT INTO foodtypes (name) VALUES (:name)";
            int id = (int) con.createQuery(sql,true)
                    .bind(foodtype)
                    .executeUpdate()
                    .getKey();
            foodtype.setId(id);
        } catch (Sql2oException ex) { System.out.println(ex);}
    }
    @Override
    public List<Foodtype> getAll(){
        try(Connection con = sql2o.open()) {
            String sql = "SELECT * FROM foodtypes";
            return con.createQuery(sql)
                    .executeAndFetch(Foodtype.class);
        }
    }
    @Override
    public void deleteById(int id){
        try(Connection con = sql2o.open()) {
            String sql = "DELETE FROM foodtypes WHERE id = :id";
            con.createQuery(sql)
                    .addParameter("id",id)
                    .executeUpdate();
        } catch (Sql2oException ex) { System.out.println(ex);}
    }
    public void clearAll(){
        try (Connection con = sql2o.open()) {
            String sql = "DELETE FROM foodtypes";
            con.createQuery(sql)
                    .executeUpdate();
        }
    }
}

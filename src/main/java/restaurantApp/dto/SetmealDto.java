package restaurantApp.dto;

import lombok.Data;
import restaurantApp.entity.Dish;
import restaurantApp.entity.Setmeal;
import restaurantApp.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private String categoryName;

    private List<SetmealDish> SetmealDishes;
}

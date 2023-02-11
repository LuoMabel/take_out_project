package restaurantApp.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import restaurantApp.entity.Dish;
import restaurantApp.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}

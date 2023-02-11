package restaurantApp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import restaurantApp.common.R;
import restaurantApp.dto.DishDto;
import restaurantApp.entity.Dish;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);
}

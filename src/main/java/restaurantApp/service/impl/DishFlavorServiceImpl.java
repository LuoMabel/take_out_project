package restaurantApp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import restaurantApp.entity.DishFlavor;
import restaurantApp.mapper.DishFlavorMapper;
import restaurantApp.service.DishFlavorService;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}

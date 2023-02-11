package restaurantApp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurantApp.dto.DishDto;
import restaurantApp.entity.Dish;
import restaurantApp.entity.DishFlavor;
import restaurantApp.mapper.DishMapper;
import restaurantApp.service.DishFlavorService;
import restaurantApp.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;


    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);//保存基本信息至菜品表
        Long dishId = dishDto.getId();//菜品Id
        List<DishFlavor> flavors = dishDto.getFlavors();//口味表
        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());//转为list集合，给dishId赋值
        //目前dishDto.getFlavors()没有设置dishId;需要手动处理。
        dishFlavorService.saveBatch(flavors);//批量保存
        //插入菜品对应的口味数据
    }
}

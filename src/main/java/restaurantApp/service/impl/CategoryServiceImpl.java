package restaurantApp.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurantApp.common.CustomerException;
import restaurantApp.entity.Category;
import restaurantApp.entity.Dish;
import restaurantApp.entity.Setmeal;
import restaurantApp.mapper.CategoryMapper;
import restaurantApp.service.CategoryService;
import restaurantApp.service.DishService;
import restaurantApp.service.SetmealService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if(count1 >0){
            //关联了菜品，准备抛出异常
            throw new CustomerException("关联了菜品，无法删除");
        }

        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealQueryWrapper);
        if(count2 >0){
            //关联了套餐，准备抛出异常
            throw new CustomerException("关联了套餐，无法删除");
        }
        super.removeById(id);
    }
}

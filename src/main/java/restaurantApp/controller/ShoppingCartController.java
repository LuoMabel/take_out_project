package restaurantApp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import restaurantApp.common.BaseContext;
import restaurantApp.common.R;
import restaurantApp.entity.ShoppingCart;
import restaurantApp.service.DishService;
import restaurantApp.service.SetmealService;
import restaurantApp.service.ShoppingCartService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加菜品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getId();//用户Id
        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);//当前用户的菜品

        Long dishId = shoppingCart.getDishId();
        if(dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //当前菜品是否在购物车里？
        ShoppingCart sc = shoppingCartService.getOne(queryWrapper);

        if(sc == null){
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            sc = shoppingCart;
        }else{
            Integer number = sc.getNumber();
            sc.setNumber(number+1);
            shoppingCartService.updateById(sc);
        }
        return R.success(sc);
    }
    /**
     * 查询购物车信息
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     *
     * 修改购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("修改购物车数据{}",shoppingCart.toString());
        Long dishId = shoppingCart.getDishId();
        Long userId = BaseContext.getId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        if(dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart sc = shoppingCartService.getOne(queryWrapper);
        Integer number = sc.getNumber();
        if(number > 1) {
            sc.setNumber(number - 1);
            shoppingCartService.updateById(sc);
        }else{
            sc.setNumber(0);
            shoppingCartService.removeById(sc.getId());
        }
        return R.success(sc);
    }
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long userId = BaseContext.getId();
        shoppingCartService.removeById(userId);
        return R.success("清除成功");
    }
}

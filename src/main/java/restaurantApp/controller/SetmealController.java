package restaurantApp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import restaurantApp.common.R;
import restaurantApp.dto.DishDto;
import restaurantApp.dto.SetmealDto;
import restaurantApp.entity.Category;
import restaurantApp.entity.Dish;
import restaurantApp.entity.Setmeal;
import restaurantApp.entity.SetmealDish;
import restaurantApp.service.CategoryService;
import restaurantApp.service.DishService;
import restaurantApp.service.SetmealDishService;
import restaurantApp.service.SetmealService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPageInfo = new Page<>();
        //构造分页查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        
        //拷贝到dto对象里
        BeanUtils.copyProperties(pageInfo,dtoPageInfo,"records");
        
        //查询分类名称
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);

            return setmealDto;
        }).collect(Collectors.toList());

        dtoPageInfo.setRecords(list);
        return R.success(dtoPageInfo);
    }

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody SetmealDto setmealDto) {
        setmealService.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> list = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(list);
        return R.success("保存成功");
    }

    /**
     * 批量删除或删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteMore(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);

        setmealService.remove(queryWrapper);

        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(queryWrapper1);

        return R.success("删除成功");
    }

    /**
     * 批量起售
     * @param st
     * @param ids
     * @return
     */
    @PostMapping("status/{st}")
    public R<String> changeStatus(@PathVariable Integer st,@RequestParam List<Long> ids){

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = setmealService.list(queryWrapper);
        List<Setmeal> collect = list.stream().map((item) -> {
            item.setStatus(st);
            return item;
        }).collect(Collectors.toList());

        setmealService.updateBatchById(collect);
        return  R.success("状态修改成功！");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        Setmeal setmeal = setmealService.getById(id);//查询到套餐
        Long categoryId = setmeal.getCategoryId();
        Category category = categoryService.getById(categoryId);
        String categoryName = category.getName();
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setCategoryName(categoryName);
        //查询套餐的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateById(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> collect = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        setmealDishService.saveBatch(collect);
        return R.success("修改成功！");
    }
    @GetMapping("/list")
    public R<List<SetmealDto>> list(Long categoryId,Integer status){
        if(status == 0){
            return R.error("菜品已售空");
        }
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,categoryId);
        List<Setmeal> list = setmealService.list(queryWrapper);//这个套餐分类具体的套组

        List<SetmealDto> setmealDtoList = null;
        //把套餐关联的菜品表加入
        setmealDtoList = list.stream().map((item)->{
            SetmealDto sd = new SetmealDto();

            BeanUtils.copyProperties(item,sd);
            Long id = item.getId();//获取到套餐id

            LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(SetmealDish::getSetmealId,id);
            List<SetmealDish> dishes = setmealDishService.list(queryWrapper1);

            sd.setSetmealDishes(dishes);

            return sd;
        }).collect(Collectors.toList());
        return R.success(setmealDtoList);
    }
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> getDish(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        List<DishDto> collect = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long dishId = item.getDishId();
            Dish dish = dishService.getById(dishId);
            BeanUtils.copyProperties(dish, dishDto);

            return dishDto;
        }).collect(Collectors.toList());
        return R.success(collect);
    }

}

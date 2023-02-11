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
import restaurantApp.entity.Category;
import restaurantApp.entity.Dish;
import restaurantApp.entity.DishFlavor;
import restaurantApp.service.CategoryService;
import restaurantApp.service.DishFlavorService;
import restaurantApp.service.DishService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPageInfo = new Page<>(page,pageSize);
        //查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
            queryWrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
            dishService.page(pageInfo,queryWrapper);
        //查询到结果
        BeanUtils.copyProperties(pageInfo,dishDtoPageInfo,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category cate = categoryService.getById(categoryId);
            if(cate != null){
                dishDto.setCategoryName(cate.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPageInfo.setRecords(list);
        return R.success(dishDtoPageInfo);
    }
    @PostMapping("/status/{st}")
    public R<String> setStatus(@PathVariable int st, String ids){
        //处理string 转成Long
        String[] split = ids.split(",");
        List<Long> idList = Arrays.stream(split).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());

        //将每个id new出来一个Dish对象，并设置状态
        List<Dish> dishes = idList.stream().map((item) -> {
            Dish dish = new Dish();
            dish.setId(item);
            dish.setStatus(st);
            return dish;
        }).collect(Collectors.toList()); //Dish集合

        log.info("status ids : {}",ids);
        dishService.updateBatchById(dishes);//批量操作
        return R.success("操作成功");
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        log.info("查询菜品信息{}",id);
        Dish dish = dishService.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //floavor是一个List<DishFlavor>的集合，目前只知道dishId;
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        log.info("查询口味信息{}",list);
        dishDto.setFlavors(list);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        //修改菜品表
        dishService.updateById(dishDto);
        //修改口味表
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //获取当前口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(String ids){
        String[] split = ids.split(","); //将每个id分开
        //每个id还是字符串，转成Long
        List<Long> idList = Arrays.stream(split).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        dishService.removeByIds(idList);//执行批量删除
        log.info("删除的ids: {}",ids);
        return R.success("删除成功"); //返回成功
    }

    /**
     * 获取菜品分类列表
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> getDishType(Long categoryId,String name) {
        //构建查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        if (name == null) {
            queryWrapper.eq(Dish::getCategoryId, categoryId);
        } else {
            queryWrapper.like(Dish::getName, name);
        }
        //查询
        List<Dish> list = dishService.list(queryWrapper);

        //
        List<DishDto> dtoList = null;
        dtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);

            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dtoList);
    }
}

package restaurantApp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import restaurantApp.common.BaseContext;
import restaurantApp.common.R;
import restaurantApp.dto.OrderDto;
import restaurantApp.entity.OrderDetail;
import restaurantApp.entity.Orders;
import restaurantApp.entity.User;
import restaurantApp.service.AddressBookService;
import restaurantApp.service.OrderService;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
@Autowired
    private OrderService orderService;

    /**
     * 用户订单分页查询
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, @DateTimeFormat(pattern = "yyyy-mm-dd HH:mm:ss") Date beginTime,@DateTimeFormat(pattern = "yyyy-mm-dd HH:mm:ss")Date endTime){
    log.info("订单分页查询");
    // 创建分页对象
    Page<Orders> pageInfo = new Page<>(page, pageSize);
    // 创建查询条件对象。
    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(StringUtils.isNotEmpty(number), Orders::getNumber, number);
    if (beginTime != null) {
        queryWrapper.between(Orders::getOrderTime, beginTime, endTime);
    }
    orderService.page(pageInfo, queryWrapper);
    return R.success(pageInfo);
}
    @GetMapping("/userPage")
    public R<Page<OrderDto>> userPage(int page,int pageSize){
        log.info("查询订单详情");
        Page<OrderDto> orderDtoPage = orderService.userPage(page, pageSize);
        return R.success(orderDtoPage);
    }

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("用户下单...");

        orderService.submit(orders);
        return  R.success("用户下单成功");
    }

    @PutMapping
    public R<String> send(@RequestBody Orders orders){
        log.info("修改订单状态");
        orderService.updateById(orders);
        return R.success("订单修改成功");

    }
}

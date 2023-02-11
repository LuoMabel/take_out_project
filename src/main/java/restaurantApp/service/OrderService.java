package restaurantApp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;
import restaurantApp.common.R;
import restaurantApp.dto.OrderDto;
import restaurantApp.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
    Page<OrderDto> userPage(int page, int pageSize);
}

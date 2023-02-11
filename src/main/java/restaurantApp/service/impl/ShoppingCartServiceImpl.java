package restaurantApp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import restaurantApp.entity.ShoppingCart;
import restaurantApp.mapper.ShoppingCartMapper;
import restaurantApp.service.ShoppingCartService;
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}

package restaurantApp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import restaurantApp.entity.ShoppingCart;
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}

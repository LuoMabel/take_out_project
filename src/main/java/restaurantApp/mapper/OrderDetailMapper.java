package restaurantApp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import restaurantApp.entity.OrderDetail;
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}

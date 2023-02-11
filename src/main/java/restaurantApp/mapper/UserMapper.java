package restaurantApp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.apache.ibatis.annotations.Mapper;
import restaurantApp.entity.User;
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

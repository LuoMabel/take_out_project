package restaurantApp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;
import restaurantApp.entity.Setmeal;
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
}

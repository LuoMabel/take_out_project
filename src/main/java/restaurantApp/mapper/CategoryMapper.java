package restaurantApp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import restaurantApp.entity.Category;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}

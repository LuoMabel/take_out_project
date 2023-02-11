package restaurantApp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import restaurantApp.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}

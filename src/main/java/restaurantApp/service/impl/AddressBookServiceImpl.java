package restaurantApp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;
import restaurantApp.entity.AddressBook;
import restaurantApp.mapper.AddressBookMapper;
import restaurantApp.service.AddressBookService;
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}

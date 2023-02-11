package restaurantApp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import restaurantApp.common.BaseContext;
import restaurantApp.common.R;
import restaurantApp.entity.AddressBook;
import restaurantApp.service.AddressBookService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 获取收货地址信息列表
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        log.info("获取收货地址信息列表...");
        Long userId = BaseContext.getId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId);
        queryWrapper.orderByDesc(AddressBook::getId);
        List<AddressBook> list = addressBookService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
   @PostMapping
   public R<String> add(@RequestBody AddressBook addressBook){
       log.info("新增地址{}",addressBook.toString());
        addressBook.setUserId(BaseContext.getId());
        addressBookService.save(addressBook);
        return R.success("保存成功");
   }

    /**
     * 查询单个信息
     * @param id
     * @return
     */
   @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id){
       log.info("查询单个信息id为{}",id);
       AddressBook byId = addressBookService.getById(id);
       return R.success(byId);
   }

   @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
       log.info("修改地址信息");
       addressBookService.updateById(addressBook);
       return R.success("修改成功");
   }

   @DeleteMapping
    public R<String> delete(Long ids){
       log.info("删除地址");
       addressBookService.removeById(ids);
       return R.success("删除成功");
   }

    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        // 增加日志
        log.info("addressBook:{}", addressBook);
        //1.先把所有的字段更新为0，因为只能有一个默认地址。
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getId());
        wrapper.set(AddressBook::getIsDefault, 0);
        // SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);
        //把客户端传来的改为1.
        addressBook.setIsDefault(1);
        // SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 获得默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
       LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
       queryWrapper.eq(AddressBook::getUserId,BaseContext.getId());
       queryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if(addressBook == null){
            return R.error("请求错误");
        }else{
            return R.success(addressBook);
        }
    }
}

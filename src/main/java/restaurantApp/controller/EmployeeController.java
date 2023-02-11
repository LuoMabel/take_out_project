package restaurantApp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import restaurantApp.common.R;
import restaurantApp.entity.Employee;
import restaurantApp.service.EmployeeService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    public EmployeeService employeeService;
    @PostMapping("/login")//可处理的请求路径
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        log.info("开始登陆.....");
        //1.将接受的密码进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //获得userName 并在数据库中查询有没有这个userName
        String userName = employee.getUsername();
        //构建查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        //调用Service根据条件查询，getOne是因为userName字段由unique修饰，即唯一表示
        Employee emp = employeeService.getOne(queryWrapper);
        if(emp == null){

            return R.error("用户不存在，请重新输入！");
        }

        if(!emp.getPassword().equals(password)){
            return R.error("密码错误，请重新输入");
        }
        if(emp.getStatus() == 0){
            return R.error("用户已锁定！");
        }
        //将数据存入浏览器
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("添加员工{}",employee.toString());
        //设置employee中为空的字段
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

       /* //设置操作时间，更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置更新人员操作人员
        Long emp = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(emp);
        employee.setUpdateUser(emp);*/

        employeeService.save(employee);
        return R.success("保存成功");
    }
    //获取员工信息
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page pageInfo = new Page(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        if(name != null) {
            queryWrapper.like(Employee::getName,name);
            queryWrapper.orderByDesc(Employee::getUpdateTime);
        }
        //分页查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        //更新数据库
        log.info("更改员工",employee.getId());


        /*//设置操作时间，更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());*/

        //设置更新人员操作人员
        //Long emp = (Long) request.getSession().getAttribute("employee");
        /*employee.setCreateUser(emp);
        employee.setUpdateUser(emp);*/
        employeeService.updateById(employee);
        return R.success("成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee emp = employeeService.getById(id);
        return R.success(emp);
    }


}

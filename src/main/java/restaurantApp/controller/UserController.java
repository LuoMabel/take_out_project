package restaurantApp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import restaurantApp.common.R;
import restaurantApp.entity.User;
import restaurantApp.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param u
     * @param request
     * @return
     */
    @PostMapping("/login")
    //HttpServletRequest 可以用 HttpSession 代替
    public R<String> login(@RequestBody User u, HttpServletRequest request){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,u.getPhone());
        User user = userService.getOne(queryWrapper);
        if(user == null){
            //用户第一次登陆
            user = new User();
            user.setPhone(u.getPhone());
            user.setStatus(1);
            user.setName("用户"+u.getPhone());
            userService.save(user);
        }
        //保存在浏览器
        request.getSession().setAttribute("user",user.getId());
        return R.success("登陆成功");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");

    }
}

package com.IronmanJay.business.rest;

import com.IronmanJay.business.service.UserService;
import com.IronmanJay.business.model.domain.User;
import com.IronmanJay.business.model.request.LoginUserRequest;
import com.IronmanJay.business.model.request.RegisterUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


@RequestMapping("/rest/users")
@Controller
public class UserRestApi {

    @Autowired
    private UserService userService;

    /**
     * 提供用户登录功能
     * @param username 用户名
     * @param password 密码
     * @param model 返回值
     * @return
     */
    @RequestMapping(value = "/login", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model login(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        User user = userService.loginUser(new LoginUserRequest(username, password));
        model.addAttribute("success", user != null);
        model.addAttribute("user", user);
        return model;
    }

    /**
     * 提供用户注册功能
     * @param username 用户名
     * @param password 密码
     * @param model 返回值
     * @return
     */
    @RequestMapping(value = "/register", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model addUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        if (userService.checkUserExist(username)) {
            model.addAttribute("success", false);
            model.addAttribute("message", " 用户名已经被注册！");
            return model;
        }
        model.addAttribute("success", userService.registerUser(new RegisterUserRequest(username, password)));
        return model;
    }

    /**
     * 冷启动问题，需要能够添加用户偏爱的影片类别
     * @param username 用户名
     * @param genres 数据
     * @param model 返回值
     * @return
     */
    @RequestMapping(value = "/pref", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model addPrefGenres(@RequestParam("username") String username, @RequestParam("genres") String genres, Model model) {
        User user = userService.findByUsername(username);
        user.getPrefGenres().addAll(Arrays.asList(genres.split(",")));
        user.setFirst(false);
        model.addAttribute("success", userService.updateUser(user));
        return model;
    }

}

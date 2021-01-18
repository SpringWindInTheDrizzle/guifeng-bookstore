package com.jingshi.school.bookstore.controller;

import com.jingshi.school.bookstore.model.entity.Admin;
import com.jingshi.school.bookstore.service.AdminService;
import com.jingshi.school.bookstore.util.Result;
import com.jingshi.school.bookstore.util.ResultGenerator;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
@Slf4j
@CrossOrigin
@Controller
@RequestMapping("/system")
public class AdminController {

    @Autowired
    private AdminService adminUserService;


    @PostMapping("/login/password")
    @ResponseBody
//    @ApiOperation("Login")
    public Result login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        HttpServletResponse response) {
        /*
        if (StringUtils.isEmpty(verifyCode)) {
            session.setAttribute("status", false);
            session.setAttribute("errorMsg", "验证码不能为空");
        }
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            session.setAttribute("status", false);
            session.setAttribute("errorMsg", "用户名或密码不能为空");
        }
        */
        Admin adminUser = adminUserService.login(userName, password);
        if (adminUser != null) {
            Cookie cookie1 = new Cookie("userId", String.valueOf(adminUser.getId()));
            Cookie cookie2 = new Cookie("userName", adminUser.getUserName());
            cookie1.setMaxAge(24 * 60 * 60);
            cookie2.setMaxAge(24 * 60 * 60);
            cookie1.setPath("/");
            cookie2.setPath("/");
            response.addCookie(cookie1);
            response.addCookie(cookie2);
            return ResultGenerator.genSuccessResult();
        }
        //登录失败
        return ResultGenerator.genFailResult("error");
    }

    @GetMapping("/update")
    public String getUpdate() {
        return "adminInfo";
    }

    /*
    @GetMapping("/profile")
    @ApiOperation("Profile")
    public String profile(HttpServletRequest request) {
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);
        if (adminUser == null) {
            return "admin/login";
        }
        request.setAttribute("path", "profile");
        request.setAttribute("loginUserName", adminUser.getUserName());
        return "admin/profile";
    }
     */

    @PostMapping("/profile/password")
    @ApiOperation("profile password")
    @ResponseBody
    public Result passwordUpdate(HttpServletRequest request, @RequestParam("password") String originalPassword,
                                 HttpServletResponse response) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        if (cookieMap.get("userId") == null) {
            return ResultGenerator.genFailResult("重新登录");
        }
        if (adminUserService.updatePassword(Integer.valueOf(cookieMap.get("userId").getValue()), originalPassword, originalPassword).equals("success")) {
            Cookie cookie1 = new Cookie("userId", null);
            Cookie cookie2 = new Cookie("userName", null);
            cookie1.setMaxAge(0);
            cookie2.setMaxAge(0);
            cookie1.setPath("/");
            cookie2.setPath("/");
            response.addCookie(cookie1);
            response.addCookie(cookie2);
            return ResultGenerator.genSuccessResult("success");
        } else {
            return ResultGenerator.genFailResult("error");
        }
    }

    /*
    @PostMapping("/profile/name")
    @ApiOperation("Profile name")
    public String nameUpdate(HttpServletRequest request, @RequestParam("loginUserName") String loginUserName,
                             @RequestParam("nickName") String nickName) {
        if (StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)) {
            return "参数不能为空";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updateName(loginUserId, loginUserName)) {
            return ServiceResultEnum.SUCCESS.getResult();
        } else {
            return "修改失败";
        }
    }
    */

    @GetMapping("/logout")
    @ApiOperation("Admin logout")
    @ResponseBody
    public Result logout(HttpServletResponse response) {
        Cookie cookie1 = new Cookie("userId", null);
        Cookie cookie2 = new Cookie("userName", null);
        cookie1.setMaxAge(0);
        cookie2.setMaxAge(0);
        cookie1.setPath("/");
        cookie2.setPath("/");
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/index")
    public String dd() {
        return "index";
    }

    @GetMapping({"/login", "login.html"})
    public String loginPage() {
        return "login";
    }
}
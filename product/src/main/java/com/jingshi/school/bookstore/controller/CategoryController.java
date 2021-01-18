/**
 * FileName: CategoryController
 * Author:   sky
 * Date:     2020/4/10 15:39
 * Description:
 */
package com.jingshi.school.bookstore.controller;

import com.jingshi.school.bookstore.model.entity.Category;
import com.jingshi.school.bookstore.model.entity.Product;
import com.jingshi.school.bookstore.service.CategoryService;
import com.jingshi.school.bookstore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 *
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
@Controller
@CrossOrigin
@RequestMapping("/product")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    // 一级分类获取
    @GetMapping("/category/get/{id}")
    public String dd(@PathVariable("id") Integer id, HttpServletRequest request) {
        List<Category> categories = categoryService.getCategoriesPage(2, id);
        if (categories.size() == 0) {
            return "error";
        }
        List<Product> products = productService.listPorductsByCategories(categories);
        request.setAttribute("category", categoryService.getGoodsCategoryById(id) );
        request.setAttribute("categoryId", id);
        request.setAttribute("categories", categories);
        request.setAttribute("products", products);
        return "category";
    }

    // 二级分类获取商品
    @GetMapping("/category/se/{id}")
    public String dc(@PathVariable("id") Integer id, HttpServletRequest request) {
        List<Product> products = productService.listPorductsByCategory(id);
        Category category = categoryService.getGoodsCategoryById(id);
        request.setAttribute("category",category );
        request.setAttribute("products", products);
        return "category";
    }

}
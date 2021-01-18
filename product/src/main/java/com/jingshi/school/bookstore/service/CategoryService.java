/**
 * FileName: CategoryService
 * Author:   sky
 * Date:     2020/4/10 15:38
 * Description:
 */
package com.jingshi.school.bookstore.service;

import com.jingshi.school.bookstore.model.vo.SearchPageCategoryVO;
import com.jingshi.school.bookstore.model.entity.Category;

import java.util.List;

/**
 *
 *
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
public interface CategoryService {

    /**
     * 后台分页
     * 于后台管理系统查询管理分类
     * 由上层传入page 参数
     * *
     * @param categoryLevel 商品类别登记
     * @param parentId 父类id
     * @return 一页包装数据
     */
    List<Category> getCategoriesPage(int categoryLevel, int parentId);

    /**
     * 保存商品分类信息
     *
     * @param goodsCategory 新增商品分类
     * @return 结果信息
     */
    String saveCategory(Category goodsCategory);

    /**
     * 更新商品分类信息
     *
     * @param goodsCategory 商品分类
     * @return 结果信息
     */
    String updateGoodsCategory(Category goodsCategory);

    /**
     * 以商品分类id获取商品分类信息
     *
     * @param id 商品分类id
     * @return 商品分类信息，若查不到则为空
     */
    Category getGoodsCategoryById(Integer id);

    /**
     * 批量删除商品分类
     * 若只删除一个也是通过该方法
     *
     * @param ids id数组
     * @return 结果
     */
    String deleteBatch(Integer[] ids);

    /**
     * 返回分类数据(首页调用)
     * 获取有层次的分类信息
     * 全部返回
     *
     * @return
     */
    List<Category> getCategoriesForIndex();

    /**
     * 获取同级分类列表
     * 返回分类数据(搜索页调用)
     * 分类搜索时，可以切换同级分类
     *
     * @param categoryId 分类id
     * @return 轮播图 视图模型
     */
    SearchPageCategoryVO getCategoriesForSearch(Integer categoryId);

    /**
     * 根据parentId和level获取分类列表
     *
     * @param parentIds 父类id
     * @param categoryLevel 商品等级
     * @return 列表数据
     */
    List<Category> selectByLevelAndParentIdsAndNumber(List<Integer> parentIds, int categoryLevel);

}
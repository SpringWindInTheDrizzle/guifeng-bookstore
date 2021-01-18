/**
 * FileName: ProductService
 * Author:   sky
 * Date:     2020/4/9 18:05
 * Description:
 */
package com.jingshi.school.bookstore.service;

import com.jingshi.school.bookstore.model.entity.Category;
import com.jingshi.school.bookstore.model.entity.Product;

import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
public interface ProductService {
    /**
     * 后台分页
     * 可以商品名称模糊查询
     * 不输入商品名称查询
     * 以商品状态查询
     * 创建时间多样化查询
     *
     * @param goodsName
     * @param goodsStatus
     * @param start
     * @param end
     * @return
     */
    List<Product> getMallGoodsPage(String goodsName, String goodsStatus, Date start, Date end);

    /**
     * 添加商品
     * goods的所有信息
     *
     * @param goods 商品信息
     * @return 结果信息
     */
    String saveMallGoods(Product goods);

    /**
     * 批量新增商品数据
     * 由于可能存在部分商品成功部分失败，则此处返回值无意义，则不返回任何值
     *
     * @param goodsList 商品列表
     * @return 无返回值
     */
    String batchSaveMallGoods(List<Product> goodsList);

    /**
     * 修改商品信息
     * 若商品id查询不到则false
     *
     * @param goods 商品信息
     * @return 结果信息
     */
    String updateMallGoods(Product goods);

    /**
     * 商品id获取商品详情
     *
     * @param id 商品id
     * @return 商品信息
     */
    Product getMallGoodsById(Integer id);

    /**
     * 批量修改销售状态(上架下架)
     * 若sellStatus 为 1 则改为上架
     * 为 0 改为下架
     *
     * @param ids 商品id集合
     * @param  sellStatus 商品要修改成的状态
     * @return 结果布尔类型
     */
    String batchUpdateSellStatus(Integer[] ids, int sellStatus);


    /**
     * 搜索商品
     *
     * @param categoryId 类别id
     * @param keyWord 关键字，商品名或简介
     * @param orderBy 排序方式
     * @return
     */
    List<Product> searchMallGoods(String categoryId, String keyWord, String orderBy);

//    GoodsInfo searchMallGoods(int current, int size, String keyword, String orderBy);

    /**
     *
     * @param categoryId
     * @return
     */
    List<Product> listPorductsByCategory(int categoryId);

    List<Product> listPorductsByCategories(List<Category> categories);

    List<Product> listProducts();

}
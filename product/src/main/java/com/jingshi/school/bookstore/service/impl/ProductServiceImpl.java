/**
 * FileName: ProductService
 * Author:   sky
 * Date:     2020/4/9 18:05
 * Description:
 */
package com.jingshi.school.bookstore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import com.jingshi.school.bookstore.dao.ProductMapper;
import com.jingshi.school.bookstore.model.entity.Category;
import com.jingshi.school.bookstore.model.entity.Product;
import com.jingshi.school.bookstore.service.ProductService;
import com.jingshi.school.bookstore.model.enums.ServiceResultEnum;
import com.mysql.cj.util.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
@DS("mysql")
@org.springframework.stereotype.Service
@Service(version="1.0.0")
public class ProductServiceImpl implements ProductService {

    Gson gson = new Gson();

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ProductMapper goodsMapper;

    @Override
    @DS("slave")
    public List<Product> getMallGoodsPage(String goodsName, String goodsStatus, Date start, Date end) {
        return goodsMapper.selectList(Wrappers.<Product>lambdaQuery()
                .eq(Product::getGoodsSellStatus, 1)
                .like(!StringUtils.isNullOrEmpty(goodsName), Product::getName, goodsName)
                .eq(!StringUtils.isNullOrEmpty(goodsStatus), Product::getGoodsSellStatus, goodsStatus)
                .gt(start != null, Product::getGmtCreate, start)
                .lt(end != null, Product::getGmtCreate, end));

    }

    @Override
    public String saveMallGoods(Product goods) {
        if (goodsMapper.insert(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String batchSaveMallGoods(List<Product> goodsList) {
        if (!CollectionUtils.isEmpty(goodsList)) {
            for (Product info : goodsList) {
                if (goodsMapper.insert(info) <= 0) {
                    return ServiceResultEnum.OPERATE_ERROR.getResult();
                }
            }
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.ERROR.getResult();
    }

    @Override
    public String updateMallGoods(Product goods) {
        Product temp = goodsMapper.selectById(goods.getId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        redisTemplate.delete("info:" + goods.getId());
        goods.setGmtModified(new Date());
        if (goodsMapper.updateById(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    @DS("slave")
    public Product getMallGoodsById(Integer id) {
        Object product = redisTemplate.opsForValue().get("info:" + id);
        if (product == null) {
            Product pp = goodsMapper.selectById(id);
            redisTemplate.opsForValue().set("info:" + id, gson.toJson(pp));
            return pp;
        } else {
            return gson.fromJson(product.toString(), Product.class);
        }
    }

    @Override
    public String batchUpdateSellStatus(Integer[] ids, int sellStatus) {
        for (int id : ids) {
            Product temp = goodsMapper.selectById(id);
            if (temp == null) {
                return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
            }
            redisTemplate.delete("info:" + id);
            temp.setGoodsSellStatus((byte)sellStatus);
            if (goodsMapper.updateById(temp) <= 0) {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.SUCCESS.getResult();
    }

    /**
     * orderBy 为空则 默认排序
     * 为 priceUp 价格小到大排序
     * 为 priceDown 价格从大到小排序
     * 为 new 发布时间倒序
     * 默认按照库存数量从大到小排列
     * <p>
     * controller 解决商品信息太长问题
     *
     * @param keyWord
     * @param orderBy
     * @return
     */
    @Override
    @DS("slave")
    public List<Product> searchMallGoods(String categoryId, String keyWord, String orderBy) {

        /**
        if (orderBy.equals(GoodsOrderByEnum.NEW)) {
            return longStringDeal(goodsMapper.selectList(Wrappers.<Product>lambdaQuery()
                    .like(!StringUtils.isNullOrEmpty(keyWord), Product::getName, keyWord)
                    .or().like(!StringUtils.isNullOrEmpty(keyWord), Product::getSubtitle, keyWord)
                    .eq(categoryId != null, Product::getCategoryId, categoryId)
                    .orderByAsc(Product::getId)));
        } else if (orderBy.equals(GoodsOrderByEnum.PRICK_UP)) {
            return longStringDeal(goodsMapper.selectList(Wrappers.<Product>lambdaQuery()
                    .like(!StringUtils.isNullOrEmpty(keyWord), Product::getName, keyWord)
                    .or().like(!StringUtils.isNullOrEmpty(keyWord), Product::getSubtitle, keyWord)
                    .eq(categoryId != null, Product::getCategoryId, categoryId)
                    .orderByAsc(Product::getSellingPrice)));
        } else if (orderBy.equals(GoodsOrderByEnum.PRICK_DOWN)) {
            return longStringDeal(goodsMapper.selectList(Wrappers.<Product>lambdaQuery()
                    .like(!StringUtils.isNullOrEmpty(keyWord), Product::getName, keyWord)
                    .or().like(!StringUtils.isNullOrEmpty(keyWord), Product::getSubtitle, keyWord)
                    .eq(categoryId != null, Product::getCategoryId, categoryId)
                    .orderByDesc(Product::getSellingPrice)));
        } else {
            // GoodsOrderByEnum.STOCK
            return longStringDeal(goodsMapper.selectList(Wrappers.<Product>lambdaQuery()
                    .like(!StringUtils.isNullOrEmpty(keyWord), Product::getName, keyWord)
                    .or().like(!StringUtils.isNullOrEmpty(keyWord), Product::getSubtitle, keyWord)
                    .eq(categoryId != null, Product::getCategoryId, categoryId)
                    .orderByDesc(Product::getStockNum)));
        }
         */
        return longStringDeal(goodsMapper.selectList(Wrappers.<Product>lambdaQuery()
                .eq(Product::getGoodsSellStatus, 1)
                .like(!StringUtils.isNullOrEmpty(keyWord), Product::getName, keyWord)
                .or().like(!StringUtils.isNullOrEmpty(keyWord), Product::getSubtitle, keyWord)
                .orderByAsc(Product::getId)));
    }

    @Override
    @DS("slave")
    public List<Product> listPorductsByCategory(int categoryId) {
        return goodsMapper.selectList(Wrappers.<Product>lambdaQuery().eq(Product::getGoodsSellStatus, 1).eq(Product::getCategoryId, categoryId));
    }

    @DS("slave")
    @Override
    public List<Product> listPorductsByCategories(List<Category> categories) {
        List<Integer> categoriesId = new ArrayList<>();
        for (Category category : categories) {
            categoriesId.add(category.getId());
        }
        return goodsMapper.selectList(Wrappers.<Product>lambdaQuery().eq(Product::getGoodsSellStatus, 1).in(Product::getCategoryId, categoriesId));
    }

    @Override
    @DS("slave")
    public List<Product> listProducts() {
        return goodsMapper.selectList(Wrappers.<Product>lambdaQuery());
    }

    @DS("slave")
    @Override
    public Product getProductByName(String name, String author) {
        return goodsMapper.selectOne(Wrappers.<Product>lambdaQuery().eq(Product::getAuthor, author).eq(Product::getName, name));
    }

    /**
     * 设置商品名称 28
     * 设置商品简介 30
     *
     * @param list
     * @return
     */
    private List<Product> longStringDeal(List<Product> list) {
        for (Product goodInfo : list) {
            String goodsName = goodInfo.getName();
            String goodsIntro = goodInfo.getSubtitle();
            // 字符串过长导致文字超出的问题
            if (goodsName.length() > 28) {
                goodsName = goodsName.substring(0, 28) + "...";
                goodInfo.setName(goodsName);
            }
            if (goodsIntro.length() > 30) {
                goodsIntro = goodsIntro.substring(0, 30) + "...";
                goodInfo.setSubtitle(goodsIntro);
            }
        }
        return list;
    }

}
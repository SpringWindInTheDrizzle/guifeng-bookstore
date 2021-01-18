/**
 * FileName: CategoryServiceImpl
 * Author:   sky
 * Date:     2020/4/10 15:39
 * Description:
 */
package com.jingshi.school.bookstore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jingshi.school.bookstore.dao.CategoryMapper;
import com.jingshi.school.bookstore.model.entity.Category;
import com.jingshi.school.bookstore.model.enums.ServiceResultEnum;
import com.jingshi.school.bookstore.model.vo.SearchPageCategoryVO;
import com.jingshi.school.bookstore.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
@DS("mysql")
@Service
@org.apache.dubbo.config.annotation.Service(version="1.0.0")
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper goodsCategoryMapper;

    @DS("slave")
    @Override
    public List<Category> getCategoriesPage(int categoryLevel, int parentId) {
        return goodsCategoryMapper.selectList(Wrappers.<Category>lambdaQuery()
                .eq(Category::getLevel, categoryLevel).eq(Category::getParentId, parentId)
                .eq(Category::getIsDeleted, false));
    }

    /**
     * 以分类等级和名称
     *
     * @param goodsCategory
     * @return
     */
    @Override
    public String saveCategory(Category goodsCategory) {
        Category temp = goodsCategoryMapper.selectOne(Wrappers.<Category>lambdaQuery()
                .eq(Category::getLevel, goodsCategory.getLevel())
                .eq(Category::getName, goodsCategory.getName()));
        if (temp != null) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        if (goodsCategoryMapper.insert(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateGoodsCategory(Category goodsCategory) {
        Category temp = goodsCategoryMapper.selectById(goodsCategory.getId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        Category temp2 = goodsCategoryMapper.selectOne(Wrappers.<Category>lambdaQuery()
                .eq(Category::getLevel, goodsCategory.getLevel())
                .eq(Category::getName, goodsCategory.getName()));
        if (temp2 != null && !temp2.getId().equals(goodsCategory.getId())) {
            //同名且不同id 不能继续修改
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        goodsCategory.setGmtModified(new Date());
        if (goodsCategoryMapper.updateById(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    @DS("slave")
    public Category getGoodsCategoryById(Integer id) {
        return goodsCategoryMapper.selectById(id);
    }

    @Override
    public String deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return ServiceResultEnum.ERROR.getResult();
        }
        //删除分类数据
        if (goodsCategoryMapper.deleteBatchIds(Arrays.asList(ids)) == ids.length) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.OPERATE_ERROR.getResult();
    }

    @Override
    @DS("slave")
    public List<Category> getCategoriesForIndex() {
//        List<Category> indexCategoryVOS = new ArrayList<>();
//        //获取一级分类的固定数量的数据
//        // 获取一级分类的所有数据
//        List<Category> firstLevelCategories = goodsCategoryMapper.selectList(Wrappers.<Category>lambdaQuery()
//                .in(Category::getParentId, Collections.singletonList(0))
//                .eq(Category::getLevel, CategoryLevelEnum.LEVEL_ONE.getLevel())
//                .eq(Category::getIsDeleted, false).orderByDesc(Category::getSortOrder));
//
//        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
//            List<Integer> firstLevelCategoryIds = firstLevelCategories.stream().map(Category::getId).collect(Collectors.toList());
//            //获取二级分类的数据
//            List<Category> secondLevelCategories = goodsCategoryMapper.selectList(Wrappers.<Category>lambdaQuery()
//                    .in(Category::getParentId, firstLevelCategoryIds)
//                    .eq(Category::getCategoryLevel, CategoryLevelEnum.LEVEL_TWO.getLevel())
//                    .eq(Category::getDeleted, false).orderByDesc(Category::getCategoryRank));
//            //处理一级分类
//            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
//                //根据 parentId 将 thirdLevelCategories 分组
//                Map<Integer, List<Category>> secondLevelCategoryVOMap =
//                        secondLevelCategories.stream().collect(groupingBy(Category::getParentId));
//                for (Category firstCategory : firstLevelCategories) {
//                    Category mallIndexCategoryVO = new Category();
//                    BeanUtil.copyProperties(firstCategory, mallIndexCategoryVO);
//                    //如果该一级分类下有数据则放入 newBeeMallIndexCategoryVOS 对象中
//                    if (secondLevelCategoryVOMap.containsKey(firstCategory.getId())) {
//                        //根据一级分类的id取出secondLevelCategoryVOMap分组中的二级级分类list
//                        List<Category> tempGoodsCategories = secondLevelCategoryVOMap.get(firstCategory.getId());
//                        mallIndexCategoryVO.setSecondLevelCategoryVOS((BeanUtil.copyList(tempGoodsCategories, SecondLevelCategoryVO.class)));
//                        indexCategoryVOS.add(mallIndexCategoryVO);
//                    }
//                }
//            }
//            return indexCategoryVOS;
//        } else {
//            return null;
//        }
        return null;
        // TODO: 2020/4/10
    }

    @Override
    @DS("slave")
    public SearchPageCategoryVO getCategoriesForSearch(Integer categoryId) {
        SearchPageCategoryVO searchPageCategoryVO = new SearchPageCategoryVO();
        Category secondLevelGoodsCategory = goodsCategoryMapper.selectById(categoryId);
        if (secondLevelGoodsCategory != null && secondLevelGoodsCategory.getLevel() == 2) {
            //获取当前二级分类的一级分类
            Category firstLevelGoodsCategory = goodsCategoryMapper.selectById(secondLevelGoodsCategory.getParentId());
            if (firstLevelGoodsCategory != null && firstLevelGoodsCategory.getLevel() == 1) {
                //获取当前一级分类下的二级分类List
                List<Category> thirdLevelCategories = goodsCategoryMapper.selectList(Wrappers.<Category>lambdaQuery()
                        .in(Category::getParentId, Collections.singletonList(firstLevelGoodsCategory.getId()))
                        .eq(Category::getLevel, 3));
                searchPageCategoryVO.setCurrentCategoryName(secondLevelGoodsCategory.getName());
                searchPageCategoryVO.setSecondLevelCategoryName(firstLevelGoodsCategory.getName());
                searchPageCategoryVO.setThirdLevelCategoryList(thirdLevelCategories);
                return searchPageCategoryVO;
            }
        }
        return null;
    }

    @Override
    @DS("slave")
    public List<Category> selectByLevelAndParentIdsAndNumber(List<Integer> parentIds, int categoryLevel) {
        return goodsCategoryMapper.selectList(Wrappers.<Category>lambdaQuery().in(Category::getParentId, parentIds)
                .eq(Category::getLevel, categoryLevel));
    }
}
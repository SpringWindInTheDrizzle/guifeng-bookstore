package com.jingshi.school.bookstore.model.vo;

import com.jingshi.school.bookstore.model.entity.Category;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索页面分类数据VO
 * @author 14520
 */
@Data
@ToString
public class SearchPageCategoryVO implements Serializable {

    private String firstLevelCategoryName;

    private List<Category> secondLevelCategoryList;

    private String secondLevelCategoryName;

    private String currentCategoryName;

    public void setThirdLevelCategoryList(List<Category> thirdLevelCategories) {
    }
}

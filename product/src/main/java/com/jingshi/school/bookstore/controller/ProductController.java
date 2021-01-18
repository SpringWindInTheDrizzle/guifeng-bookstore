/**
 * FileName: ProductController
 * Author:   sky
 * Date:     2020/4/9 18:21
 * Description:
 */
package com.jingshi.school.bookstore.controller;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jingshi.school.bookstore.commom.Constants;
import com.jingshi.school.bookstore.dao.ProductMapper;
import com.jingshi.school.bookstore.model.entity.Category;
import com.jingshi.school.bookstore.model.entity.Product;
import com.jingshi.school.bookstore.model.enums.ServiceResultEnum;
import com.jingshi.school.bookstore.model.vo.GoodVO;
import com.jingshi.school.bookstore.model.vo.ProductAdminVO;
import com.jingshi.school.bookstore.server.MQService;
import com.jingshi.school.bookstore.service.CategoryService;
import com.jingshi.school.bookstore.service.ProductDetailService;
import com.jingshi.school.bookstore.service.ProductService;
import com.jingshi.school.bookstore.model.entity.ProductDetail;
import com.jingshi.school.bookstore.util.FTPFileUploadUtils;
import com.jingshi.school.bookstore.util.Result;
import com.jingshi.school.bookstore.util.ResultGenerator;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
@Slf4j
@Controller
@RequestMapping("/product")
public class ProductController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private MQService mqService;

    @Resource
    private ProductDetailService productDetailService;

    @Resource
    private ProductService goodsService;

    @Resource
    private CategoryService categoryService;

    private String[] categoriesName = new String[]{"绘本", "儿童科普", "幼儿启蒙", "卡通动漫", "手工/游戏", "智力开发", "少儿英语",
            "中小学教辅", "考试", "外语学习", "大中专教材", "字典词典", "小说", "文学", "传记", "青春文学", "历史", "哲学/宗教", "心理学", "政治/军事",
            "国学/古籍", "法律", "文化", "社会科学", "IT计算机", "科普读物", "医学", "科学与自然", "电子与通信", "工业技术", "建筑", "管理", "励志成功", "经济", "金融投资",
            "书法", "艺术", "绘画", "音乐", "摄影", "育儿家教", "健身保健", "烹饪/美食", "孕产/胎教", "娱乐/休闲", "家居", "旅游/地图", "婚恋与两性", "育儿家教"};

    @GetMapping({"/search", "/search.html"})
    public String searchPage(@RequestParam("keyWord") String keyWord, HttpServletRequest request) {
        List<Product> info = goodsService.searchMallGoods(null, keyWord, null);
        List<GoodVO> list = new ArrayList<>();
        for (Product product : info) {
            list.add(new GoodVO(product));
        }
        request.setAttribute("products", list);
        return "products";
    }

    @GetMapping("/goods/edit")
    @ApiOperation("Edit goods")
    @ResponseBody
    public Result edit() {
        //查询所有的一级分类
        List<Category> firstLevelCategories = categoryService
                .selectByLevelAndParentIdsAndNumber(Collections.singletonList(0), 1);
        Map<String, List<Category>> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<Category> secondLevelCategories = categoryService
                    .selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).
                            getId()), 2);
            map.put("firstLevelCategories", firstLevelCategories);
            map.put("secondLevelCategories", secondLevelCategories);
        }
        Result result = ResultGenerator.genSuccessResult("success");
        result.setData(map);
        return result;
    }


    @GetMapping("/goods/edit/{goodsId}")
    @ApiOperation("Edit good by good id")
    @ResponseBody
    public Result edit(@PathVariable("goodsId") Integer goodsId) {
        Product mallGoods = goodsService.getMallGoodsById(goodsId);
        if (mallGoods == null) {
            return ResultGenerator.genFailResult("error");
        }
        Map<String, Object> map = new HashMap<>();
        if (mallGoods.getCategoryId() > 0) {
            if (mallGoods.getCategoryId() != null || mallGoods.getCategoryId() > 0) {
                //有分类字段则查询相关分类数据返回给前端以供分类的二级联动显示
                Category currentGoodsCategory = categoryService.getGoodsCategoryById(mallGoods.getCategoryId());
                //商品表中存储的分类id字段为二级分类的id，不为二级分类则是错误数据
                if (currentGoodsCategory != null && currentGoodsCategory.getLevel() == 2) {
                    //查询所有的一级分类
                    List<Category> firstLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0), 1);
                    Category secondCategory = categoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<Category> secondLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), 2);
                        //查询当前二级分类的父级一级分类
                        Category firestCategory = categoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firestCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            map.put("firstLevelCategories", firstLevelCategories);
                            map.put("secondLevelCategories", secondLevelCategories);
                            map.put("firestCategoryId", firestCategory.getId());
                            map.put("secondLevelCategoryId", secondCategory.getId());
                            map.put("thirdLevelCategoryId", currentGoodsCategory.getId());
                        }
                    }
                }
            }
        }
        if (mallGoods.getCategoryId() == 0) {
            //查询所有的一级分类
            List<Category> firstLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0), 1);
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<Category> secondLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getId()), 2);
                map.put("firstLevelCategories", firstLevelCategories);
                map.put("secondLevelCategories", secondLevelCategories);
            }
        }
        map.put("goods", mallGoods);
        Result result = ResultGenerator.genSuccessResult("success");
        result.setData(map);
        return result;
    }

    /**
     * 列表
     */
    @GetMapping("/goods/list")
    @ApiOperation("List all goods")
    @ResponseBody
    public Result list() {
        return ResultGenerator.genSuccessResult(goodsService.getMallGoodsPage(
                null, null, null, null));
    }

    /**
     * 添加 ,不完整
     */
    @PostMapping("/goods/save")
    @ApiOperation("Save goods")
    @ResponseBody
    public Result save(@RequestBody Product goodInfo) {
        if (StringUtils.isEmpty(goodInfo.getName())
                || StringUtils.isEmpty(goodInfo.getSubtitle())
                || StringUtils.isEmpty(goodInfo.getAuthor())
                || Objects.isNull(goodInfo.getOriginalPrice())
                || Objects.isNull(goodInfo.getCategoryId())
                || Objects.isNull(goodInfo.getSellingPrice())
                || Objects.isNull(goodInfo.getStockNum())
                || Objects.isNull(goodInfo.getGoodsSellStatus())
                || StringUtils.isEmpty(goodInfo.getMainImage())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = goodsService.saveMallGoods(goodInfo);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 修改
     */
    @PostMapping("/goods/update")
    @ApiOperation("Update a goods info")
    @ResponseBody
    public Result update(@RequestBody Product goodInfo) {
        if (Objects.isNull(goodInfo.getId())
                || StringUtils.isEmpty(goodInfo.getSubtitle())
                || StringUtils.isEmpty(goodInfo.getAuthor())
                || Objects.isNull(goodInfo.getOriginalPrice())
                || Objects.isNull(goodInfo.getCategoryId())
                || Objects.isNull(goodInfo.getSellingPrice())
                || Objects.isNull(goodInfo.getStockNum())
                || Objects.isNull(goodInfo.getGoodsSellStatus())
                || StringUtils.isEmpty(goodInfo.getMainImage())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = goodsService.updateMallGoods(goodInfo);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/goods/info/{id}")
    @ApiOperation("Get a goods by id")
    public String info(@PathVariable("id") Integer id, HttpServletRequest request) {
        Product goods = goodsService.getMallGoodsById(id);
        if (goods == null) {
            return "/error";
        }
        request.setAttribute("good", new GoodVO(goods));
        return "detail";
    }

    /**
     * 批量修改销售状态
     */
    @PutMapping("/goods/status/{sellStatus}")
    @ApiOperation("Update goods's status")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (goodsService.batchUpdateSellStatus(ids, sellStatus).equals(ServiceResultEnum.SUCCESS.getResult())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    @GetMapping("/admin/manage")
    public String adminManage(HttpServletRequest request) {
        List<Product> products = goodsService.listProducts();
        List<ProductAdminVO> list = new ArrayList<>();
        for (Product product : products) {
            ProductAdminVO vo = new ProductAdminVO();
            vo.setId(product.getId());
            vo.setAuthor(product.getAuthor());
            vo.setCategoryName(categoriesName[product.getCategoryId() - 10012]);
            vo.setGoodsSellStatus(product.getGoodsSellStatus());
            vo.setName(product.getName());
            vo.setOriginalPrice(product.getOriginalPrice());
            vo.setSellingPrice(product.getSellingPrice());
            vo.setStockNum(product.getStockNum());
            list.add(vo);
        }
        request.setAttribute("products", list);
        return "manage";
    }

    @PostMapping("/admin/state")
    @ResponseBody
    public Result updateState(@RequestParam("productId") String id) {
        Product product = goodsService.getMallGoodsById(Integer.valueOf(id));
        if (product == null) {
            return ResultGenerator.genFailResult("无该图书");
        } else {
            int b = product.getGoodsSellStatus();
            b = 1 - b;
            redisTemplate.delete("info:"+id);
            product.setGoodsSellStatus((byte) b);
            goodsService.updateMallGoods(product);
            return ResultGenerator.genSuccessResult();
        }
    }

    @GetMapping("/admin/update/{id}")
    public String edit(@PathVariable("id") Integer id, HttpServletRequest request) {
        Product product = goodsService.getMallGoodsById(id);
        if (product == null) {
            return "error";
        }
        request.setAttribute("product", new GoodVO(product));
        return "editProduct";
    }

    @PostMapping("/admin/update")
    @ResponseBody
    public Result updateProduct(@RequestParam("id") Integer id,
                                @RequestParam("price") Double price,
                                @RequestParam("stockNum") Integer num,
                                @RequestParam("subtitle") String subtitle) {
        Product product = goodsService.getMallGoodsById(id);
        if (num != null) {
            product.setStockNum(num);
        }
        if (subtitle != null) {
            product.setSubtitle(subtitle);
        }
        if (price != null) {
            product.setSellingPrice(price);
        }
        goodsService.updateMallGoods(product);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/admin/insert")
    @ResponseBody
    public Result insertProduct(@RequestParam("name") String name,
                                @RequestParam("sub") String sub,
                                @RequestParam("category") Integer category,
                                @RequestParam("author") String author,
                                @RequestParam("press") String press,
                                @RequestParam("ISBN") String isbn,
                                @RequestParam("time") String time,
                                @RequestParam("price1") Double price1,
                                @RequestParam("price2") Double price2,
                                @RequestParam("stockNum") Integer num,
                                @RequestParam("image") String image) {
        Gson gson1 = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
        Date bb = gson1.fromJson("\"" + time + "\"", Date.class);
        Product product = new Product();
        product.setName(name);
        product.setSubtitle(sub);
        if (category < 10012 || category > 10060) {
            return ResultGenerator.genFailResult("分类编号不正确，请重新输入");
        }
        if (goodsService.getProductByName(name, author) != null) {
            return ResultGenerator.genFailResult("已存在同书名同作者的书籍");
        }
        product.setCategoryId(category);
        product.setAuthor(author);
        product.setPress(press);
        product.setIsbn(isbn);
        product.setPublicationTime(bb);
        product.setOriginalPrice(price1);
        product.setSellingPrice(price2);
        product.setStockNum(num);
        product.setMainImage(image.substring(12));
        goodsService.saveMallGoods(product);
        Product product1 = goodsService.getProductByName(name, author);
        mqService.fanout(String.valueOf(product1.getId()));
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/admin/insert")
    public String getInsertPage() {
        return "submitProduct";
    }

    @PostMapping("/trans")
    @ResponseBody
    public Result upload(@RequestParam("file") MultipartFile uploadFile) {
        if (null == uploadFile) {
            return ResultGenerator.genFailResult("上传失败，无法找到文件！");
        }
        // BMP、JPG、JPEG、PNG、GIF
        String fileName = uploadFile.getOriginalFilename();
        if (!fileName.endsWith(".bmp") && !fileName.endsWith(".jpg")
                && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png")
                && !fileName.endsWith(".gif")) {
            return ResultGenerator.genFailResult("上传失败，请选择BMP、JPG、JPEG、PNG、GIF文件！");
        }
        try {
            InputStream inputStream = uploadFile.getInputStream();
            //调用ftp上传文件工具类，返回true成功，返回false失败
            Boolean flag = FTPFileUploadUtils.uploadFile(fileName,inputStream);
            if(flag == true){
                //这里按功能需求做相应处理即可
                return ResultGenerator.genSuccessResult("上传成功");
            }
            return ResultGenerator.genSuccessResult("上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("上传异常！");
        }
    }

    @PostMapping("/name")
    @ResponseBody
    public Result getName(@RequestParam("id") String id) {
        Product product = goodsService.getMallGoodsById(Integer.valueOf(id));
        if (product == null) {
            return ResultGenerator.genFailResult("无该图书");
        }
        Result result = new Result();
        result.setResultCode(200);
        result.setMessage("SUCCESS");
        result.setData(product.getName());
        return result;
    }

}
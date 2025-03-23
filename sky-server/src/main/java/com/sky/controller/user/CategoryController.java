package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: CategoryController
 * Package: com.sky.controller.user
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/3/23 13:40
 * @Version 1.0
 */
@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "用户分类接口")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 用户查询分类接口
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "用户查询分类接口")
    public Result<List<Category>> list(Integer type){
        List<Category> categories = categoryService.getByType(type);
        return Result.success(categories);
    }
}

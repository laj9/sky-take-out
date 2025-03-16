package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: CategoryController
 * Package: com.sky.controller.admin
 * Description: 分类管理
 *
 * @Author Aijing Liu
 * @Create 2025/3/15 15:10
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类管理相关接口")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "启用禁用分类")
    public Result startOrStop(@PathVariable int status, Long id){
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增分类")
    public Result<Category> save (@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除分类")
    public Result delete(Long id){
        log.info("删除分类：{}", id);
        categoryService.delete(id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return 类型可以对应对各查询结果，需要使用list包装
     */
    @GetMapping("/list")
    @ApiOperation(value = "根据类型查询分类")
    public Result<List<Category>> getByType(int type){
        List<Category> list = categoryService.getByType(type);
        return Result.success(list);
    }
}

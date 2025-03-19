package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: DishController
 * Package: com.sky.controller.admin
 * Description: 菜品管理
 *
 * @Author Aijing Liu
 * @Create 2025/3/17 17:11
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {

    @Autowired
    DishService dishService;

    /**
     * 添加菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("添加菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("开始进行菜品管理：{}", dishDTO);
        dishService.savewithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * @param dishPageQueryDTO
     * @return 不需要加RequestBody因为是直接请求用？，而不是使用json格式
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("开始分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        dishService.deleteById(ids);
        return Result.success();
    }
}

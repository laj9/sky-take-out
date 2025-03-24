package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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

    @Autowired
    RedisTemplate redisTemplate;

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
        //构造key，删除被修改的分类的缓存
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);

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
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("开始删除菜品：{}", ids);
        dishService.deleteById(ids);
        //删除所有缓存菜品
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status, id);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(@PathVariable Long id) {
        log.info("开始根据id查询菜品：{}", id);
        List<Dish> list = dishService.list(id);
        return Result.success(list);
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("开始修改菜品：{}",dishDTO);
        dishService.update(dishDTO);
        //删除所有缓存菜品
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 清理缓存中的菜品数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}

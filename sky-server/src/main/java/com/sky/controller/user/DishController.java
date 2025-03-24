package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.element.TypeElement;
import java.util.List;

/**
 * ClassName: DishController
 * Package: com.sky.controller.user
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/3/23 13:47
 * @Version 1.0
 */
@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "C端-菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation(value = "根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        //构造Redis中的key
        String key = "dish_" + categoryId;
        //查询Redis中是否有该缓存，若有，直接返回
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (list != null && list.size() > 0) {
            return Result.success(list);
        }
        //若没有，进入SQL查询，并将其保存在缓存中
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        list = dishService.listWithFlavor(dish);

        redisTemplate.opsForValue().set(key, list);
        return Result.success(list);
    }
}

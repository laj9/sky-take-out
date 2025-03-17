package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: DishServiceImpl
 * Package: com.sky.service.impl
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/3/17 17:19
 * @Version 1.0
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和对应口味
     *
     * @param dishDTO
     */
    public void savewithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //插入一条菜品数据
        dishMapper.save(dish);
        //获取dishId
        Long id = dish.getId();
        //插入多条风味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}

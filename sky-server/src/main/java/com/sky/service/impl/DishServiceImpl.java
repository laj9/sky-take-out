package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    SetmealDishMapper setmealDishMapper;

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

    /**
     * 分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> dishVOPage = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(dishVOPage.getTotal(), dishVOPage.getResult());
    }

    /**
     * 删除菜品
     *
     * @param ids
     */
    @Transactional
    public void deleteById(List<Long> ids) {
        //检查菜品状态是否为禁用
        for (Long id : ids) {
            Dish dish = dishMapper.getBydish(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //检查菜品是否与套餐关联
        List<Long> setmealIds = setmealDishMapper.getBydishId(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品
//        for (Long id : ids) {
//            dishMapper.delete(id);
//            //删除菜品关联的口味数据
//            dishFlavorMapper.delete(id);
//        }
        //批量删除菜品和口味数据
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByIds(ids);
    }

    /**
     * 根据id返回菜品
     *
     * @param id
     * @return
     */
    public DishVO getById(Long id) {
        //根据id从dish表中获得数据
        Dish dish = dishMapper.getBydish(id);
        //根据id从flavor表中获得口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getById(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     */
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //修改菜品基础信息
        dishMapper.update(dish);

        //修改菜品口味信息：删除原本信息+加上新的
        dishFlavorMapper.delete(dishDTO.getId());

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}

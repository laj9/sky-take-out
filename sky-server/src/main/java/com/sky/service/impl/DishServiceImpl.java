package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
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

import java.util.ArrayList;
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
    @Autowired
    SetmealMapper setmealMapper;

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

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<DishVO> dishVOS = new ArrayList<>();
        List<Dish> dishList = dishMapper.list(dish);

        for(Dish d:dishList){
            //将dish中的属性拷贝给dishVO
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);
            //获取flavour list
            List<DishFlavor> flavorList = dishFlavorMapper.getById(d.getId());
            dishVO.setFlavors(flavorList);
            //将其添加入list
            dishVOS.add(dishVO);
        }
        return dishVOS;
    }

    /**
     * 菜品起售/停售
     *
     * @param status
     * @param id
     */
    @Transactional
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        if (status == StatusConstant.DISABLE) {
            // 如果是停售操作，还需要将包含当前菜品的套餐也停售
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            // select setmeal_id from setmeal_dish where dish_id in (?,?,?)
            List<Long> setmealIds = setmealDishMapper.getBydishId(dishIds);
            if (setmealIds != null && setmealIds.size() > 0) {
                for (Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }
}

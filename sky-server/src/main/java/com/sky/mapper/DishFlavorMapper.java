package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: dishFlavorMapper
 * Package: com.sky.mapper
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/3/17 19:57
 * @Version 1.0
 */
@Mapper
public interface DishFlavorMapper {
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除口味
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void delete(Long id);

    /**
     * 菜品口味批量删除
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据dishId获得口味数据
     * @param id
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getById(Long id);
}

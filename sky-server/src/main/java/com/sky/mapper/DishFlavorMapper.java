package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

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
}

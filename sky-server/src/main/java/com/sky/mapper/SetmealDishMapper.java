package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * ClassName: SetmealDishMapper
 * Package: com.sky.mapper
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/3/16 12:19
 * @Version 1.0
 */
@Mapper
public interface SetmealDishMapper {

    List<Long> getBydishId(List<Long> dishIds);
}

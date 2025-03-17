package com.sky.service;

import com.sky.dto.DishDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: DishService
 * Package: com.sky.service
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/3/17 17:15
 * @Version 1.0
 */

public interface DishService {
    /**
     * 新增菜品和对应口味
     * @param dishDTO
     */
    void savewithFlavor(DishDTO dishDTO);
}

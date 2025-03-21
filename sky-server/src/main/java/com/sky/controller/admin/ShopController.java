package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: ShopController
 * Package: com.sky.controller.admin
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/3/21 21:43
 * @Version 1.0
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(value = "店铺相关接口")
@Slf4j
public class ShopController {

    private static final String key = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺状态
     *
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation(value = "设置店铺状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("开始设置店铺状态：{}", status == 1 ? "营业中" : "已打烊");
        redisTemplate.opsForValue().set(key, status);
        return Result.success();
    }

    /**
     * 获取店铺状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation(value = "获取店铺状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(key);
        log.info("得到店铺状态：{}", status == 1 ? "营业中" : "已打烊");
        return Result.success(status);
    }
}

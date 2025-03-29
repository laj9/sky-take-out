package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

/**
 * ClassName: AddressBookService
 * Package: com.sky.service
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/3/25 15:45
 * @Version 1.0
 */
public interface AddressBookService {

    /**
     * 新增地址
     * @param addressBook
     */
    void add(AddressBook addressBook);

    /**
     * 查询登录用户所有地址
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     *根据id修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    void delete(Long id);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 设置默认地址
     * @param addressBook
     */
    void setDefault(AddressBook addressBook);
}

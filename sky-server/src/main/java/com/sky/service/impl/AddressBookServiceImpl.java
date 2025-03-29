package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: AddressBookServiceImpl
 * Package: com.sky.service.impl
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/3/25 15:45
 * @Version 1.0
 */
@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     *
     * @param addressBook
     */
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
//        try {
            addressBookMapper.add(addressBook);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 查询登录用户所有地址
     *
     * @return
     */
    public List<AddressBook> list(AddressBook addressBook) {
        List<AddressBook> list = addressBookMapper.list(addressBook);
        return list;
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     */
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id删除地址
     *
     * @param id
     */
    public void delete(Long id) {
        addressBookMapper.delete(id);
    }

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    public AddressBook getById(Long id) {
        AddressBook addressBook = addressBookMapper.getById(id);
        return addressBook;
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     */
    public void setDefault(AddressBook addressBook) {
        //1. 将该用户的所有地址设置为非默认
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);

        //2. 将传入的地址设为默认
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }
}

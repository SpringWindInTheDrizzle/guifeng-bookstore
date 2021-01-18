/**
 * FileName: AddressService
 * Author:   sky
 * Date:     2020/4/9 13:55
 * Description:
 */
package com.jingshi.school.bookstore.service;



import com.jingshi.school.bookstore.model.entity.Address;

import java.util.List;

/**
 *
 *
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
public interface AddressService {
    /**
     * 用户保存自己的地址
     *
     * @param address
     * @return
     */
    String saveAddress(Address address);

    /**
     *用户删除地址
     *
     * @param id
     * @return
     */
    String deleteAddress(Integer id);

    /**
     * 用户更新地址
     *
     * @param address
     * @return
     */
    String updateAddress(Address address);

    /**
     *用户获取地址
     *
     * @param userId
     * @return
     */
    List<Address> getAddress(Integer userId);

    /**
     * 由地址id获取地址简写
     * @param id
     * @return
     */
    String getAddressById(Integer id);
}
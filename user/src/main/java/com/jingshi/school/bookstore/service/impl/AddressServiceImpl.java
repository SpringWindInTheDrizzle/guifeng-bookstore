/**
 * FileName: AddressServiceImpl
 * Author:   sky
 * Date:     2020/4/9 13:56
 * Description:
 */
package com.jingshi.school.bookstore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jingshi.school.bookstore.dao.AddressMapper;
import com.jingshi.school.bookstore.model.entity.Address;
import com.jingshi.school.bookstore.model.enums.ServiceResultEnum;
import com.jingshi.school.bookstore.service.AddressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
@DS("mysql")
@Service
@org.apache.dubbo.config.annotation.Service(version="1.0.0")
public class AddressServiceImpl implements AddressService {
    @Resource
    AddressMapper addressMapper;

    @Override
    public String saveAddress(Address address) {
        if (addressMapper.selectOne(Wrappers.<Address>lambdaQuery().eq(Address::getUserId, address.getUserId())) == null) {
            if (addressMapper.insert(address) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        } else {
            if (addressMapper.update(address, Wrappers.<Address>lambdaQuery().eq(Address::getUserId, address.getUserId())) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
    }

    @Override
    public String deleteAddress(Integer id) {

        if (addressMapper.deleteById(id) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String updateAddress(Address address) {
        Address temp = addressMapper.selectById(address.getId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setAddress(address.getAddress());
        temp.setReceiverName(address.getReceiverName());
        temp.setReceiverPhone(address.getReceiverPhone());
        temp.setGmtModified(new Date());
        if (addressMapper.updateById(temp) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @DS("slave")
    @Override
    public List<Address> getAddress(Integer id) {
        return addressMapper.selectList(Wrappers.<Address>lambdaQuery().eq(Address::getUserId, id));
    }

    @DS("slave")
    @Override
    public String getAddressById(Integer id) {
        Address address = addressMapper.selectOne(Wrappers.<Address>lambdaQuery()
                .eq(Address::getId, id));
        String addr = address.getReceiverProvince()+ "/"+ address.getReceiverCity() + "/" + address.getAddress() + "    " + address.getReceiverName() + "/" + address.getReceiverPhone();
        return addr;
    }
}
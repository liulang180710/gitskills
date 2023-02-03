package org.my.springcloud.producer.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.my.springcloud.base.bean.HumanInfo;
import org.my.springcloud.base.bean.ResultInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface HumanInfoMapper {
    HumanInfo getHumanInfo(Integer humanID);

    List<HumanInfo> findAllHuman();

    HumanInfo validPassword(@Param("userName") String userName, @Param("password") String password);

}

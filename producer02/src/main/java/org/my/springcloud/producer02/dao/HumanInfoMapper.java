package org.my.springcloud.producer02.dao;

import org.apache.ibatis.annotations.Mapper;
import org.my.springcloud.base.bean.HumanInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface HumanInfoMapper {
    HumanInfo getHumanInfo(Integer humanID);

    List<HumanInfo> findAllHuman();
}

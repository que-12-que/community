package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaHibernate") // 可自定义名字，默认为类名首字母小写
public class AlphaDaoHibernateImpl implements AlphaDao{
    @Override
    public String select(){
        return "Hibornate";
    }
}

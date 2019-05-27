package org.geeksword;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/**
 * @Author: zhoulinshun
 * @Description:
 * @Date: Created in 2019-05-24 19:44
 */
public class Main {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
//        configuration.setUseGeneratedKeys();
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
    }
}

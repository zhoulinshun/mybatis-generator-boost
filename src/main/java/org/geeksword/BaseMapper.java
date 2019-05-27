package org.geeksword;

/**
 * @Author: zhoulinshun
 * @Description:
 * @Date: Created in 2019-05-21 19:46
 */
public interface BaseMapper<T, ID> {

    int deleteByPrimaryKey(ID id);

    int insert(T domain);

    int insertSelective(T domain);

    T selectByPrimaryKey(ID id);

    int updateByPrimaryKeySelective(T domain);

    int updateByPrimaryKey(T domain);
}

package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username 用户名
     * @return Employee 员工对象
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 新增员工数据
     *
     * @param employee 员工对象
     */
    @Insert("insert into employee values (null, #{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Employee employee);

    /**
     * 条件查询
     *
     * @param name 员工id
     * @return Employee 员工对象
     */
    Page<Employee> list(String name);

    /**
     * 根据id修改员工信息
     *
     * @param employee 员工对象
     */
    void update(Employee employee);

    /**
     * 根据回显员工信息
     *
     * @param id 员工id
     * @return Employee 员工对象
     */
    Employee getById(Long id);
}





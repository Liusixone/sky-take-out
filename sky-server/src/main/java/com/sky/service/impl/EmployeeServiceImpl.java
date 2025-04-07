package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;


@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 用于封装员工登录信息的数据传输对象，包含员工的用户名和密码等信息。
     * @return 返回一个 Result<EmployeeLoginVO> 对象，表示员工登录操作的结果，包含登录成功后的员工信息和JWT令牌。
     */
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus().equals(StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO 用于封装员工信息的数据传输对象，包含员工的姓名、手机号、性别、身份证号码等信息。
     */
    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {
        //1、属性拷贝
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        //2、补充缺失属性
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employee.setStatus(StatusConstant.ENABLE);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);

    }

    /**
     * 员工分页查询--分页插件PageHelper
     *
     * @param employeePageQueryDTO 用于封装员工分页查询条件的数据传输对象，包含分页信息、查询条件等信息。
     * @return 返回一个 PageResult<Employee> 对象，表示员工分页查询操作的结果，包含查询到的员工信息列表和分页信息。
     */
    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        //1、设置分页参数
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        //2、调用Mapper查询
        Page<Employee> page = employeeMapper.list(employeePageQueryDTO.getName());
        //3、封装结果
        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     * 启用禁用员工账号
     *
     * @param status 用于表示员工账号的状态，1表示启用，0表示禁用。
     * @param id     用于表示员工的唯一标识符。
     */
    @Override
    public void  updateStatus(Integer status, Long id) {
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        //1、调用Mapper
        employeeMapper.update(employee);
    }
    /**
     * 根据id查询员工信息
     *
     * @param id 用于表示员工的唯一标识符。
     * @return 返回一个 Employee 对象，表示查询到的员工信息。
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("******");
        return employee;
    }
    /**
     * 修改员工信息
     *
     * @param employeeDTO 用于封装员工信息的数据传输对象，包含员工的姓名、手机号、性别、身份证号码等信息。
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        //设置修改时间和修改人
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }


}

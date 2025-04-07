package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@Api(tags = "员工管理")
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO 用于封装员工登录信息的数据传输对象，包含员工的用户名和密码等信息。
     * @return 返回一个 Result<EmployeeLoginVO> 对象，表示员工登录操作的结果，包含登录成功后的员工信息和JWT令牌。
     */
    @ApiOperation("员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return 返回一个 Result<String> 对象，表示退出操作的结果，包含退出成功的消息。
     */
    @ApiOperation("员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     *
     * @param employeeDTO 用于封装新增员工信息的数据传输对象，包含员工的基本信息和其他相关数据。
     * @return 返回一个 Result 对象，表示新增员工操作的结果，通常包含成功或失败的消息。
     */
    @ApiOperation("新增员工")
    @PostMapping
    public Result<T> addEmployee(@RequestBody EmployeeDTO employeeDTO) {

        log.info("新增员工：{}", employeeDTO);
        employeeService.addEmployee(employeeDTO);
        return Result.success();
    }

    /**
     * 分页展示员工信息
     *
     * @param employeePageQueryDTO 用于封装分页查询条件的数据传输对象，包含分页信息和查询条件等。
     * @return 返回一个 Result<PageResult> 对象，表示分页查询员工信息的结果，包含查询到的员工信息列表和分页信息。
     */
    @ApiOperation("分页展示员工信息")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("分页查询：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.page(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工账号
     *
     * @param status 用于指定要启用或禁用的员工账号的状态，通常是一个整数值。
     * @param id 用于指定要启用或禁用的员工账号的唯一标识符，通常是一个整数值。
     * @return 返回一个 Result 对象，表示启用禁用员工账号操作的结果，通常包含成功或失败的消息。
     */
    @ApiOperation("启用禁用员工账号")
    @PostMapping("/status/{status}")
    public Result<T> updateStatus(@PathVariable Integer status, Long id) {
        log.info("启用禁用员工账号：status={},id={}", status, id);
        employeeService.updateStatus(status, id);
        return Result.success();

    }

    /**
     * 回显员工信息
     *
     * @param id 用于指定要修改的员工的唯一标识符，通常是一个整数值。
     * @return 返回一个 Result 对象，表示修改员工信息操作的结果，通常包含成功或失败的消息。
     */

    @ApiOperation("回显员工信息")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("回显员工:id={}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 修改员工信息
     *
     * @param employeeDTO 用于封装修改员工信息的数据传输对象，包含员工的基本信息和其他相关数据。
     * @return 返回一个 Result 对象，表示修改员工信息操作的结果，通常包含成功或失败的消息。
     *
     */
    @ApiOperation("修改员工信息")
    @PutMapping
    public Result<T> update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("修改员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

}

package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
@ServletComponentScan
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //获取前端传递过来的密码password并使用md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据前端传递过来的用户名username查数据库
        //new一个查询对象
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //Employee::getUsername就相当于创建一个Employee对象并调用其getUsername方法
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        //username唯一，可以用getOne方法查询,并将查询结果封装到emp中
        Employee emp = employeeService.getOne(queryWrapper);

        //-------------------------------------登陆相关业务逻辑-----------------------------------------


        //不存在的用户
        if(emp == null){
            //返回R对象
            return R.error("登陆失败");
        }


        //用户密码错误
        if(!emp.getPassword().equals(password)){
            //返回R对象
            return R.error("登陆失败");
        }


        //查看员工状态，账号是否禁用
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }


        //执行到这里证明账号正常，登录并将员工id存入Session
        request.getSession().setAttribute("employee", emp.getId());
        //返回登录成功，注意参数是emp不是String了
        return R.success(emp);
    }

    /**
     * 员工退出登录功能
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前员工的id，注意参数与登录时候相同
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

     /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping        //前端console的url只到employee，故这里不需要写进一步的路径
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工：{}", employee.toString());
        //初始密码123456，并进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置新建员工的时间
        employee.setCreateTime(LocalDateTime.now());
        //设置更新员工的时间
        employee.setUpdateTime(LocalDateTime.now());
        //设置更新人、创建人————当前登录用户的id，先通过session获取id
        Long empID = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empID);
        employee.setUpdateUser(empID);
        //调用save方法传参，把这个employee对象传入
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}", page);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.hasText(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);

    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没查询到员工信息");
    }


}

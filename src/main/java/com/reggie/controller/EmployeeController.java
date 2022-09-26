package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 登录功能实现
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

//    @RequestBody接收前端 发送过来的JSON风格的数据，将其转化为相应的对象

    /**  登录功能处理逻辑如下:
     1、将页面提交的密码password进行 MD5 加密处理
     2、根据页面提交的用户名username查询数据库
     3、如果没有查询到数据，则返回登录失败的结果
     4、进行密码比对，如果不一致，则返回登录失败的结果
     5、查看员工状态，如果为 已禁用状态，则返回被禁用的结果信息
     6、登录成功，将员工id 存入Session并返回登录成功的结果
     * */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1、将页面提交的密码password进行 MD5 加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3、如果没有查询到数据，则返回登录失败的结果
        if (emp == null){
            return R.error("用户名不存在！");
        }

        //4、进行密码比对，如果不一致，则返回登录失败的结果
        if (!emp.getPassword().equals(password)){
            return R.error("用户名或密码错误！");
        }

        //5、查看员工状态，如果为 已禁用状态，则返回被禁用的结果信息
        if (emp.getStatus() != 1){  // 账号被禁用，status == 1,账号可以正常登录
            return R.error("账号被禁用，请联系管理员或客服！");
        }

        // 6、登录成功，将员工id 存入Session并返回登录成功的结果
        request.getSession().setAttribute("employee",emp.getId());

        // 将从数据库
        return R.success(emp);
    }


    /**
     * 员工退出功能实现
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获得当前用户登录用户的id
        //Long empId = (Long) request.getSession().getAttribute("employee");

        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }


        /*  pageShow方法的返回对象 应该是MP 中的
       Page对象(包含分页数据集合records、数据总数、每页的大小)
         protected List<T> records;
         protected long total;
         protected long size;
     */

    // 分页展示功能的流程分析:
//     1、页面发送Ajax请求，将分页查询参数(page、pageSize、name)提交到服务端
//     2、服务端Controller接收页面提交的数据 并调用Service查询数据
//     3、Service调用Mapper操作数据库，查询分页的数据
//     4、Controller将查询的分页数据 响应给页面
//     5、页面接收到分页数据并通过前端(ElementUI)的table组件展示到页面上
    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //  构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加过滤条件(name不为null，才会 比较 getUsername方法和前端传入的name是否匹配 的过滤条件)
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件(根据 更新用户的时间升序 分页展示)
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询(去数据库查询)
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * (根据员工id修改数据)编辑员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());


        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);

        long empId = (Long)request.getSession().getAttribute("employee");
        //获取当前时间
        employee.setUpdateTime(LocalDateTime.now());
        //用户修改的时间
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息进行回显（进行修改员工信息）
     * {和添加员工公用一个HTML页面}
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息。。。");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
       return R.error("没有查询到对应员工信息");
    }
}

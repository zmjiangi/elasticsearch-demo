package com.gzyijian.elasticsearchdemo.restcontroller;

import com.google.gson.Gson;
import com.gzyijian.elasticsearchdemo.entity.Employee;
import com.gzyijian.elasticsearchdemo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zmjiangi
 * @date 2019-5-5
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * 添加
     *
     * @return
     */
    @PostMapping("add")
    public String add() {

        Employee employee = new Employee();
        employee.setId("1");
        employee.setFirstName("xuxu");
        employee.setLastName("zh");
        employee.setAge(26);
        employee.setAbout("i am in peking");
        employeeRepository.save(employee);
        System.err.println("add a obj");
        return "success";
    }

    /**
     * 删除
     *
     * @return
     */
    @DeleteMapping("delete")
    public String delete() {
        Employee employee = employeeRepository.queryById("1");
        employeeRepository.delete(employee);
        return "success";
    }

    /**
     * 局部更新
     *
     * @return
     */
    @RequestMapping("update")
    public String update() {
        Employee employee = employeeRepository.queryById("1");
        employee.setFirstName("哈哈");
        employeeRepository.save(employee);
        System.err.println("update a obj");
        return "success";
    }

    /**
     * 查询
     *
     * @return
     */
    @GetMapping("query")
    public Employee query() {
        Employee accountInfo = employeeRepository.queryById("1");
        System.err.println(new Gson().toJson(accountInfo));
        return accountInfo;
    }

}

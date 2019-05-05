package com.gzyijian.elasticsearchdemo.repository;

import com.gzyijian.elasticsearchdemo.entity.Employee;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zmjiangi
 * @date 2019-5-5
 */
@Repository
public interface EmployeeRepository extends ElasticsearchCrudRepository<Employee, String> {

    /**
     * 返回根据id查询的员工信息
     *
     * @param id
     * @return
     */
    Employee queryById(String id);

}

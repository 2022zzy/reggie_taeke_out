package com.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//启动类
@Slf4j
@SpringBootApplication
@ServletComponentScan  //扫描注解
@EnableTransactionManagement  // 开启事务，DishServiceImpl的saveWithFlavor方法
@EnableCaching    // 开启SpringCache注解方式的缓存功能
public class RegieApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegieApplication.class,args);
        log.info("项目启动成功！");
    }
}

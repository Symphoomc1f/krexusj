package com.java110.things;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName ThingsApplicationStart
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 17:02
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
@MapperScan("com.java110.mapper")
@SpringBootApplication
public class ThingsApplicationStart {
    public static void main(String[] args) {
        SpringApplication.run(ThingsApplicationStart.class, args);
    }
}

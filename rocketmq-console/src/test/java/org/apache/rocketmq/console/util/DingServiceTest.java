package org.apache.rocketmq.console.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Title: DingServiceTest
 * @Athor: baowp
 * @CreateTime: 2021/7/21 20:21
 * @Description:
 * @Version: 1.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
//@EnableConfigurationProperties
public class DingServiceTest {
    @Resource
    private DingService dingService;

    @Test
    public void dingtalk(){
        dingService.dingtalk("test1");
    }
}

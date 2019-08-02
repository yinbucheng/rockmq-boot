package cn.bucheng.rockmqboot;

import cn.bucheng.rockmqboot.service.PromoService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RockmqBootApplicationTests {

    @Autowired
    private PromoService promoService;

    @BeforeClass
    public static void initProperties(){
        System.setProperty("spring.profiles.active","fat");
    }

    @Test
    public void testPushPromo(){
        promoService.pushPromo(1);
    }
}

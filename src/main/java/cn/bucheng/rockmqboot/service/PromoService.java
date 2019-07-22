package cn.bucheng.rockmqboot.service;

import cn.bucheng.rockmqboot.entity.PromoEntity;
import com.baomidou.mybatisplus.service.IService;

/**
 * @author ：yinchong
 * @create ：2019/7/22 14:34
 * @description：
 * @modified By：
 * @version:
 */
public interface PromoService extends IService<PromoEntity> {
    void pushPromo(long promoId);
}

package cn.bucheng.rockmqboot.mapper;

import cn.bucheng.rockmqboot.entity.StockLogEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface StockLogMapper extends BaseMapper<StockLogEntity> {
    int updateStatusById(@Param("id") Long id,@Param("status") Integer status);
}

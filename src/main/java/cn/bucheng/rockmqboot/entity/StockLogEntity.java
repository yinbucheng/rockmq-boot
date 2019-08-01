package cn.bucheng.rockmqboot.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * @author ：yinchong
 * @create ：2019/7/22 20:02
 * @description：
 * @modified By：
 * @version:
 */
@TableName("ad_stock_log")
@Alias("stockLog")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockLogEntity extends BaseEntity implements Serializable {
    @TableId(type= IdType.AUTO)
    private Long id;
    //商品id
    private Long itemId;
    //商品数量
    private Integer amount;
    //执行状态
    private Integer status;
    //用户id
    private Long userId;

    private Long promoId;

    private Integer executeNum;
}

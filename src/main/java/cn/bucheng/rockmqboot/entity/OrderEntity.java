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
 * @create ：2019/7/22 13:27
 * @description：
 * @modified By： 订单表
 * @version:
 */
@Alias("order")
@TableName("ad_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends BaseEntity implements Serializable {
    @TableId(type= IdType.INPUT)
    private String id;
    private Long userId;
    private Long itemId;
    private Integer itemPrice;
    private Integer amount;
    private Integer orderPrice;
    private Long promoId;
}

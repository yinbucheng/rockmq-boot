package cn.bucheng.rockmqboot.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ：yinchong
 * @create ：2019/7/22 13:37
 * @description：
 * @modified By：活动促销对象
 * @version:
 */
@Data
@Alias("promot")
@TableName("ad_promo")
@NoArgsConstructor
@AllArgsConstructor
public class PromoEntity extends BaseEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long itemId;
    private Date startTime;
    private Date endTime;
    private Integer promoItemPrice;
}

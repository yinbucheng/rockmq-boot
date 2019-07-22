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
 * @modified By：商品库存表
 * @version:
 */
@TableName("ad_item_stock")
@Alias("itemStock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemStockEntity extends BaseEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer stock;
    private Long itemId;
}

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
 * @create ：2019/7/22 13:26
 * @description：
 * @modified By：商品表
 * @version:
 */
@Alias("item")
@TableName("ad_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemEntity extends BaseEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String title;
    //原理价格
    private Integer price;
    //打折后的价格
    private Integer sales;
    private String describe;
    private String imageUrl;
}

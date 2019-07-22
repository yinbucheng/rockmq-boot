package cn.bucheng.rockmqboot.exception;

/**
 * @author ：yinchong
 * @create ：2019/7/22 12:43
 * @description：
 * @modified By：
 * @version:
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

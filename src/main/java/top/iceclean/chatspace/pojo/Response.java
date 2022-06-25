package top.iceclean.chatspace.pojo;

import top.iceclean.chatspace.constant.ResponseStatusEnum;
import lombok.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : Ice'Clean
 * @date : 2022-01-29
 *
 * 返回给前端的固定格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    /** 返回的状态码，由 ResponseStatusEnum 给定 */
    private Integer status;
    /** 附加的信息 */
    private String msg;
    /** 返回的数据 */
    private Map<String, Object> data = new HashMap<>();

    public Response(ResponseStatusEnum status) {
        setStatus(status);
    }

    public Response addData(String name, Object value) {
        data.put(name, value);
        return this;
    }

    public Response removeData(String name) {
        data.remove(name);
        return this;
    }

    public Response setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Response setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * 通过全局枚举字段直接装配
     * @param status 全局枚举状态码
     * @return 通用返回结果
     */
    public Response setStatus(ResponseStatusEnum status) {
        this.status = status.value();
        this.msg = status.msg();
        return this;
    }

    /**
     * 封装一个对象数据
     * @param data 对象数据
     * @param <T> 类型
     * @return 通用返回结果
     */
    public <T> Response setData (T data) {
        Class<?> dataClass = data.getClass();
        for (Field field : dataClass.getDeclaredFields()) {
            boolean flag = field.isAccessible();
            try {
                field.setAccessible(true);
                this.addData(field.getName(),field.get(data));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(flag);
        }
        return this;
    }


    @Override
    public String toString() {
        return "[" + status + " : " + msg + "] " + data;
    }
}

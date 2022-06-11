package top.iceclean.chatspace.DTO;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import top.iceclean.chatspace.constant.WsType;

/**
 * websocket 消息接收对象
 * @author : Ice'Clean
 * @date : 2022-06-01
 */
@Data
@AllArgsConstructor
public class WsMessageDTO {
    /** 消息类型 */
    private WsType wsType;

    /** 消息主体 */
    private Object wsContent;
}

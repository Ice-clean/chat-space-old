package top.iceclean.chatspace.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.iceclean.chatspace.DTO.RequestDTO;

/**
 * @author : Ice'Clean
 * @date : 2022-06-25
 */
@Data
@NoArgsConstructor
@TableName("t_session_request")
public class SessionRequest {
    @TableId(value = "req_id", type = IdType.AUTO)
    private Integer reqId;
    private Integer type;
    private Integer senderId;
    private Integer targetId;
    private String reqSrc;
    private String reqRemark;
    private Integer reqStatus;
    private String createTime;

    public SessionRequest(RequestDTO requestDTO) {
        this.type = requestDTO.getType().value();
        this.senderId = requestDTO.getSenderId();
        this.targetId = requestDTO.getTargetId();
        this.reqSrc = requestDTO.getReqSrc();
        this.reqRemark = requestDTO.getReqRemark();
    }
}

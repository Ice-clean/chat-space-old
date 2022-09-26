package top.iceclean.chatspace.DTO;

import lombok.Data;
import org.springframework.validation.annotation.Validated;
import top.iceclean.chatspace.constant.SessionType;
import top.iceclean.chatspace.validation.annotation.NotSelf;

import javax.validation.constraints.NotNull;

/**
 * 好友和群聊申请
 * @author : Ice'Clean
 * @date : 2022-08-23
 */
@Data
public class RequestDTO {
    private SessionType type;

    @NotNull(message = "申请人不能为空")
    private Integer senderId;

    @NotNull(message = "目标 ID 不能为空")
    private Integer targetId;

    private String reqSrc;

    private String reqRemark;
}

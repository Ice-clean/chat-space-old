package top.iceclean.chatspace.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话类
 * @author : Ice'Clean
 * @date : 2022-06-11
 */
@Data
@TableName("t_session")
@NoArgsConstructor
public class Session {
    @TableId(value = "session_id", type = IdType.AUTO)
    private Integer sessionId;
    private Integer type;
    private Integer targetId;
    private String deleteTime;
    private String createTime;
    @TableField(update = "now()")
    private String updateTime;

    public Session(int type, int targetId) {
        this.type = type;
        this.targetId = targetId;
    }
}

package top.iceclean.chatspace.VO;

import lombok.Data;
import top.iceclean.chatspace.po.Message;
import top.iceclean.chatspace.utils.DateUtils;

/**
 * 消息响应对象
 * @author : Ice'Clean
 * @date : 2022-05-26 18:00:00
 */
@Data
public class MessageVO {
    /** 消息所属会话*/
    private SessionVO session;
    /** 消息发送者 */
    private UserVO sender;
    /** 消息类型 */
    private Integer type;
    /** 消息内容 */
    private String content;
    /** 消息发送时间 */
    private String sendTime;

    /** 当前用户是否为消息发送者 */
    private Boolean self;

    /**
     * 构建消息响应对象
     * @param message 目标消息
     * @param session 消息所属会话
     * @param sender 消息发送者
     * @param self 消息是否为当前用户发送
     */
    public MessageVO(Message message, SessionVO session, UserVO sender, boolean self) {
        // 消息元数据
        this.session = session;
        this.sender = sender;
        this.type = message.getType();
        this.content = message.getContent();
        this.sendTime = message.getCreateTime();

        // 用户个性化数据
        this.self = self;
    }

    /**
     * 精简消息发送时间
     * 如果是今天以内的，缩短为小时和分钟
     * 如果是今天之前的，缩短为月数和天数
     * 如果是今年之前的，缩短为年份和月份
     * @param want 是否需要
     */
    public MessageVO shortTime(boolean want) {
        // 在需要时精简
        if (want) {
            // 重解析时间为数组
            int[] times = DateUtils.reparseTime(sendTime);
            // 获取年
            int year = Integer.parseInt(DateUtils.getYear());
            int day = Integer.parseInt(DateUtils.getDay());
            // 精简日期
            if (year > times[0]) {
                sendTime = String.format("%d年%d月", times[0], times[1]);
            } else if (day > times[2]) {
                sendTime = String.format("%d月%d日", times[1], times[2]);
            } else {
                sendTime = String.format("%02d:%02d", times[3], times[4]);
            }
        }
        return this;
    }
}

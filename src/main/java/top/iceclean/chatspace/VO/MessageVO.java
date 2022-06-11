package top.iceclean.chatspace.VO;

import lombok.Data;
import top.iceclean.chatspace.po.Message;
import top.iceclean.chatspace.utils.DateUtils;

/**
 * 消息响应对象
 * @author : Ice'Clean
 * @date : 2022-05-26
 */
@Data
public class MessageVO {
    /** 消息发送者 */
    private UserVO sender;
    /** 消息类型（私聊/群聊） */
    private Integer type;
    /** 接收方 ID，可能为好友主键或群主键 */
    private Integer receiveId;
    /** 消息内容 */
    private String content;
    /** 消息是否已读 */
    private Boolean isRead;
    /** 当前用户是否为消息发送者 */
    private Boolean self;
    /** 消息发送时间 */
    private String sendTime;

    /** 头像、名称、在线信息和在线记录（获取消息列表时会有，获取聊天记录时不需要） */
    private String name;
    private String avatar;
    /** 为朋友时，值为 0/1 表示是否在线，为群聊时，值为 -1，且有在线记录 onlineRecord */
    private String online;
    /** 在线记录，值为 在线人数/总人数，为群聊时有 */
    private String onlineRecord;

    public MessageVO(Message message, UserVO sender, Boolean self) {
        this.type = message.getType();
        this.receiveId = message.getReceiveId();
        this.content = message.getContent();
        this.isRead = message.getIsRead();
        this.sendTime = message.getCreateTime();
        this.sender = sender;
        this.self = self;
    }

    /** 设置消息额外信息 */
    public MessageVO setInfo(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
        return this;
    }

    /**
     * 将 createTime 时间长度变短
     * 如果是今天以内的，缩短为小时和分钟
     * 如果是今天之前的，缩短为月数和天数
     * 如果是今年之前的，缩短为年份和月份
     */
    public MessageVO shortTime() {
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
        return this;
    }
}

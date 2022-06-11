package top.iceclean.chatspace.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import top.iceclean.chatspace.DTO.MessageDTO;
import top.iceclean.chatspace.VO.MessageVO;
import top.iceclean.chatspace.VO.SessionVO;
import top.iceclean.chatspace.VO.UserVO;
import top.iceclean.chatspace.constant.SessionType;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.mapper.MessageMapper;
import top.iceclean.chatspace.po.Message;
import top.iceclean.chatspace.po.Response;
import top.iceclean.chatspace.service.*;
import top.iceclean.chatspace.utils.DateUtils;
import top.iceclean.logtrace.annotation.EnableLogTrace;
import top.iceclean.logtrace.bean.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@Service
@EnableLogTrace
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserService userService;
    @Lazy
    @Autowired
    private SessionService sessionService;
    private Logger logTrace;


    @Override
    public Response getSessionMessageList(int userId) {
        // 获取所有会话 ID
        List<Integer> sessionIdList = sessionService.getSessionIdList(userId);

        // 获取每个会话的最新消息 ID
        List<Integer> messageIdList = messageMapper.selectList(new QueryWrapper<Message>()
                .select("max(id) as id")
                .in("session_id", sessionIdList).groupBy("session_id"))
                .stream().map(Message::getId).collect(Collectors.toList());

        // 查询出最新的会话消息
        List<Message> latestMessageList = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .in(Message::getId, messageIdList).orderByDesc(Message::getCreateTime));

        // 将最新的会话信息封装成响应对象并返回
        List<MessageVO> messageList = toListMessageVO(latestMessageList, userId, true);
        return new Response(ResponseStatusEnum.OK).addData("messageList", messageList);
    }

    @Override
    public Response getChatHistory(int userId, int sessionId, int page) {
        // 查询指定会话的历史消息（这里不需要按时间倒序排，正常顺序插入即可）
        List<Message> messageList = messageMapper.selectPage(
                new Page<>(page, 10),
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getSessionId, sessionId)
        ).getRecords();

        // 将历史信息封装成响应对象并返回
        List<MessageVO> historyList = toListMessageVO(messageList, userId, false);
        return new Response(ResponseStatusEnum.OK).addData("historyList", historyList);
    }

    @Override
    public MessageVO toMessageVO(Message message, int userId, boolean info) {
        // 获取会话响应对象（根据是否需要详细信息获取会话响应对象）
        SessionVO sessionVO = info ? sessionService.getSessionVO(message.getSessionId(), userId) :
                sessionService.getSessionVO(message.getSessionId());

        // 获取发送用户的响应对象，初步构建消息响应对象
        UserVO userVO = userService.toUserVO(userService.getUserById(message.getSenderId()));
        MessageVO messageVO = new MessageVO(message, sessionVO, userVO, userId == message.getSenderId());

        if (info) {
            // 需要详细信息时，将时间格式精简
            messageVO.shortTime();
            // 是群聊且非用户本身发消息时，在消息预览区显示发送者昵称 TODO 有备注时应该显示备注的，这里还没有实现
            if (message.getType() == SessionType.GROUP.value() && userId != message.getSenderId()) {
                messageVO.setContent(userVO.getNickName() + "：" + message.getContent());
            }
        }

        return messageVO;
    }

    @Override
    public Message saveMessage(MessageDTO messageDTO) {
        Message message = new Message(messageDTO);
        message.setCreateTime(DateUtils.getTime());
        messageMapper.insert(message);
        return message;
    }

    @Override
    public int getLastMsgId(int sessionId) {
        // TODO 考虑先从缓存拿
        return messageMapper.selectOne(new QueryWrapper<Message>()
                .select("max(msg_id) as msg_id")
                .eq("session_id", sessionId)).getMsgId();
    }

    /**
     * 根据消息列表生成响应消息列表
     * @param messageList 消息列表
     * @param userId 用户 ID
     * @param info 是否需要详细信息（消息列表需要）
     * @return 消息响应实体列表
     */
    public List<MessageVO> toListMessageVO(List<Message> messageList, int userId, boolean info) {
        List<MessageVO> list = new ArrayList<>(messageList.size());
        messageList.forEach(message -> list.add(toMessageVO(message, userId, info)));
        return list;
    }
}

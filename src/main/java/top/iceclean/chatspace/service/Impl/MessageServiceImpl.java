package top.iceclean.chatspace.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.iceclean.chatspace.DTO.MessageDTO;
import top.iceclean.chatspace.VO.MessageVO;
import top.iceclean.chatspace.VO.UserVO;
import top.iceclean.chatspace.constant.ReceiveType;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.mapper.MessageMapper;
import top.iceclean.chatspace.po.Group;
import top.iceclean.chatspace.po.Message;
import top.iceclean.chatspace.po.Response;
import top.iceclean.chatspace.po.User;
import top.iceclean.chatspace.service.GroupService;
import top.iceclean.chatspace.service.MessageService;
import top.iceclean.chatspace.service.FriendService;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.chatspace.utils.DateUtils;
import top.iceclean.logtrace.annotation.EnableLogTrace;
import top.iceclean.logtrace.bean.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    @Autowired
    private FriendService friendService;
    @Autowired
    private GroupService groupService;
    private Logger logTrace;

    @Override
    public Response getMessageList(int userId) {
        // 获取所有接收域主键列表（分朋友和群聊这两个接收域）
        List<String> receiveList = new ArrayList<>();
        receiveList.addAll(friendService.getFriendKeyList(userId).stream().map(id -> "(0," + id + ")").collect(Collectors.toList()));
        receiveList.addAll(groupService.getGroupKeyList(userId).stream().map(id -> "(1," + id + ")").collect(Collectors.toList()));

        // 拼接 in 条件
        String inCondition = String.join(",", receiveList);
        // 获取最新的历史消息主键列表
        List<Integer> historyIdList = messageMapper.selectList(new QueryWrapper<Message>()
                .select("max(id) as id").inSql("(type, receive_id)", inCondition).groupBy(Arrays.asList("type", "receive_id")))
                .stream().map(Message::getId).collect(Collectors.toList());

        // 查询出最新的历史消息
        List<Message> chatHistoryList = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .in(Message::getId, historyIdList).orderByDesc(Message::getCreateTime));

        // 将历史信息分装成响应对象并返回
        List<MessageVO> messageList = toChatHistoryVO(chatHistoryList, userId, true, true);
        return new Response(ResponseStatusEnum.OK).addData("messageList", messageList);
    }

    @Override
    public Response getChatHistory(int userId, int type, int receiveId, int page) {
        // 查询指定接收域的历史消息
        List<Message> messageList = messageMapper.selectPage(
                new Page<>(page, 10),
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getType, type)
                        .eq(Message::getReceiveId, receiveId)
                        .orderByDesc(Message::getCreateTime)
        ).getRecords();
        Collections.reverse(messageList);
        // 将历史信息分装成响应对象并返回
        List<MessageVO> historyList = toChatHistoryVO(messageList, userId, false, false);
        return new Response(ResponseStatusEnum.OK).addData("historyList", historyList);
    }

    @Override
    public MessageVO toMessageVO(Message message, int userId, boolean shortTime, boolean info) {
        // 获取发送用户的响应对象，初步构建消息响应对象
        UserVO userVO = userService.toUserVO(userService.getUserById(message.getSenderId()));
        MessageVO messageVO = new MessageVO(message, userVO, userId == message.getSenderId());

        // 需要将时间缩短
        if (shortTime) {
            messageVO.shortTime();
        }

        // 需要额外信息
        if (info) {
            if (message.getType() == ReceiveType.FRIEND.value()) {
                // 额外的名字和头像信息
                User friend = userService.getUserById(friendService.getFriendUserId(userId, message.getReceiveId()));
                messageVO.setInfo(friend.getUserNick(), friend.getUserAvatar());

                // 朋友的在线状态为 0/1
                messageVO.setOnline(userService.isOnline(friend.getUserId()) ? "1" : "0");
            } else if (message.getType() == ReceiveType.GROUP.value()) {
                // 额外的名字和头像信息
                Group group = groupService.getGroupById(message.getReceiveId());
                messageVO.setInfo(group.getName(), group.getAvatar());

                // 群聊的在线状态为 在线人数/总人数
                messageVO.setOnline("-1");
                messageVO.setOnlineRecord(groupService.getOnlineNum(group.getGroupId()) + " / " + group.getNumber());

                // 是群聊且非用户本身发消息时，在消息预览区显示发送者昵称
                if (userId != message.getSenderId()) {
                    messageVO.setContent(userVO.getUserNick() + "：" + message.getContent());
                }
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

    /**
     * 根据消息列表生成响应消息列表
     * @param messageList 消息列表
     * @param userId 用户 ID
     * @param shortTime 是否需要自动缩短时间（消息列表需要）
     * @param info 是否需要额外信息（消息列表需要）
     * @return 消息响应实体列表
     */
    public List<MessageVO> toChatHistoryVO(List<Message> messageList, int userId, boolean shortTime, boolean info) {
        List<MessageVO> list = new ArrayList<>(messageList.size());
        messageList.forEach(message -> list.add(toMessageVO(message, userId, shortTime, info)));
        return list;
    }
}

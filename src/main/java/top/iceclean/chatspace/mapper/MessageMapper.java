package top.iceclean.chatspace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.iceclean.chatspace.po.Message;

import java.util.List;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}

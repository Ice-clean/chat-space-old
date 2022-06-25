package top.iceclean.chatspace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.iceclean.chatspace.po.SessionRequest;

/**
 * 会话请求
 * @author : Ice'Clean
 * @date : 2022-06-25
 */
@Mapper
public interface SessionRequestMapper extends BaseMapper<SessionRequest> {
}

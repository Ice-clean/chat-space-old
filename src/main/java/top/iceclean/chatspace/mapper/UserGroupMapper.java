package top.iceclean.chatspace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.iceclean.chatspace.po.Group;
import top.iceclean.chatspace.po.UserGroup;

/**
 * @author : Ice'Clean
 * @date : 2022-05-29
 */
@Mapper
public interface UserGroupMapper extends BaseMapper<UserGroup> {
}

package top.iceclean.chatspace.po;

import lombok.Data;

/**
 * 用户位置实体
 * @author : Ice'Clean
 * @date : 2022-10-01
 */
@Data
public class Site {
    private Integer userId;
    private Integer spaceId;
    private Integer x;
    private Integer y;
    private Integer vision;
    private Integer exist;
}

package top.iceclean.chatspace.DTO;

import lombok.Data;

/**
 * 用户位置对象
 * @author : Ice'Clean
 * @date : 2022-09-30
 */
@Data
public class SiteDTO {
    private Integer spaceId;
    private Integer x;
    private Integer y;
    private Integer vision;
    private Integer exist;
}

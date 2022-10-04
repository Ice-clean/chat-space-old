package top.iceclean.chatspace.po;

import lombok.Data;

/**
 * 空间实体
 * @author : Ice'Clean
 * @date : 2022-10-01
 */
@Data
public class Space {
    private Integer spaceId;
    private String spaceName;
    private Integer sizeX;
    private Integer sizeY;
}

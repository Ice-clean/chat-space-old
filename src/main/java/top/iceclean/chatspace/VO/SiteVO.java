package top.iceclean.chatspace.VO;

import lombok.Data;
import top.iceclean.chatspace.DTO.SiteDTO;
import top.iceclean.chatspace.po.Site;

/**
 * 用户位置响应对象
 * @author : Ice'Clean
 * @date : 2022-10-01
 */
@Data
public class SiteVO {
    private UserVO user;
    private Integer spaceId;
    private Integer x;
    private Integer y;
    private Integer vision;
    private Integer exist;

    public SiteVO(SiteDTO site, UserVO user) {
        this.user = user;
        this.spaceId = site.getSpaceId();
        this.x = site.getX();
        this.y = site.getY();
    }

    public SiteVO(Site site, UserVO user) {
        this.user = user;
        this.spaceId = site.getSpaceId();
        this.x = site.getX();
        this.y = site.getY();
    }

    /** 更新当前位置 */
    public void updateSite(SiteDTO site) {
        this.spaceId = site.getSpaceId();
        this.x = site.getX();
        this.y = site.getY();
    }
}

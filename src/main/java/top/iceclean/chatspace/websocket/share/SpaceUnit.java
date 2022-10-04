package top.iceclean.chatspace.websocket.share;

import top.iceclean.chatspace.DTO.SiteDTO;
import top.iceclean.chatspace.VO.SiteVO;
import top.iceclean.chatspace.VO.UserVO;
import top.iceclean.chatspace.po.Site;
import top.iceclean.chatspace.po.Space;
import top.iceclean.chatspace.po.User;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 空间单元
 * @author : Ice'Clean
 * @date : 2022-10-01
 */
public class SpaceUnit {
    /** 所有的空间 */
    private static final ConcurrentHashMap<Integer, SpaceUnit> SPACE_MAP = new ConcurrentHashMap<>();
    /** 所有的用户坐标 */
    private static final ConcurrentHashMap<Integer, SiteVO> SITE_MAP = new ConcurrentHashMap<>();

    /** 空间网格 */
    private GridUnit[][] spaceGrid;

    /** 空间的ID、名称和横纵大小 */
    private Integer spaceId;
    private String spaceName;
    private Integer sizeX;
    private Integer sizeY;

    /**
     * 初始化一个空间
     * @param space 空间实体
     */
    public SpaceUnit(Space space) {
        System.out.println("初始化空间：" + space);
        this.spaceId = space.getSpaceId();
        this.spaceName = space.getSpaceName();
        this.sizeX = space.getSizeX();
        this.sizeY = space.getSizeY();
        initSpaceGrid();
        SPACE_MAP.put(space.getSpaceId(), this);
    }

    /** 初始化空间网格 */
    private void initSpaceGrid() {
        spaceGrid = new GridUnit[sizeY][sizeX];
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                spaceGrid[y][x] = new GridUnit(spaceId, x, y, 0);
            }
        }
    }

    /** 判断坐标是否合法 */
    public boolean isLegal(int x, int y) {
        return x >= 0 && x < sizeX && y >= 0 && y < sizeY;
    }

    /** 获取空间网格 */
    public GridUnit[][] getSpaceGrid() {
        return spaceGrid;
    }

    /** 获取网格单元 */
    public GridUnit getGrid(int x, int y) {
        return spaceGrid[x][y];
    }

    /** 获取用户所在的网格单元 */
    public static GridUnit getGrid(int userId) {
        SiteVO site = SITE_MAP.get(userId);
        return SPACE_MAP.get(site.getSpaceId()).spaceGrid[site.getY()][site.getX()];
    }

    /** 获取指定空间的指定网格 */
    public static GridUnit getGrid(int spaceId, int x, int y) {
        return SPACE_MAP.get(spaceId).spaceGrid[y][x];
    }

    /** 获取空间网格 */
    public static GridUnit[][] getSpaceGrid(int spaceId) {
        return SPACE_MAP.get(spaceId).spaceGrid;
    }

    /** 获取空间单元 */
    public static SpaceUnit getSpace(int spaceId) {
        return SPACE_MAP.get(spaceId);
    }

    /** 获取用户坐标 */
    public static SiteVO getSite(int userId) {
        return SITE_MAP.get(userId);
    }

    /** 更新用户坐标，并返回用户原先所在的网格 */
    public static GridUnit updateSite(int userId, SiteDTO siteDTO) {
        GridUnit grid = getGrid(userId);
        SITE_MAP.get(userId).updateSite(siteDTO);
        return grid;
    }

    /** 用户初次抵达空间 */
    public static void newToSpace(Site site, User user) {
        SITE_MAP.put(user.getUserId(), new SiteVO(site, new UserVO(user, true)));
        // TODO 初次抵达需要初始化各种属性（存在位置、视野、存在感）
        for (int y = site.getY(); y < site.getY() + site.getVision(); y++) {

        }
    }

//    /**
//     * 新用户进入空间
//     * @param site 用户的位置信息
//     */
//    public static void userIn(Site site) {
//        SPACE_MAP.get(site.getSpaceId()).
//    }
}

package top.iceclean.chatspace.websocket.processor;

import com.alibaba.fastjson.JSONObject;
import top.iceclean.chatspace.DTO.SiteDTO;
import top.iceclean.chatspace.constant.WsType;
import top.iceclean.chatspace.po.Site;
import top.iceclean.chatspace.po.Space;
import top.iceclean.chatspace.websocket.share.GridUnit;
import top.iceclean.chatspace.websocket.share.SpaceUnit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户位置处理器
 * @author : Ice'Clean
 * @date : 2022-09-30
 */
public class SiteProcessor {
    /** 内置执行函数式接口 */
    private interface Exec {
        /**
         * 执行单元格转移逻辑
         * @param oldX 旧单元格 X 坐标
         * @param oldY 旧单元格 Y 坐标
         * @param newX 新单元格 X 坐标
         * @param newY 新单元格 Y 坐标
         */
        void move(int oldX, int oldY, int newX, int newY);
    }

    public static void newSite(Site site) {
        rangeScan(site.getSpaceId(), site.getVision(), site.getX(), site.getY(), site.getUserId(), 1);
        rangeScan(site.getSpaceId(), site.getExist(), site.getX(), site.getY(), site.getUserId(), 2);
    }

    /**
     * 更新位置信息
     * @param content 位置信息串
     * @param userId 当前用户的 ID
     */
    public static void updateSite(String content, int userId) {
        // 转化为位置类型，并获取新旧网格
        SiteDTO siteDTO = JSONObject.parseObject(content, SiteDTO.class);
        System.out.println(userId + "：" + siteDTO);
        GridUnit oldGridUnit = SpaceUnit.updateSite(userId, siteDTO);
        GridUnit newGridUnit = SpaceUnit.getGrid(userId);

        // 首先完成用户存在感的更新（后续两个操作都要用到）
        changeExist(userId, siteDTO.getExist(), oldGridUnit, newGridUnit);

        // 将用户更新到新的位置，并向其他用户发送移除、发现或更新当前用户的消息
        changeSite(userId, oldGridUnit, newGridUnit);

        // 更新用户的视野范围，并发送给当前用户移除或发现用户的消息
        changeVision(userId, siteDTO.getVision(), oldGridUnit, newGridUnit);

        GridUnit[][] spaceGrid = SpaceUnit.getSpaceGrid(0);
        for (GridUnit[] gridArray : spaceGrid) {
            System.out.println(Arrays.toString(gridArray));
        }
    }

    private static void changeExist(int userId, int exist, GridUnit oldGridUnit, GridUnit newGridUnit) {
        SpaceUnit spaceUnit = SpaceUnit.getSpace(newGridUnit.getSpaceId());
        rangeMoveScan(exist, oldGridUnit.getX(), oldGridUnit.getY(), newGridUnit.getX(), newGridUnit.getY(), (oldX, oldY, newX, newY) -> {
            // 存在感，简单的进行移除和增加即可
            if (spaceUnit.isLegal(oldX, oldY)) {
                spaceUnit.getGrid(oldX, oldY).removeExist(userId);
            }
            if (spaceUnit.isLegal(newX, newY)) {
                spaceUnit.getGrid(newX, newY).addExist(userId);
            }
        });
    }

    /**
     * 将当前用户从就网格移动到新网格
     * @param userId 当前用户 ID
     * @param oldGridUnit 旧网格
     * @param newGridUnit 新网格
     */
    private static void changeSite(int userId, GridUnit oldGridUnit, GridUnit newGridUnit) {
        // 将用户从旧网格中移除，并返回该网格上的所有视野观察者（无法发现该用户的其他用户）
        Set<Integer> remove = oldGridUnit.removeUser(userId);
        // 将用户添加到新网格位置，并返回该网格上视野观察者所属用户能感知到该用户的其他用户
        Set<Integer> add = newGridUnit.addUser(userId)
                .stream().filter(uId -> SpaceUnit.getGrid(uId).exist(userId)).collect(Collectors.toSet());

        // 从上边两个集合中，挑选出需要更新坐标的用户（先移除了该用户，后边又发现该用户的，则判定为更新）
        Set<Integer> update = new HashSet<>();
        Iterator<Integer> iterator = remove.iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            if (add.contains(id)) {
                iterator.remove();
                add.remove(id);
                update.add(id);
            }
        }

        // 最后执行移除、发现和更新的消息通知
        MessageProcessor.changeSiteState(add, WsType.SITE_ADD, userId);
        MessageProcessor.changeSiteState(remove, WsType.SITE_REMOVE, userId);
        MessageProcessor.changeSiteState(update, WsType.SITE_UPDATE, userId);
    }

    private static void changeVision(int userId, int vision, GridUnit oldGridUnit, GridUnit newGridUnit) {
        // 移除和发现其他用户的集合（不存在更新的情况）
        Set<Integer> remove = new HashSet<>();
        Set<Integer> add = new HashSet<>();
        SpaceUnit spaceUnit = SpaceUnit.getSpace(newGridUnit.getSpaceId());
        rangeMoveScan(vision, oldGridUnit.getX(), oldGridUnit.getY(), newGridUnit.getX(), newGridUnit.getY(), (oldX, oldY, newX, newY) -> {
            // 移除的话肯定就是移除成功
            if (spaceUnit.isLegal(oldX, oldY)) {
                remove.addAll(spaceUnit.getGrid(oldX, oldY).removeVision(userId));
            }
            // 而添加的话，还需要判断是否位于对方的存在感之内
            if (spaceUnit.isLegal(newX, newY)) {
                add.addAll(spaceUnit.getGrid(newX, newY).addVision(userId)
                        .stream().filter(newGridUnit::exist).collect(Collectors.toSet()));
            }
        });

        // 最后执行移除、发现的消息通知
        MessageProcessor.changeSiteState(userId, WsType.SITE_ADD, add);
        MessageProcessor.changeSiteState(userId, WsType.SITE_REMOVE, remove);
    }

    /**
     * 范围移动扫描，将就范围更新为新范围，
     * 采用的是差值扫描方法，即两个范围重叠的地方不进行扫描
     * @param range 范围的大小
     * @param startX 起点 X 坐标
     * @param startY 起点 Y 坐标
     * @param endX 终点 X 坐标
     * @param endY 终点 Y 坐标
     * @param exec 处理方法
     */
    private static void rangeMoveScan(int range, int startX, int startY, int endX, int endY, Exec exec) {
        // 遍历因子计算
        int len = range * 2 + 1;
        int xMove, yMove;
        int xFlag = 1, yFlag = 1;

        if ((xMove = endX - startX) < 0) {
            xMove = - xMove;
            xFlag = -xFlag;
        }
        if ((yMove = endY - startY) < 0) {
            yMove = -yMove;
            yFlag = -yFlag;
        }

        int oldX = startX - range * xFlag;
        int newX = endX + range * xFlag;
        int oldY = startY - range * yFlag;
        int newY = endY + range * yFlag;

        // 真正的遍历
        for (int x = 0; x < xMove; x++) {
            for (int y = 0; y < len; y++) {
                exec.move(oldX + x * xFlag, oldY + y * yFlag, newX - x * xFlag, newY - y * yFlag);
            }
        }

        for (int y = 0; y < yMove; y++) {
            for (int x = xMove; x < len; x++) {
                exec.move(oldX + x * xFlag, oldY + y * yFlag, newX - x * xFlag, newY - y * yFlag);
            }
        }
    }

    public static void rangeScan(int spaceId, int range, int x, int y, int userId, int kind) {
        SpaceUnit spaceUnit = SpaceUnit.getSpace(spaceId);
        x = Math.max(x - range, 0);
        y = Math.max(y - range, 0);
        range = 2 * range + 1;
        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                if (spaceUnit.isLegal(x+i, y+j)) {
                    switch (kind) {
                        case 1: spaceUnit.getGrid(x, y).addVision(userId); break;
                        case 2: spaceUnit.getGrid(x, y).addExist(userId); break;
                        default:
                    }
                }
            }
        }
    }
}

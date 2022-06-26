package top.iceclean.chatspace.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件常量
 * @author : Ice'Clean
 * @date : 2022-06-26
 */
public class FileConst {

    @Getter
    @AllArgsConstructor
    public enum Code {
        /** 文件上传状态码 */
        UPLOAD_SUCCESS("上传成功"),
        EMPTY_FILE("上传失败，文件为空"),
        CREATE_DIR_FAILED("上传失败，创建文件夹失败"),
        UNKNOWN_ERROR("上传失败，未知错误");

        private final String msg;
    }

    /** 文件的上传路径 */
    public static final String ROOT_PATH = "/home/iceclean/project/ChatSpace/static/";
    public static final String AVATAR_PATH = "img/avatar/";
    public static final String CHAT_IMG_PATH = "img/chat/";
}

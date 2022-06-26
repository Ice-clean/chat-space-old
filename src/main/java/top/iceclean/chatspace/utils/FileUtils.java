package top.iceclean.chatspace.utils;

import org.springframework.web.multipart.MultipartFile;
import top.iceclean.chatspace.constant.FileConst;

import java.io.File;
import java.io.IOException;

/**
 * 文件处理工具
 * @author : Ice'Clean
 * @date : 2022-06-26
 */
public class FileUtils {

    /**
     * 上传文件
     * @param file 文件数据
     * @param filePath 文件路径
     * @param fileName 上传后的文件名称
     * @return 上传状态码
     */
    public static FileConst.Code upload(MultipartFile file, String filePath, String fileName) {
        // 文件为空，上传失败
        if (file.isEmpty()) {
            return FileConst.Code.EMPTY_FILE;
        }

        try {
            // 获取文件目录
            File dir = new File(FileConst.ROOT_PATH + filePath);
            System.out.println(dir.isAbsolute());
            if (dir.exists() || dir.mkdirs()) {
                // 尝试上传文件
                File file1 = new File(FileConst.ROOT_PATH + filePath + fileName);
                System.out.println(file1.isAbsolute());
                file.transferTo(new File(FileConst.ROOT_PATH + filePath + fileName));
                return FileConst.Code.UPLOAD_SUCCESS;
            } else {
                // 创建文件夹失败
                return FileConst.Code.CREATE_DIR_FAILED;
            }
        } catch (Exception e) {
             e.printStackTrace();
        }

        return FileConst.Code.UNKNOWN_ERROR;
    }
}

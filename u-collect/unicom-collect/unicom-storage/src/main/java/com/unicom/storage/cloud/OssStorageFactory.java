package com.unicom.storage.cloud;


import com.unicom.common.util.SpringContextUtils;
import com.unicom.storage.entity.enums.OssTypeEnum;

/**
 * 文件上传Factory
 *
 * @author smalljop
 */
public final class OssStorageFactory {
    private static OssStorageConfig config;

    static {
        config = SpringContextUtils.getBean(OssStorageConfig.class);
    }


    public static OssStorageService build() {
        if (config.getOssType() == OssTypeEnum.QINIU) {
            return new QiniuCloudStorageService(config);
        } else if (config.getOssType() == OssTypeEnum.ALIYUN) {
            return new AliyunOssStorageService(config);
        } else if (config.getOssType() == OssTypeEnum.UPYUN) {
            return new UpyunStorageService(config);
        } else if (config.getOssType() == OssTypeEnum.LOCAL) {
            return new localStorageService(config);
        }
        return null;
    }

}

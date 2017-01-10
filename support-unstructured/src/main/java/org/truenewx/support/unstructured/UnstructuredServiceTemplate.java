package org.truenewx.support.unstructured;

import java.io.Serializable;

import org.truenewx.support.unstructured.model.UnstructuredWriteToken;

/**
 * 非结构化存储服务模版
 *
 * @author jianglei
 * @param <AT>
 *            授权类型
 * @param <UT>
 *            用户类型
 * @param <UK>
 *            用户标识类型
 */
public interface UnstructuredServiceTemplate<AT extends Enum<AT>, UT extends Enum<UT>, UK extends Serializable> {

    /**
     * 指定用户获取指定授权类型资源的写权限
     *
     * @param authorizeType
     *            授权类型
     * @param userId
     *            用户id
     * @return 写权限令牌
     */
    UnstructuredWriteToken authorizePrivateWrite(AT authorizeType, UK userId);

    /**
     * 公开指定用户在指定授权类型资源中指定文件的读取授权，可供匿名用户访问
     *
     * @param authorizeType
     *            授权类型
     * @param userId
     *            用户id
     * @param filename
     *            文件名
     */
    void authorizePublicRead(AT authorizeType, UK userId, String filename);

    /**
     * 指定用户获取指定授权类型资源URL的外部访问URL
     *
     * @param authorizeType
     *            授权类型
     * @param userType
     *            用户类型
     * @param userId
     *            用户id
     * @param innerUrl
     *            内部URL
     * @param protocol
     *            外部访问协议，取值：http、https、空
     * @return 外部访问URL
     */
    String getOuterUrl(AT authorizeType, UT userType, UK userId, String innerUrl, String protocol);

}

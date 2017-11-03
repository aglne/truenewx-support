package org.truenewx.support.unstructured.core;

import org.truenewx.support.unstructured.core.model.UnstructuredProvider;

/**
 * 非结构化存储授权器
 *
 * @author jianglei
 *
 */
public interface UnstructuredAuthorizer {

    /**
     * 获取当前授权器的服务提供商
     *
     * @return 服务提供商
     */
    UnstructuredProvider getProvider();

    /**
     * @return 服务主机地址
     */
    String getHost();

    /**
     * @return 所属地区
     */
    String getRegion();

    /**
     * 标准化资源路径，使其符合服务商的规则
     *
     * @param path
     *            资源路径
     * @return 标准化后的资源路径
     */
    String standardizePath(final String path);

    /**
     * 授权指定资源为公开可读
     *
     * @param bucket
     *            存储桶名称
     * @param path
     *            资源路径
     */
    void authorizePublicRead(String bucket, String path);

    /**
     * 获取指定资源读取URL
     *
     * @param userKey
     *            用户唯一标识
     * @param bucket
     *            存储桶名称
     * @param path
     *            资源路径
     *
     * @return 资源读取URL
     */
    String getReadUrl(String userKey, String bucket, String path);

}

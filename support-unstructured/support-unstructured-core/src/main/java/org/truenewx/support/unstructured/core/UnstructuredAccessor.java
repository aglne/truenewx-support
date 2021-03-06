package org.truenewx.support.unstructured.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.truenewx.support.unstructured.core.model.UnstructuredStorageMetadata;

/**
 * 非结构化数据访问器
 *
 * @author jianglei
 *
 */
public interface UnstructuredAccessor {

    void write(String bucket, String path, String filename, InputStream in) throws IOException;

    /**
     * 获取指定资源的最后修改时间
     *
     * @param bucket
     *            存储桶名
     * @param path
     *            存储路径
     * @return 最后修改时间毫秒数，指定资源不存在时返回0
     */
    long getLastModifiedTime(String bucket, String path);

    /**
     * 获取指定资源的存储元信息
     *
     * @param bucket
     *            存储桶名
     * @param path
     *            存储路径
     * @return 资源元信息
     */
    UnstructuredStorageMetadata getStorageMetadata(String bucket, String path);

    void read(String bucket, String path, OutputStream out) throws IOException;

}

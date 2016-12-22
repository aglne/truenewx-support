package org.truenewx.support.unstructured.model;

/**
 * 非结构化存储写权限令牌
 *
 * @author jianglei
 *
 */
public class UnstructuredWriteToken {

    /**
     * 服务商
     */
    private UnstructuredProvider provider;

    /**
     * 授权账号id
     */
    private String accountId;

    /**
     * 授权账号密钥
     */
    private String accountSecret;

    /**
     * 存储主机路径
     */
    private String host;

    /**
     * 存储桶名称
     */
    private String bucket;

    /**
     * 资源相对路径
     */
    private String path;

    /**
     * 资源完整URL
     */
    private String url;

    /**
     * 是否公开可读
     */
    private boolean publicReadable;

    /**
     * @return 服务商
     */
    public UnstructuredProvider getProvider() {
        return this.provider;
    }

    /**
     * @param provider
     *            服务商
     */
    public void setProvider(final UnstructuredProvider provider) {
        this.provider = provider;
    }

    /**
     * @return 授权账号id
     */
    public String getAccountId() {
        return this.accountId;
    }

    /**
     * @param accountId
     *            授权账号id
     */
    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    /**
     * @return 授权账号密钥
     */
    public String getAccountSecret() {
        return this.accountSecret;
    }

    /**
     * @param accountSecret
     *            授权账号密钥
     */
    public void setAccountSecret(final String accountSecret) {
        this.accountSecret = accountSecret;
    }

    /**
     * @return 存储主机路径
     */
    public String getHost() {
        return this.host;
    }

    /**
     * @param host
     *            存储主机路径
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * @return 储存桶
     */
    public String getBucket() {
        return this.bucket;
    }

    /**
     * @param bucket
     *            储存桶
     */
    public void setBucket(final String bucket) {
        this.bucket = bucket;
    }

    /**
     * @return 资源相对路径
     */
    public String getPath() {
        return this.path;
    }

    /**
     * @param path
     *            资源相对路径
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * @return 资源完整URL
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @param url
     *            资源完整URL
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * @return 是否公开可读
     */
    public boolean isPublicReadable() {
        return this.publicReadable;
    }

    /**
     * @param publicReadable
     *            是否公开可读
     */
    public void setPublicReadable(final boolean publicReadable) {
        this.publicReadable = publicReadable;
    }

}

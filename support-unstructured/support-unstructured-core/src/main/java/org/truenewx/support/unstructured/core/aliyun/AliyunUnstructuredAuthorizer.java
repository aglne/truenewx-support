package org.truenewx.support.unstructured.core.aliyun;

import java.net.URL;
import java.util.Date;

import org.slf4j.LoggerFactory;
import org.truenewx.core.Strings;
import org.truenewx.core.util.DateUtil;
import org.truenewx.support.unstructured.core.UnstructuredAuthorizer;
import org.truenewx.support.unstructured.core.model.UnstructuredProvider;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectAcl;
import com.aliyun.oss.model.ObjectPermission;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse.Credentials;

/**
 * 阿里云的非结构化存储授权器
 *
 * @author jianglei
 *
 */
public class AliyunUnstructuredAuthorizer implements UnstructuredAuthorizer {

    private int tempReadExpiredSeconds = 30; // 默认30秒钟
    private AliyunAccount account;
    private AliyunPolicyBuilder policyBuilder;
    private AliyunStsRoleAssumer readStsRoleAssumer;

    /**
     * @param tempReadExpiredSeconds
     *            临时读取权限过期秒数
     */
    public void setTempReadExpiredSeconds(final int tempReadExpiredSeconds) {
        this.tempReadExpiredSeconds = tempReadExpiredSeconds;
    }

    /**
     * @param account
     *            阿里云账户信息
     */
    public void setAccount(final AliyunAccount account) {
        this.account = account;
    }

    /**
     * @param policyBuilder
     *            授权方针构建器
     */
    public void setPolicyBuilder(final AliyunPolicyBuilder policyBuilder) {
        this.policyBuilder = policyBuilder;
    }

    /**
     * @param readStsRoleAssumer
     *            读权限的STS临时角色假扮器
     */
    public void setReadStsRoleAssumer(final AliyunStsRoleAssumer readStsRoleAssumer) {
        this.readStsRoleAssumer = readStsRoleAssumer;
    }

    @Override
    public UnstructuredProvider getProvider() {
        return UnstructuredProvider.ALIYUN;
    }

    @Override
    public void authorizePublicRead(final String bucket, final String path) {
        // TODO 避免同样的路径反复多次申请公开读
        this.account.getOssClient().setObjectAcl(bucket, path, CannedAccessControlList.PublicRead);
    }

    private boolean isPublicRead(final String bucket, final String path) {
        final ObjectAcl acl = this.account.getOssClient().getObjectAcl(bucket, path);
        final ObjectPermission permission = acl.getPermission();
        return permission == ObjectPermission.PublicRead
                || permission == ObjectPermission.PublicReadWrite;
    }

    @Override
    public String getReadUrl(final String userKey, final String bucket, String path) {
        final int index = path.indexOf("?");
        String paramString = Strings.EMPTY;
        if (index > 0) {
            paramString = path.substring(index);
            path = path.substring(0, index);
        }
        try {
            if (isPublicRead(bucket, path)) {
                final StringBuffer url = new StringBuffer(bucket).append(Strings.DOT)
                        .append(this.account.getOssEndpoint()).append(Strings.SLASH).append(path);
                return url.toString() + paramString;
            } else { // 非公开可读的，授予临时读取权限
                final String policyDocument = this.policyBuilder.buildReadDocument(bucket, path);
                final Credentials credentials = this.readStsRoleAssumer.assumeRole(userKey,
                        policyDocument);
                if (credentials != null) {
                    final OSS oss = new OSSClient(this.account.getOssEndpoint(),
                            credentials.getAccessKeyId(), credentials.getAccessKeySecret(),
                            credentials.getSecurityToken());
                    final Date expiration = DateUtil.addSeconds(new Date(),
                            this.tempReadExpiredSeconds);
                    final URL url = oss.generatePresignedUrl(bucket, path, expiration);
                    return url.toString() + paramString;
                }
            }
        } catch (final Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return null;
    }

}

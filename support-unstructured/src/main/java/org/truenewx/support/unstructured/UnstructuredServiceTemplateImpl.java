package org.truenewx.support.unstructured;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.Strings;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.support.unstructured.model.UnstructuredAccess;
import org.truenewx.support.unstructured.model.UnstructuredProvider;
import org.truenewx.support.unstructured.model.UnstructuredWriteToken;

/**
 * 非结构化存储服务模版实现
 *
 * @author jianglei
 *
 */
public class UnstructuredServiceTemplateImpl<AT extends Enum<AT>, UT extends Enum<UT>, UK extends Serializable>
        implements UnstructuredServiceTemplate<AT, UT, UK>, ContextInitializedBean {

    private Map<AT, UnstructuredAuthorizePolicy<AT, UT, UK>> policies = new HashMap<>();
    private Map<UnstructuredProvider, UnstructuredAuthorizer> authorizers = new HashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        final Map<String, UnstructuredAuthorizePolicy> policies = context
                .getBeansOfType(UnstructuredAuthorizePolicy.class);
        for (final UnstructuredAuthorizePolicy<AT, UT, UK> policy : policies.values()) {
            this.policies.put(policy.getType(), policy);
        }

        final Map<String, UnstructuredAuthorizer> authorizers = context
                .getBeansOfType(UnstructuredAuthorizer.class);
        for (final UnstructuredAuthorizer authorizer : authorizers.values()) {
            this.authorizers.put(authorizer.getProvider(), authorizer);
        }
    }

    @Override
    public UnstructuredWriteToken authorizePrivateWrite(final AT authorizeType, final UK userId) {
        final UnstructuredAuthorizePolicy<AT, UT, UK> policy = this.policies.get(authorizeType);
        if (policy != null) {
            final UnstructuredProvider provider = policy.getProvider();
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);

            final String userKey = policy.getUserKey(userId);
            final String bucket = policy.getBucket(userId);
            String path = policy.getPath(userId, null);
            path = authorizer.standardizePath(path);
            final UnstructuredAccess access = authorizer.authorizePrivateWrite(userKey, bucket,
                    path);
            if (access != null) {
                final UnstructuredWriteToken token = new UnstructuredWriteToken();
                token.setAccessId(access.getAccessId());
                token.setAccessSecret(access.getAccessSecret());
                token.setProvider(provider);
                token.setHost(authorizer.getHost());
                token.setBucket(bucket);
                token.setPath(path);
                token.setInnerUrl(getInnerUrl(provider, bucket, path));
                token.setPublicReadable(policy.isPublicReadable());
                token.setRegion(authorizer.getRegion());
                return token;
            }
        }
        return null;
    }

    @Override
    public void authorizePublicRead(final AT authorizeType, final UK userId,
            final String filename) {
        final UnstructuredAuthorizePolicy<AT, UT, UK> policy = this.policies.get(authorizeType);
        if (policy != null) {
            final UnstructuredProvider provider = policy.getProvider();
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);

            final String bucket = policy.getBucket(userId);
            String path = policy.getPath(userId, filename);
            path = authorizer.standardizePath(path);
            authorizer.authorizePublicRead(bucket, path);
        }
    }

    private String getInnerUrl(final UnstructuredProvider provider, final String bucket,
            final String path) {
        // 形如：${proivder}://${bucket}/${path}
        final StringBuffer url = new StringBuffer(provider.name().toLowerCase()).append("://")
                .append(bucket);
        if (!path.startsWith(Strings.SLASH)) {
            url.append(Strings.SLASH);
        }
        url.append(path);
        return url.toString();
    }

    @Override
    public String getOuterUrl(final AT authorizeType, final UT userType, final UK userId,
            final String innerUrl, final String protocol) {
        int index1 = innerUrl.indexOf("://");
        if (index1 > 0) {
            final String innerProtocol = innerUrl.substring(0, index1);
            index1 += 3;
            final int index2 = innerUrl.indexOf(Strings.SLASH, index1);
            if (index2 > 0) {
                final String bucket = innerUrl.substring(index1, index2);
                String path = innerUrl.substring(index2);

                final UnstructuredProvider provider = UnstructuredProvider
                        .valueOf(innerProtocol.toUpperCase());

                final UnstructuredAuthorizePolicy<AT, UT, UK> policy = this.policies
                        .get(authorizeType);
                if (policy.isReadable(userType, userId, path)) { // 指定用户必须可去读该授权类型下的资源
                    final String userKey = policy.getUserKey(userId);
                    final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);
                    path = authorizer.standardizePath(path);
                    final String url = authorizer.getReadHttpUrl(userKey, bucket, path);
                    if (url != null) {
                        if (StringUtils.isBlank(protocol)) {
                            return url.replace("http://", "//");
                        } else if ("https".equalsIgnoreCase(protocol)) {
                            return url.replace("http://", "https://");
                        } else {
                            return url;
                        }
                    }
                }
            }
        }
        return null;
    }

}

package org.truenewx.support.sms.send;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.truenewx.core.Strings;
import org.truenewx.support.sms.SmsContentProvider;

/**
 * 短信发送器实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SmsSenderImpl implements SmsSender {
    private Map<String, SmsContentSender> contentSenders = new HashMap<>();
    private Map<String, SmsContentProvider> contentProviders = new HashMap<>();
    private boolean disabled;

    public void setContentSenders(final Map<String, SmsContentSender> contentSenders) {
        this.contentSenders = contentSenders;
    }

    public void setContentProviders(final List<SmsContentProvider> contentProviders) {
        for (final SmsContentProvider contentProvider : contentProviders) {
            this.contentProviders.put(contentProvider.getType(), contentProvider);
        }
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    private SmsContentProvider getContentProvider(final String type) {
        if (this.disabled) { // 禁用时始终返回空的内容提供者，以实际控制不发送短信
            return null;
        }
        SmsContentProvider contentSender = this.contentProviders.get(type);
        if (contentSender == null) {
            contentSender = this.contentProviders.get(Strings.ASTERISK); // 默认内容提供者
        }
        return contentSender;
    }

    private SmsContentSender getContentSender(final String type) {
        SmsContentSender contentSender = this.contentSenders.get(type);
        if (contentSender == null) {
            contentSender = this.contentSenders.get(Strings.ASTERISK); // 默认内容发送器
        }
        return contentSender;
    }

    @Override
    public SmsSendResult send(final String type, final Map<String, Object> params,
            final Locale locale, final String... mobilePhones) {
        final SmsContentProvider contentProvider = getContentProvider(type);
        if (contentProvider != null) {
            final String content = contentProvider.getContent(params, locale);
            if (content != null) {
                final SmsContentSender contentSender = getContentSender(type);
                if (contentSender != null) {
                    return contentSender.send(content, contentProvider.getMaxCount(), mobilePhones);
                }
            }
        }
        return null;
    }

    @Override
    public void send(final String type, final Map<String, Object> params, final Locale locale,
            final String[] mobilePhones, final SmsSendCallback callback) {
        final SmsContentProvider contentProvider = getContentProvider(type);
        if (contentProvider != null) {
            final String content = contentProvider.getContent(params, locale);
            if (content != null) {
                final SmsContentSender contentSender = getContentSender(type);
                if (contentSender != null) {
                    contentSender.send(content, contentProvider.getMaxCount(), mobilePhones,
                            callback);
                }
            }
        }
    }
}

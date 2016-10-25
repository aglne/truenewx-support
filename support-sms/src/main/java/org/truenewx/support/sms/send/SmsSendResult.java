package org.truenewx.support.sms.send;

import java.util.HashSet;
import java.util.Set;

import org.truenewx.support.sms.Sms;

/**
 * 短信发送结果
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class SmsSendResult {
    private Sms sms;
    private Set<String> failures;

    public SmsSendResult(final Sms sms) {
        this.sms = sms;
    }

    public Sms getSms() {
        return this.sms;
    }

    /**
     *
     * @return 发送失败的手机号码清单
     */
    public Set<String> getFailures() {
        if (this.failures == null) {
            this.failures = new HashSet<>();
        }
        return this.failures;
    }

    public void addFailures(final String... failures) {
        getFailures(); // 确保非空
        for (final String failure : failures) {
            this.failures.add(failure);
        }
    }

    /**
     *
     * @return 是否全部发送成功
     */
    public boolean isAllSuccessful() {
        return this.failures == null || this.failures.isEmpty();
    }
}

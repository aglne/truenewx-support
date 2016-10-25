package org.truenewx.support.sms;

import java.util.Locale;
import java.util.Map;

/**
 * 短信提供者
 *
 * @author jianglei
 * @since JDK 1.7
 */
public interface SmsProvider {
    /**
     *
     * @return 短信类型
     */
    String getType();

    /**
     *
     * @return 允许发送的最大条数，<=0时不限
     */
    int getMaxCount();

    /**
     * 根据指定参数映射集获取短信内容
     *
     * @param params
     *            参数映射集
     * @param locale
     *            TODO
     * @return 短信内容
     */
    String getContent(Map<String, Object> params, Locale locale);
}

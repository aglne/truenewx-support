package org.truenewx.support.sms.http;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HTTP短信发送策略
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface HttpSmsSendStrategy {

    /**
     * 获取请求URL
     *
     * @return 请求URL
     */
    String getUrl();

    /**
     * 获取请求方式
     *
     * @return 请求方式
     */
    String getRequestMethod();

    /**
     * 获取编码方式
     *
     * @return 编码方式
     */
    String getEncoding();

    /**
     *
     * @return 是否支持一次性向一个手机号码批量发送多条短信
     */
    boolean isBatchable();

    /**
     * 判断指定手机号码是否有效
     *
     * @param mobilePhone
     *            手机号码
     * @return 指定手机号码是否有效
     */
    boolean isValid(String mobilePhone);

    /**
     * 获取发送请求参数集
     *
     * @param contents
     *            短信内容清单，每一个内容为一条短信
     * @param index
     *            内容索引下标，支持批量发送时，传入小于0的值
     * @param mobilePhones
     *            手机号码集
     * @return 发送请求参数集
     */
    Map<String, Object> getParams(List<String> contents, int index, Set<String> mobilePhones);

    /**
     * 根据响应获取发送失败的手机号码清单
     *
     * @param statusCode
     *            响应状态码
     * @param content
     *            响应内容
     * @return 发送失败的手机号码清单
     */
    Set<String> getFailures(int statusCode, String content);
}

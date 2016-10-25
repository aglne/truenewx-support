package org.truenewx.support.sms;

import java.util.Date;
import java.util.List;

/**
 * 短信
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface Sms {

    /**
     *
     * @return 内容清单
     */
    List<String> getContents();

    /**
     *
     * @return 手机号码清单
     */
    String[] getMobilePhones();

    /**
     *
     * @return 发送时间
     */
    Date getSendTime();

    /**
     *
     * @return 接收时间
     */
    Date getReceiveTime();

}

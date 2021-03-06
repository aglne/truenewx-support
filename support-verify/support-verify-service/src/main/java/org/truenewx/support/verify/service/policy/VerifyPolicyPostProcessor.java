package org.truenewx.support.verify.service.policy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.truenewx.verify.data.model.VerifyUnity;

/**
 * 校验方针Bean提交处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class VerifyPolicyPostProcessor<U extends VerifyUnity<T>, T extends Enum<T>>
        implements BeanPostProcessor {

    private VerifyPolicyRegistrar<U, T> registrar;

    @Autowired(required = false)
    public void setRegistrar(final VerifyPolicyRegistrar<U, T> registrar) {
        this.registrar = registrar;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(final Object bean, final String beanName)
            throws BeansException {
        if (bean instanceof VerifyPolicy && this.registrar != null) {
            this.registrar.register((VerifyPolicy<U, T>) bean);
        }
        return bean;
    }

}

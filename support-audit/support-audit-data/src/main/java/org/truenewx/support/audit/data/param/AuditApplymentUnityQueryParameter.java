package org.truenewx.support.audit.data.param;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.truenewx.data.query.QueryParameterImpl;
import org.truenewx.support.audit.data.model.AuditStatus;

/**
 * 审核申请单体查询参数
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuditApplymentUnityQueryParameter extends QueryParameterImpl {

    private AuditStatus[] statuses;
    private Set<Integer> applicantIds;
    private Map<String, String> contentParams;

    public AuditStatus[] getStatuses() {
        return this.statuses;
    }

    public void setStatuses(final AuditStatus... statuses) {
        this.statuses = statuses;
    }

    public Set<Integer> getApplicantIds() {
        return this.applicantIds;
    }

    public void setApplicantIds(final Set<Integer> applicantIds) {
        this.applicantIds = applicantIds;
    }

    public Map<String, String> getContentParams() {
        return this.contentParams;
    }

    public void addContentParam(final String name, final String value) {
        if (this.contentParams == null) {
            this.contentParams = new HashMap<>();
        }
        this.contentParams.put(name, value);
    }

}

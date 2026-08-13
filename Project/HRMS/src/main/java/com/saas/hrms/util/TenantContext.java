package com.saas.hrms.util;

public class TenantContext {
	/*What is ThreadLocal?
      Think of it like a locker assigned to each request:
		Request from Company A gets locker A → stores companyId = 1
		Request from Company B gets locker B → stores companyId = 2
		They never see each other's locker — completely isolated per thread*/

    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        currentTenant.set(tenantId);
    }

    public static Long getTenantId() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
    
}
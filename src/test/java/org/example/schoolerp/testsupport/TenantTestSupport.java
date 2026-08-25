package org.example.schoolerp.testsupport;

import java.util.UUID;
import java.util.function.Supplier;

import org.example.schoolerp.security.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class TenantTestSupport {
   
    @Autowired
    protected PlatformTransactionManager platformTransactionManager;

    protected TransactionTemplate newTx() {
        TransactionTemplate tt = new TransactionTemplate(platformTransactionManager);
        tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return tt;
    }

    protected <T> T asTenant(UUID tenant, Supplier<T> action) {
        TenantContext.set(tenant);
        try {
            return newTx().execute(status -> action.get());
        } finally {
            TenantContext.clear();
        }
    }

    protected void asTenantVoid(UUID tenant, Runnable action) {
        asTenant(tenant, () -> { action.run(); return null;});
    }
}

package org.example.schoolerp.core;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.organization.Organization;
import org.hibernate.annotations.TenantId;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public abstract class OrganizationOwned extends Base {

  // Tenant discriminator. Keep synchronized with organization.
  @TenantId
  @Column(name = "organization_id", nullable = false, updatable = false)
  private UUID organizationId;

  // readonly association
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "organization_id", nullable = false, insertable = false, updatable = false)
  private Organization organization;
}

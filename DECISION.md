## Past Decisions
### No Independent User
Decision: User is always organization-owned.

Reason:
The application currently treats authentication and identity as
organization-scoped. A username only has meaning within an organization,
and all current use cases enter through an organization context.

Consequence:
There is no global User identity. This may need reconsideration if
we introduce a platform-level owner/control panel.

---

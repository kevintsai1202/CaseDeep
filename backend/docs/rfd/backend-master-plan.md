# Backend Master Plan & Architecture

**Document Version:** 1.0.0
**Last Updated:** 2025/1/20

## 1. Executive Summary

This document consolidates the backend development plans, architecture designs, and refactoring initiatives for the platform. It unifies the following previously separate RFDs:
*   Admin Features & Dashboard
*   Admin User Management
*   User Query & Statistics API
*   Industry & Order Template Query (Refined for Single Language)
*   Industry Translation Removal (Refactoring)
*   Revenue Share Architecture

The goal is to provide a single source of truth for the backend roadmap, covering User Management, Order Systems, and Financial features.

---

## 2. Admin User Management & Dashboard

### 2.1 Overview
The goal is to enhance the admin capabilities to manage users effectively, including sorting, role-based access control (RBAC) with industry scopes, and advanced querying/statistics.

### 2.2 Core User Management
*(Consolidated from `admin-user-management-backend-implementation-plan.md`)*

#### 2.2.1 User Display Order
*   **Requirement**: Admins need to manually order users for display purposes.
*   **Database**: Add `DISPLAY_ORDER` (int) to `T_USER`. Default to `ID`. Index on this column.
*   **API**:
    *   `PUT /api/admin/users/reorder`: Batch update display order.
    *   `PUT /api/admin/users/{userId}/move-up`: Move user up one position.
    *   `PUT /api/admin/users/{userId}/move-down`: Move user down one position.
    *   `GET /api/admin/users`: Update to sort by `displayOrder` ASC.

#### 2.2.2 Role Refinement
*   **Requirement**: More granular control over admin permissions.
*   **New Roles**:
    *   `ROLE_USER_MANAGE`: Manage users.
    *   `ROLE_PROMOTED_ORDER_MANAGE`: Manage promoted orders.
    *   `ROLE_DIRECT_ORDER_MANAGE`: Manage direct orders.
    *   `ROLE_CERTIFICATION_MANAGE`: Manage certifications.
*   **Entity Update**: Add `DESCRIPTION` to `T_ROLE`.

#### 2.2.3 Industry Scope Permissions
*   **Requirement**: Limit admin access to specific industries.
*   **Entity**: `AdminRoleIndustryScope`.
*   **API**:
    *   `GET /api/admin/users/{userId}/industry-scopes`: Get scopes for a user.
*   **Logic**: `IndustryScopeSecurityUtil` to check `hasAccessToIndustry` or `hasAccessToAllIndustries`.

### 2.3 Advanced User Query & Statistics
*(Consolidated from `user-query-api-implementation-plan.md`)*

#### 2.3.1 Search API
*   **Endpoint**: `GET /api/admin/users/search`
*   **Filters**:
    *   `industry`: "All Industries" or specific name.
        *   For **Providers**: Check `BusinessProfile.industry`.
        *   For **Clients**: Check `PersonProfile.industry`.
    *   `userType`: `PROVIDER`, `CLIENT`, or null (all).
    *   `createdInLastWeek`: Boolean filter (optional).
*   **Response**: Polymorphic list (`ProviderResponse` / `ClientResponse`) with relevant profile details and metrics.

#### 2.3.2 Statistics API
*   **Endpoint**: `GET /api/admin/users/statistics`
*   **Metrics**:
    *   Total User Count.
    *   Weekly New Users (e.g., "150 (+5)").
    *   Breakdown by **Industry** and **UserType**.

### 2.4 Admin Dashboard & Profile Enhancements
*(Consolidated from `admin-features-development-plan.md`)*

#### 2.4.1 Profile Location Support
*   **Requirement**: Support region-based filtering for dashboard stats.
*   **Database**:
    *   `T_BUSINESS_PROFILE`: Add `region` (varchar), `location` (varchar).
    *   `T_PERSON_PROFILE`: Add `region` (varchar), `location` (varchar).
*   **API**: Admin APIs should support filtering by `region`.

#### 2.4.2 Dashboard API
*   **Endpoint**: `GET /api/admin/dashboard/overview`
*   **Data**:
    *   Total Users, Active Users, Total Orders, Total Revenue.
    *   Charts: Order trends, User growth, Revenue trends.
    *   Top Providers list.
    *   Stats should be filtered by the Admin's `region` scope.

---

## 3. Industry & Order System

### 3.1 Order Template Query (Hierarchical)
*(Adapted from `implementation-plan-industry-ordertemplate.md` & `industry-translation-removal-plan.md`)*

**Constraint**: This feature implementation must adhere to the **Industry Translation Removal** decision (Section 3.3). All query logic should be based on **English** names only.

#### 3.1.1 Objectives
*   Query OrderTemplates based on Industry hierarchy.
*   Support hierarchical paths (Parent -> Child).
*   Filter by Region.

#### 3.1.2 API Endpoints
1.  **Get by Parent Industry**:
    *   `GET /api/industries/{parentIndustry}/ordertemplates`
    *   **Logic**: Find Industry by `name` (English). Collect all child industry IDs. Return templates for these IDs.
2.  **Get by Child Industry**:
    *   `GET /api/industries/{parentIndustry}/{childIndustry}/ordertemplates`
    *   **Logic**: Verify `childIndustry` is a child of `parentIndustry`. Return templates for the child ID.

#### 3.1.3 Implementation Details
*   **Repository**: `OrderTemplateRepository.findByCategoryIdInAndEnabledTrue`.
*   **Region Filter**: Filter templates based on `template.provider.region`.
*   **Refactoring Note**: Do NOT implement `locale` parameter. Use English names for URL path variables.

### 3.2 Offline Order Management
*(From `admin-features-development-plan.md`)*

#### 3.2.1 Overview
Allow admins to record orders that happened offline.

#### 3.2.2 Changes
*   **Enum**: Add `OFFLINE` to `OrderType`.
*   **API**:
    *   `POST /api/admin/orders/offline`: Create offline order.
    *   `GET /api/admin/orders/offline`: List offline orders.

### 3.3 Refactoring: Industry Translation Removal
*(From `industry-translation-removal-plan.md`)*

#### 3.3.1 Goal
Completely remove `IndustryTranslation` entity and related multi-language logic to simplify the system (English only).

#### 3.3.2 Execution Steps
1.  **Delete Files**:
    *   `IndustryTranslation.java` (Entity)
    *   `IndustryTranslationRepository.java`
    *   `IndustryTranslationRequest.java`, `IndustryTranslationResponse.java`
2.  **Clean Entity**:
    *   Remove `translations` list from `Industry.java`.
3.  **Clean Service/Controller**:
    *   Remove `locale` parameters from `IndustryController` and `IndustryService`.
    *   Remove `getAllTranslations` API.
    *   Refactor `IndustryService` to look up industries by `name` only, not translations.

---

## 4. Financial & Revenue System

### 4.1 Revenue Share Architecture
*(From `revenue-share-architecture-design.md`)*

#### 4.1.1 Overview
Automated system to calculate and record the platform's revenue share from orders.

#### 4.1.2 Entity Design: `RevenueShare`
*   **Table**: `T_REVENUE_SHARE`
*   **Fields**:
    *   `revenueShareNo`: "RS" + yyMM + random digits.
    *   `order`: Link to `Order`.
    *   `client`: Link to `User`.
    *   `industry`: Link to `Industry`.
    *   `revenueShareRate`: Calculated rate (Float).
    *   `orderAmount`: Total order value.
    *   `revenueShareAmount`: `orderAmount * revenueShareRate`.
    *   `status`: `Unpaid` / `Paid`.
    *   `paymentTime`: Timestamp.

#### 4.1.3 Rate Calculation Logic
*   **Base Rate**: `Industry.revenueShareRate`.
*   **Discount Rule**:
    *   Count client's previous orders in this industry (`N`).
    *   Rate = `BaseRate - ((N) * 2.5%)`.
    *   *Correction from original RFD*: Original said "(N-1)", but usually first order (N=0 previous) is base. Let's stick to: "1st order uses base. 2nd order (1 previous) uses base - 2.5%". So `Reduction = PreviousOrderCount * 0.025`.
    *   Minimum rate: 0%.

#### 4.1.4 Trigger Mechanism
*   **Event**: When `PaymentService` updates a `PaymentCard` to `Paid`.
*   **Check**: Is this the *first* payment for this Order?
*   **Action**: If yes, call `RevenueShareService.createRevenueShare(order)`.

#### 4.1.5 APIs
*   `GET /api/revenue-shares`: List with filters (Client, Status).
*   `GET /api/revenue-shares/stats`: Unpaid vs Paid totals.
*   `PUT /api/revenue-shares/{id}/status`: Update status (Admin/System).

---

## 5. Summary of API Changes

| Module | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Admin User** | PUT | `/api/admin/users/reorder` | Batch reorder users |
| **Admin User** | GET | `/api/admin/users/search` | Advanced search (Industry, UserType) |
| **Admin User** | GET | `/api/admin/users/statistics` | User stats (Total, Weekly New) |
| **Industry** | GET | `/api/industries/{parent}/ordertemplates` | Get templates by parent industry |
| **Industry** | GET | `/api/industries` | **Changed**: Removed `locale` param |
| **Revenue** | GET | `/api/revenue-shares` | List revenue shares |
| **Revenue** | GET | `/api/revenue-shares/stats` | Financial stats |

## 6. Implementation Roadmap

1.  **Phase 1: Cleanup & Refactoring**
    *   Execute **Industry Translation Removal**.
    *   Add `region`/`location` to Profiles.

2.  **Phase 2: Admin Core**
    *   Implement User Display Order & Roles.
    *   Implement Advanced User Query & Stats.

3.  **Phase 3: Industry & Orders**
    *   Implement Hierarchical Order Template Query.
    *   Implement Offline Orders.

4.  **Phase 4: Financials**
    *   Implement `RevenueShare` entity and service.
    *   Integrate with `PaymentService`.
    *   Implement Dashboard Stats APIs.

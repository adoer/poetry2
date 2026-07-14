## 1. Backend: Admin Controllers & Services

- [x] 1.1 Create `controller/admin/AdminPoesyController.java` — poem list, detail, create, update, delete
- [x] 1.2 Create `controller/admin/AdminQuotesController.java` — quote list, detail, create, update, delete
- [x] 1.3 Create `controller/admin/AdminAuthorsController.java` — author list, detail, create, update, delete
- [x] 1.4 Create `controller/admin/AdminUserController.java` — user list, detail (read-only)
- [x] 1.5 Create `controller/admin/AdminStatsController.java` — overview, registration trend, favorite distribution
- [x] 1.6 Create `service/AdminPoesyService.java` — poem CRUD logic with max(id)+1 ID generation
- [x] 1.7 Create `service/AdminQuotesService.java` — quote CRUD logic with max(id)+1 ID generation
- [x] 1.8 Create `service/AdminAuthorsService.java` — author CRUD logic with max(id)+1 ID generation
- [x] 1.9 Create `service/AdminUserService.java` — user lookup and search
- [x] 1.10 Create `service/AdminStatsService.java` — aggregate JPQL queries for counts, registration trend, favorite distribution

## 2. Frontend: Admin API Layer

- [x] 2.1 Create `api/admin.ts` — all admin API calls (poesy, quotes, authors, users, stats)
- [x] 2.2 Add user-related types to `types/index.ts` (UserItem, StatsOverview, etc.)

## 3. Frontend: Admin Layout & Routing

- [x] 3.1 Update `layouts/AdminLayout.vue` — add sidebar menu items for all admin modules
- [x] 3.2 Update `router/index.ts` — add admin child routes for all module pages

## 4. Frontend: Dashboard Enhancement

- [x] 4.1 Enhance `views/admin/Dashboard.vue` — add stat cards (poems, quotes, authors, users count)

## 5. Frontend: Poem Management Pages

- [x] 5.1 Create `views/admin/poesy/PoesyList.vue` — paginated table with search and delete
- [x] 5.2 Create `views/admin/poesy/PoesyEdit.vue` — create/edit form with type selector (el-select allow-create)

## 6. Frontend: Quote Management Pages

- [x] 6.1 Create `views/admin/quotes/QuotesList.vue` — paginated table with search and delete
- [x] 6.2 Create `views/admin/quotes/QuotesEdit.vue` — create/edit form

## 7. Frontend: Author Management Pages

- [x] 7.1 Create `views/admin/authors/AuthorList.vue` — paginated table with search and delete
- [x] 7.2 Create `views/admin/authors/AuthorEdit.vue` — create/edit form

## 8. Frontend: User Management Page

- [x] 8.1 Create `views/admin/users/UserList.vue` — paginated read-only table with search and detail dialog

## 9. Frontend: Statistics Page

- [x] 9.1 Create `views/admin/stats/Stats.vue` — overview cards, registration trend chart, favorite distribution chart

## 10. Verification

- [x] 10.1 Verify backend compiles and all admin endpoints respond correctly
- [x] 10.2 Verify frontend builds without TypeScript errors

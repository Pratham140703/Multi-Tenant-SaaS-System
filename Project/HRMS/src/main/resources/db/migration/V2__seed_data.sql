-- ── 1. Super Admin (no company) ───────────────────────────
-- Password: superadmin@123
INSERT INTO public.users (email, name, password, role, is_active, created_at, company_id)
VALUES (
    'superadmin@hrms.com',
    'Super Admin',
    '$2b$10$d3U.5gzrnPBYZq7rpwX0OuOmME/HLhidSMGdgYIMwviiwnn6gMJzi',
    'SUPER_ADMIN',
    true,
    NOW(),
    NULL
);

-- ── 2. Companies ──────────────────────────────────────────
INSERT INTO public.companies (name, email, phone, is_active, plan_type, created_at)
VALUES
    ('TechCorp Pvt Ltd',   'contact@techcorp.com', '9876543210', true, 'PRO',        NOW()),
    ('FinServe Solutions',  'contact@finserve.com', '9876540000', true, 'ENTERPRISE', NOW());

-- ── 3. Departments ────────────────────────────────────────
-- TechCorp (company_id = 1)
INSERT INTO public.departments (name, description, is_active, company_id, created_at)
VALUES
    ('Engineering', 'Software development team', true, 1, NOW()),
    ('HR',          'Human resources team',       true, 1, NOW()),
    ('Finance',     'Finance and accounts team',  true, 1, NOW());

-- FinServe (company_id = 2)
INSERT INTO public.departments (name, description, is_active, company_id, created_at)
VALUES
    ('Operations',  'Operations team',           true, 2, NOW()),
    ('HR',          'Human resources team',      true, 2, NOW()),
    ('Compliance',  'Legal and compliance team', true, 2, NOW());

-- ── 4. HR Admin & Manager — TechCorp ─────────────────────
-- hradmin@123 / hrmanager@123
INSERT INTO public.users (email, name, password, role, is_active, created_at, company_id)
VALUES
    ('hradmin@techcorp.com',   'HR Admin TC',   '$2b$10$yJhzkuYopBuuH/IzPU9J1ufR8zvsHsKssXRBxPsC/5.iElOXdsfBO', 'HR_ADMIN',   true, NOW(), 1),
    ('hrmanager@techcorp.com', 'HR Manager TC', '$2b$10$wS7LIgQE5QhkRqBYt8KQXud8njZ7N0juRmYMgwgCCpd3l3EpOVDvm', 'HR_MANAGER', true, NOW(), 1);

-- ── 5. HR Admin & Manager — FinServe ─────────────────────
-- fsadmin@123 / fsmanager@123
INSERT INTO public.users (email, name, password, role, is_active, created_at, company_id)
VALUES
    ('hradmin@finserve.com',   'HR Admin FS',   '$2b$10$ZZH5EZvw0VF6hXCmZUUuXewqEpPloq8qIDn81tcmlx5gsLeAoS3TW', 'HR_ADMIN',   true, NOW(), 2),
    ('hrmanager@finserve.com', 'HR Manager FS', '$2b$10$J.RiW6tPwNjtLTBbchUV/.IKSw63/oq4Y/VZtHMrvbqV./8gtUw6O', 'HR_MANAGER', true, NOW(), 2);

-- ── 6. Employees — TechCorp (company_id=1) ───────────────
INSERT INTO public.employees (first_name, last_name, email, employee_code, designation, phone, monthly_salary, status, joining_date, company_id, department_id, reporting_manager_id, created_at)
VALUES
    ('Alice', 'Johnson',  'alice@techcorp.com', 'TC001', 'Software Engineer', '9000000001', 75000.00, 'ACTIVE', '2024-01-15', 1, 1, NULL, NOW()),
    ('Bob',   'Smith',    'bob@techcorp.com',   'TC002', 'Backend Developer', '9000000002', 65000.00, 'ACTIVE', '2024-03-01', 1, 1, NULL, NOW()),
    ('Carol', 'Williams', 'carol@techcorp.com', 'TC003', 'Finance Analyst',   '9000000003', 60000.00, 'ACTIVE', '2024-06-01', 1, 3, NULL, NOW());

-- ── 7. Employees — FinServe (company_id=2) ───────────────
-- department_ids: 4=Operations, 5=HR, 6=Compliance
INSERT INTO public.employees (first_name, last_name, email, employee_code, designation, phone, monthly_salary, status, joining_date, company_id, department_id, reporting_manager_id, created_at)
VALUES
    ('David', 'Brown',  'david@finserve.com', 'FS001', 'Operations Lead',    '9000000004', 80000.00, 'ACTIVE', '2023-11-01', 2, 4, NULL, NOW()),
    ('Eva',   'Green',  'eva@finserve.com',   'FS002', 'Compliance Officer', '9000000005', 70000.00, 'ACTIVE', '2023-12-15', 2, 6, NULL, NOW()),
    ('Frank', 'Miller', 'frank@finserve.com', 'FS003', 'HR Executive',       '9000000006', 55000.00, 'ACTIVE', '2024-02-01', 2, 5, NULL, NOW());

-- ── 8. Employee Users — TechCorp ─────────────────────────
-- alice@123 / bob@123 / carol@123
INSERT INTO public.users (email, name, password, role, is_active, created_at, company_id)
VALUES
    ('alice@techcorp.com', 'Alice Johnson',  '$2b$10$nwqqxZW76Pz0pa8ITwoTGeR6LIGzOhOudM3CJqphQ9i73VtTFmCxS', 'EMPLOYEE', true, NOW(), 1),
    ('bob@techcorp.com',   'Bob Smith',      '$2b$10$JZAZzKyfngw3xjH0ZA0pZebt5k9gEDuZPnqUfJksB23fwdD.QBzLG', 'EMPLOYEE', true, NOW(), 1),
    ('carol@techcorp.com', 'Carol Williams', '$2b$10$kx7EBSyhkXTiClZsmzcp4uNoPb1yyV6ShxxI.GWZqnQlqe7m3IY2e', 'EMPLOYEE', true, NOW(), 1);

-- ── 9. Employee Users — FinServe ─────────────────────────
-- david@123 / eva@123 / frank@123
INSERT INTO public.users (email, name, password, role, is_active, created_at, company_id)
VALUES
    ('david@finserve.com', 'David Brown',  '$2b$10$lBb7yq1tN2M85aR4yxHqFuJx6W1.IMzT.A59bWooO1CHmnPLNeDQe', 'EMPLOYEE', true, NOW(), 2),
    ('eva@finserve.com',   'Eva Green',    '$2b$10$NgqF0xT9CJ3qVY1FA8fWGu1dfyH3kwgpqghuC24XYcu3B9Vqjraoe', 'EMPLOYEE', true, NOW(), 2),
    ('frank@finserve.com', 'Frank Miller', '$2b$10$OizTzZLtXDQoWRgDFIlkW.5cU02a6S6JwX5bNxQBwrd6OEpBPdwWW', 'EMPLOYEE', true, NOW(), 2);

-- ── 10. Leave Types — TechCorp ───────────────────────────
INSERT INTO public.leave_types (name, description, max_days_per_year, is_active, company_id, created_at)
VALUES
    ('Casual Leave', 'For personal or casual reasons', 12, true, 1, NOW()),
    ('Sick Leave',   'For medical or health reasons',  10, true, 1, NOW()),
    ('Annual Leave', 'Yearly paid leave',              15, true, 1, NOW());

-- ── 11. Leave Types — FinServe ───────────────────────────
INSERT INTO public.leave_types (name, description, max_days_per_year, is_active, company_id, created_at)
VALUES
    ('Casual Leave', 'For personal or casual reasons', 10, true, 2, NOW()),
    ('Sick Leave',   'For medical or health reasons',  12, true, 2, NOW()),
    ('Annual Leave', 'Yearly paid leave',              18, true, 2, NOW());

-- ── 12. Leave Balances 2026 — TechCorp (employee_ids 1,2,3) ─
-- leave_type_ids: 1=TC Casual, 2=TC Sick, 3=TC Annual
INSERT INTO public.leave_balances (employee_id, leave_type_id, year, total_days, used_days, remaining_days)
VALUES
    (1, 1, 2026, 12, 2, 10),
    (1, 2, 2026, 10, 0, 10),
    (1, 3, 2026, 15, 5, 10),
    (2, 1, 2026, 12, 0, 12),
    (2, 2, 2026, 10, 3,  7),
    (2, 3, 2026, 15, 0, 15),
    (3, 1, 2026, 12, 1, 11),
    (3, 2, 2026, 10, 0, 10),
    (3, 3, 2026, 15, 2, 13);

-- ── 13. Leave Balances 2026 — FinServe (employee_ids 4,5,6) ─
-- leave_type_ids: 4=FS Casual, 5=FS Sick, 6=FS Annual
INSERT INTO public.leave_balances (employee_id, leave_type_id, year, total_days, used_days, remaining_days)
VALUES
    (4, 4, 2026, 10, 0, 10),
    (4, 5, 2026, 12, 1, 11),
    (4, 6, 2026, 18, 3, 15),
    (5, 4, 2026, 10, 2,  8),
    (5, 5, 2026, 12, 0, 12),
    (5, 6, 2026, 18, 0, 18),
    (6, 4, 2026, 10, 0, 10),
    (6, 5, 2026, 12, 4,  8),
    (6, 6, 2026, 18, 1, 17);

-- ── 14. Attendance June 2026 — TechCorp ──────────────────
-- Alice: 20 present, 1 absent, 1 half day
INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (1,1,'2026-06-01','PRESENT', true,NOW()), (1,1,'2026-06-02','PRESENT', true,NOW()),
    (1,1,'2026-06-03','PRESENT', true,NOW()), (1,1,'2026-06-04','PRESENT', true,NOW()),
    (1,1,'2026-06-05','PRESENT', true,NOW()), (1,1,'2026-06-08','PRESENT', true,NOW()),
    (1,1,'2026-06-09','PRESENT', true,NOW()), (1,1,'2026-06-10','PRESENT', true,NOW()),
    (1,1,'2026-06-11','PRESENT', true,NOW()), (1,1,'2026-06-12','PRESENT', true,NOW()),
    (1,1,'2026-06-15','PRESENT', true,NOW()), (1,1,'2026-06-16','PRESENT', true,NOW()),
    (1,1,'2026-06-17','PRESENT', true,NOW()), (1,1,'2026-06-18','PRESENT', true,NOW()),
    (1,1,'2026-06-19','PRESENT', true,NOW()), (1,1,'2026-06-22','PRESENT', true,NOW()),
    (1,1,'2026-06-23','PRESENT', true,NOW()), (1,1,'2026-06-24','PRESENT', true,NOW()),
    (1,1,'2026-06-25','PRESENT', true,NOW()), (1,1,'2026-06-26','PRESENT', true,NOW()),
    (1,1,'2026-06-29','ABSENT',  true,NOW()), (1,1,'2026-06-30','HALF_DAY',true,NOW());

-- Bob: 18 present, 2 absent, 2 half day
INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (2,1,'2026-06-01','PRESENT', true,NOW()), (2,1,'2026-06-02','PRESENT', true,NOW()),
    (2,1,'2026-06-03','PRESENT', true,NOW()), (2,1,'2026-06-04','PRESENT', true,NOW()),
    (2,1,'2026-06-05','ABSENT',  true,NOW()), (2,1,'2026-06-08','PRESENT', true,NOW()),
    (2,1,'2026-06-09','PRESENT', true,NOW()), (2,1,'2026-06-10','PRESENT', true,NOW()),
    (2,1,'2026-06-11','HALF_DAY',true,NOW()), (2,1,'2026-06-12','PRESENT', true,NOW()),
    (2,1,'2026-06-15','PRESENT', true,NOW()), (2,1,'2026-06-16','PRESENT', true,NOW()),
    (2,1,'2026-06-17','PRESENT', true,NOW()), (2,1,'2026-06-18','PRESENT', true,NOW()),
    (2,1,'2026-06-19','ABSENT',  true,NOW()), (2,1,'2026-06-22','PRESENT', true,NOW()),
    (2,1,'2026-06-23','PRESENT', true,NOW()), (2,1,'2026-06-24','PRESENT', true,NOW()),
    (2,1,'2026-06-25','HALF_DAY',true,NOW()), (2,1,'2026-06-26','PRESENT', true,NOW()),
    (2,1,'2026-06-29','PRESENT', true,NOW()), (2,1,'2026-06-30','PRESENT', true,NOW());

-- Carol: 21 present, 1 absent
INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (3,1,'2026-06-01','PRESENT',true,NOW()), (3,1,'2026-06-02','PRESENT',true,NOW()),
    (3,1,'2026-06-03','PRESENT',true,NOW()), (3,1,'2026-06-04','PRESENT',true,NOW()),
    (3,1,'2026-06-05','PRESENT',true,NOW()), (3,1,'2026-06-08','PRESENT',true,NOW()),
    (3,1,'2026-06-09','PRESENT',true,NOW()), (3,1,'2026-06-10','PRESENT',true,NOW()),
    (3,1,'2026-06-11','PRESENT',true,NOW()), (3,1,'2026-06-12','PRESENT',true,NOW()),
    (3,1,'2026-06-15','PRESENT',true,NOW()), (3,1,'2026-06-16','PRESENT',true,NOW()),
    (3,1,'2026-06-17','PRESENT',true,NOW()), (3,1,'2026-06-18','PRESENT',true,NOW()),
    (3,1,'2026-06-19','PRESENT',true,NOW()), (3,1,'2026-06-22','PRESENT',true,NOW()),
    (3,1,'2026-06-23','PRESENT',true,NOW()), (3,1,'2026-06-24','PRESENT',true,NOW()),
    (3,1,'2026-06-25','PRESENT',true,NOW()), (3,1,'2026-06-26','PRESENT',true,NOW()),
    (3,1,'2026-06-29','ABSENT', true,NOW()), (3,1,'2026-06-30','PRESENT',true,NOW());

-- ── 15. Attendance June 2026 — FinServe ──────────────────
-- David: 21 present, 1 absent
INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (4,2,'2026-06-01','PRESENT',true,NOW()), (4,2,'2026-06-02','PRESENT',true,NOW()),
    (4,2,'2026-06-03','PRESENT',true,NOW()), (4,2,'2026-06-04','PRESENT',true,NOW()),
    (4,2,'2026-06-05','PRESENT',true,NOW()), (4,2,'2026-06-08','PRESENT',true,NOW()),
    (4,2,'2026-06-09','PRESENT',true,NOW()), (4,2,'2026-06-10','PRESENT',true,NOW()),
    (4,2,'2026-06-11','PRESENT',true,NOW()), (4,2,'2026-06-12','PRESENT',true,NOW()),
    (4,2,'2026-06-15','PRESENT',true,NOW()), (4,2,'2026-06-16','PRESENT',true,NOW()),
    (4,2,'2026-06-17','PRESENT',true,NOW()), (4,2,'2026-06-18','PRESENT',true,NOW()),
    (4,2,'2026-06-19','PRESENT',true,NOW()), (4,2,'2026-06-22','PRESENT',true,NOW()),
    (4,2,'2026-06-23','PRESENT',true,NOW()), (4,2,'2026-06-24','PRESENT',true,NOW()),
    (4,2,'2026-06-25','PRESENT',true,NOW()), (4,2,'2026-06-26','PRESENT',true,NOW()),
    (4,2,'2026-06-29','ABSENT', true,NOW()), (4,2,'2026-06-30','PRESENT',true,NOW());

-- Eva: 19 present, 2 absent, 1 half day
INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (5,2,'2026-06-01','PRESENT', true,NOW()), (5,2,'2026-06-02','PRESENT', true,NOW()),
    (5,2,'2026-06-03','ABSENT',  true,NOW()), (5,2,'2026-06-04','PRESENT', true,NOW()),
    (5,2,'2026-06-05','PRESENT', true,NOW()), (5,2,'2026-06-08','PRESENT', true,NOW()),
    (5,2,'2026-06-09','PRESENT', true,NOW()), (5,2,'2026-06-10','HALF_DAY',true,NOW()),
    (5,2,'2026-06-11','PRESENT', true,NOW()), (5,2,'2026-06-12','PRESENT', true,NOW()),
    (5,2,'2026-06-15','PRESENT', true,NOW()), (5,2,'2026-06-16','PRESENT', true,NOW()),
    (5,2,'2026-06-17','PRESENT', true,NOW()), (5,2,'2026-06-18','PRESENT', true,NOW()),
    (5,2,'2026-06-19','ABSENT',  true,NOW()), (5,2,'2026-06-22','PRESENT', true,NOW()),
    (5,2,'2026-06-23','PRESENT', true,NOW()), (5,2,'2026-06-24','PRESENT', true,NOW()),
    (5,2,'2026-06-25','PRESENT', true,NOW()), (5,2,'2026-06-26','PRESENT', true,NOW()),
    (5,2,'2026-06-29','PRESENT', true,NOW()), (5,2,'2026-06-30','PRESENT', true,NOW());

-- Frank: 20 present, 1 absent, 1 half day
INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (6,2,'2026-06-01','PRESENT', true,NOW()), (6,2,'2026-06-02','PRESENT', true,NOW()),
    (6,2,'2026-06-03','PRESENT', true,NOW()), (6,2,'2026-06-04','PRESENT', true,NOW()),
    (6,2,'2026-06-05','PRESENT', true,NOW()), (6,2,'2026-06-08','HALF_DAY',true,NOW()),
    (6,2,'2026-06-09','PRESENT', true,NOW()), (6,2,'2026-06-10','PRESENT', true,NOW()),
    (6,2,'2026-06-11','PRESENT', true,NOW()), (6,2,'2026-06-12','PRESENT', true,NOW()),
    (6,2,'2026-06-15','PRESENT', true,NOW()), (6,2,'2026-06-16','PRESENT', true,NOW()),
    (6,2,'2026-06-17','PRESENT', true,NOW()), (6,2,'2026-06-18','ABSENT',  true,NOW()),
    (6,2,'2026-06-19','PRESENT', true,NOW()), (6,2,'2026-06-22','PRESENT', true,NOW()),
    (6,2,'2026-06-23','PRESENT', true,NOW()), (6,2,'2026-06-24','PRESENT', true,NOW()),
    (6,2,'2026-06-25','PRESENT', true,NOW()), (6,2,'2026-06-26','PRESENT', true,NOW()),
    (6,2,'2026-06-29','PRESENT', true,NOW()), (6,2,'2026-06-30','PRESENT', true,NOW());

-- ── 16. Attendance July 2026 — TechCorp ──────────────────
INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (1,1,'2026-07-01','PRESENT', true,NOW()), (1,1,'2026-07-02','PRESENT', true,NOW()),
    (1,1,'2026-07-03','PRESENT', true,NOW()), (1,1,'2026-07-04','PRESENT', true,NOW()),
    (1,1,'2026-07-07','PRESENT', true,NOW()), (1,1,'2026-07-08','ABSENT',  true,NOW()),
    (1,1,'2026-07-09','PRESENT', true,NOW()), (1,1,'2026-07-10','PRESENT', true,NOW()),
    (1,1,'2026-07-11','HALF_DAY',true,NOW()), (1,1,'2026-07-14','PRESENT', true,NOW()),
    (1,1,'2026-07-15','PRESENT', true,NOW()), (1,1,'2026-07-16','PRESENT', true,NOW()),
    (1,1,'2026-07-17','PRESENT', true,NOW()), (1,1,'2026-07-18','PRESENT', true,NOW());

INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (2,1,'2026-07-01','PRESENT',true,NOW()), (2,1,'2026-07-02','PRESENT',true,NOW()),
    (2,1,'2026-07-03','ABSENT', true,NOW()), (2,1,'2026-07-04','PRESENT',true,NOW()),
    (2,1,'2026-07-07','PRESENT',true,NOW()), (2,1,'2026-07-08','PRESENT',true,NOW()),
    (2,1,'2026-07-09','PRESENT',true,NOW()), (2,1,'2026-07-10','PRESENT',true,NOW()),
    (2,1,'2026-07-11','PRESENT',true,NOW()), (2,1,'2026-07-14','PRESENT',true,NOW()),
    (2,1,'2026-07-15','PRESENT',true,NOW()), (2,1,'2026-07-16','PRESENT',true,NOW()),
    (2,1,'2026-07-17','PRESENT',true,NOW()), (2,1,'2026-07-18','PRESENT',true,NOW());

INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (3,1,'2026-07-01','PRESENT',true,NOW()), (3,1,'2026-07-02','PRESENT',true,NOW()),
    (3,1,'2026-07-03','PRESENT',true,NOW()), (3,1,'2026-07-04','PRESENT',true,NOW()),
    (3,1,'2026-07-07','PRESENT',true,NOW()), (3,1,'2026-07-08','PRESENT',true,NOW()),
    (3,1,'2026-07-09','PRESENT',true,NOW()), (3,1,'2026-07-10','PRESENT',true,NOW()),
    (3,1,'2026-07-11','PRESENT',true,NOW()), (3,1,'2026-07-14','PRESENT',true,NOW()),
    (3,1,'2026-07-15','PRESENT',true,NOW()), (3,1,'2026-07-16','PRESENT',true,NOW()),
    (3,1,'2026-07-17','PRESENT',true,NOW()), (3,1,'2026-07-18','PRESENT',true,NOW());

-- ── 17. Attendance July 2026 — FinServe ──────────────────
INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (4,2,'2026-07-01','PRESENT',true,NOW()), (4,2,'2026-07-02','PRESENT',true,NOW()),
    (4,2,'2026-07-03','PRESENT',true,NOW()), (4,2,'2026-07-04','PRESENT',true,NOW()),
    (4,2,'2026-07-07','PRESENT',true,NOW()), (4,2,'2026-07-08','PRESENT',true,NOW()),
    (4,2,'2026-07-09','PRESENT',true,NOW()), (4,2,'2026-07-10','PRESENT',true,NOW()),
    (4,2,'2026-07-11','PRESENT',true,NOW()), (4,2,'2026-07-14','PRESENT',true,NOW()),
    (4,2,'2026-07-15','PRESENT',true,NOW()), (4,2,'2026-07-16','PRESENT',true,NOW()),
    (4,2,'2026-07-17','PRESENT',true,NOW()), (4,2,'2026-07-18','PRESENT',true,NOW());

INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (5,2,'2026-07-01','PRESENT', true,NOW()), (5,2,'2026-07-02','PRESENT', true,NOW()),
    (5,2,'2026-07-03','PRESENT', true,NOW()), (5,2,'2026-07-04','ABSENT',  true,NOW()),
    (5,2,'2026-07-07','PRESENT', true,NOW()), (5,2,'2026-07-08','PRESENT', true,NOW()),
    (5,2,'2026-07-09','PRESENT', true,NOW()), (5,2,'2026-07-10','PRESENT', true,NOW()),
    (5,2,'2026-07-11','PRESENT', true,NOW()), (5,2,'2026-07-14','PRESENT', true,NOW()),
    (5,2,'2026-07-15','PRESENT', true,NOW()), (5,2,'2026-07-16','PRESENT', true,NOW()),
    (5,2,'2026-07-17','PRESENT', true,NOW()), (5,2,'2026-07-18','PRESENT', true,NOW());

INSERT INTO public.attendance (employee_id, company_id, attendance_date, status, marked_by_hr, created_at)
VALUES
    (6,2,'2026-07-01','PRESENT',  true,NOW()), (6,2,'2026-07-02','PRESENT',true,NOW()),
    (6,2,'2026-07-03','PRESENT',  true,NOW()), (6,2,'2026-07-04','PRESENT',true,NOW()),
    (6,2,'2026-07-07','HALF_DAY', true,NOW()), (6,2,'2026-07-08','PRESENT',true,NOW()),
    (6,2,'2026-07-09','PRESENT',  true,NOW()), (6,2,'2026-07-10','PRESENT',true,NOW()),
    (6,2,'2026-07-11','PRESENT',  true,NOW()), (6,2,'2026-07-14','PRESENT',true,NOW()),
    (6,2,'2026-07-15','PRESENT',  true,NOW()), (6,2,'2026-07-16','PRESENT',true,NOW()),
    (6,2,'2026-07-17','PRESENT',  true,NOW()), (6,2,'2026-07-18','PRESENT',true,NOW());

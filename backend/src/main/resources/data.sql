-- Insert new role data
-- Insert only if role does not exist to avoid duplicates

-- Insert ROLE_ADMIN role
INSERT INTO t_role (ROLENAME, DESCRIPTION, ENABLED, CREATE_TIME, UPDATE_TIME)
SELECT 'ROLE_ADMIN', 'System administrator role with highest system privileges', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_role WHERE ROLENAME = 'ROLE_ADMIN');

-- Insert ROLE_USER role
INSERT INTO t_role (ROLENAME, DESCRIPTION, ENABLED, CREATE_TIME, UPDATE_TIME)
SELECT 'ROLE_USER', 'General user role with basic system access permissions', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_role WHERE ROLENAME = 'ROLE_USER');

-- Insert ROLE_USER_MANAGE role
INSERT INTO t_role (ROLENAME, DESCRIPTION, ENABLED, CREATE_TIME, UPDATE_TIME)
SELECT 'ROLE_USER_MANAGE', 'User management role responsible for managing system user accounts', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_role WHERE ROLENAME = 'ROLE_USER_MANAGE');

-- Insert ROLE_PROMOTED_ORDER_MANAGE role
INSERT INTO t_role (ROLENAME, DESCRIPTION, ENABLED, CREATE_TIME, UPDATE_TIME)
SELECT 'ROLE_PROMOTED_ORDER_MANAGE', 'Promoted order management role responsible for managing promotion-related orders', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_role WHERE ROLENAME = 'ROLE_PROMOTED_ORDER_MANAGE');

-- Insert ROLE_DIRECT_ORDER_MANAGE role
INSERT INTO t_role (ROLENAME, DESCRIPTION, ENABLED, CREATE_TIME, UPDATE_TIME)
SELECT 'ROLE_DIRECT_ORDER_MANAGE', 'Direct order management role responsible for managing direct order business', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_role WHERE ROLENAME = 'ROLE_DIRECT_ORDER_MANAGE');

-- Insert ROLE_CERTIFICATION_MANAGE role
INSERT INTO t_role (ROLENAME, DESCRIPTION, ENABLED, CREATE_TIME, UPDATE_TIME)
SELECT 'ROLE_CERTIFICATION_MANAGE', 'Certification management role responsible for managing user certification business', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_role WHERE ROLENAME = 'ROLE_CERTIFICATION_MANAGE');

-- Insert admin user
-- Password: Abc123 (encoded with BCrypt)
INSERT INTO t_user (USERNAME, PASSWORD, EMAIL, SEND_VERIFY_FLAG, SEND_RESET_FLAG, CERTIFIED, LOCKED, USER_TYPE, RANKING_SCORE, DISPLAY_ORDER, ENABLED, CREATE_TIME, UPDATE_TIME)
SELECT 'admin', '$2a$10$WpjtzpaCJAFaL2jfXvCtSOK2dpj0cJZbJdg3U9nTbuheOebuzYEfm', 'admin@casemgr.com', false, false, true, false, 'ADMIN', 0.0, 0, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE USERNAME = 'admin');

-- Assign all roles to admin user
-- Get admin user ID and assign ROLE_ADMIN
INSERT INTO t_user_role (USER_ID, ROLE_ID)
SELECT u.ID, r.ID
FROM t_user u, t_role r
WHERE u.USERNAME = 'admin' AND r.ROLENAME = 'ROLE_ADMIN'
AND NOT EXISTS (SELECT 1 FROM t_user_role ur WHERE ur.USER_ID = u.ID AND ur.ROLE_ID = r.ID);

-- Assign ROLE_USER to admin
INSERT INTO t_user_role (USER_ID, ROLE_ID)
SELECT u.ID, r.ID
FROM t_user u, t_role r
WHERE u.USERNAME = 'admin' AND r.ROLENAME = 'ROLE_USER'
AND NOT EXISTS (SELECT 1 FROM t_user_role ur WHERE ur.USER_ID = u.ID AND ur.ROLE_ID = r.ID);

-- Assign ROLE_USER_MANAGE to admin
INSERT INTO t_user_role (USER_ID, ROLE_ID)
SELECT u.ID, r.ID
FROM t_user u, t_role r
WHERE u.USERNAME = 'admin' AND r.ROLENAME = 'ROLE_USER_MANAGE'
AND NOT EXISTS (SELECT 1 FROM t_user_role ur WHERE ur.USER_ID = u.ID AND ur.ROLE_ID = r.ID);

-- Assign ROLE_PROMOTED_ORDER_MANAGE to admin
INSERT INTO t_user_role (USER_ID, ROLE_ID)
SELECT u.ID, r.ID
FROM t_user u, t_role r
WHERE u.USERNAME = 'admin' AND r.ROLENAME = 'ROLE_PROMOTED_ORDER_MANAGE'
AND NOT EXISTS (SELECT 1 FROM t_user_role ur WHERE ur.USER_ID = u.ID AND ur.ROLE_ID = r.ID);

-- Assign ROLE_DIRECT_ORDER_MANAGE to admin
INSERT INTO t_user_role (USER_ID, ROLE_ID)
SELECT u.ID, r.ID
FROM t_user u, t_role r
WHERE u.USERNAME = 'admin' AND r.ROLENAME = 'ROLE_DIRECT_ORDER_MANAGE'
AND NOT EXISTS (SELECT 1 FROM t_user_role ur WHERE ur.USER_ID = u.ID AND ur.ROLE_ID = r.ID);

-- Assign ROLE_CERTIFICATION_MANAGE to admin
INSERT INTO t_user_role (USER_ID, ROLE_ID)
SELECT u.ID, r.ID
FROM t_user u, t_role r
WHERE u.USERNAME = 'admin' AND r.ROLENAME = 'ROLE_CERTIFICATION_MANAGE'
AND NOT EXISTS (SELECT 1 FROM t_user_role ur WHERE ur.USER_ID = u.ID AND ur.ROLE_ID = r.ID);
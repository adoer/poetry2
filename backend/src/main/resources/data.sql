INSERT IGNORE INTO users (username, password, role, created_at)
VALUES ('admin', '$2a$10$GxJMSEBi38840iSOjmZYvuNbeNsvQLSp3QiZOmQ8CnAmUe2.0CXtm', 'ADMIN', NOW());

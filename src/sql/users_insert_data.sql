INSERT INTO users (user_name, password, created, created_by, last_updated, last_updated_by)
SELECT 'test', 'test', now(), 'script', now(), 'script'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users);
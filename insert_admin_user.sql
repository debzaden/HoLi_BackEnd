-- Insert Admin User for testing
-- Password: admin123
-- BCrypt hash generated for "admin123" with rounds 10

-- First insert the user with ADMIN role
INSERT INTO users (
    user_name, 
    email, 
    password, 
    avatar, 
    provider, 
    role, 
    enabled, 
    created_at, 
    updated_at
) VALUES (
    'Administrator',
    'admin@holi.com',
    '$2a$10$7iFhEH.YtDgmw7XZNR8E2eSNVpKSBpqVhO9qQIBKFzRnfcv3WqxHG', -- bcrypt hash of "admin123" with rounds 10
    'https://via.placeholder.com/150x150.png?text=Admin',
    'LOCAL',
    'ADMIN',
    1,
    NOW(),
    NOW()
);

-- Then insert the admin record linking to the user
-- Replace 'LAST_INSERT_ID()' with actual user_id if needed
INSERT INTO admins (user_id) 
SELECT user_id FROM users WHERE email = 'admin@holi.com' AND role = 'ADMIN';

-- Verify the data
SELECT u.user_id, u.user_name, u.email, u.role, a.id as admin_id 
FROM users u 
LEFT JOIN admins a ON u.user_id = a.user_id 
WHERE u.role = 'ADMIN';

-- Note: Make sure the password is properly hashed using BCrypt
-- Online BCrypt generator: https://bcrypt-generator.com/
-- For password "admin123", use rounds 10
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    login VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

INSERT INTO users (user_id, login, first_name, last_name) VALUES
('11111111-1111-1111-1111-111111111111', 'user-1', 'User', 'Userenko'),
('22222222-2222-2222-2222-222222222222', 'user-4', 'AnotherUser', 'Another');

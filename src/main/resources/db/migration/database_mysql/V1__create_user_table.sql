CREATE TABLE user_table (
    ldap_login VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL
);

INSERT INTO user_table (ldap_login, name, surname) VALUES
('user-1', 'User', 'Userenko'),
('user-2', 'Testuser', 'Testov');

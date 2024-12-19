INSERT INTO users (username, password, role)
VALUES
    ('admin', '$2a$12$DptwGutvW.DmWI2oVTYzbORovLSFDCljcPNYRDNzrsUboRrUm/bWK', 'ADMIN'), -- password = secret
    ('user1', '$2a$12$W.geKnryBQUavtQh2gZ2lOZGbw2yK9fgwfU.VpWvsEjbXEFtAjMvG', 'USER'), -- password = 123456
    ('user2', '$2a$12$W.geKnryBQUavtQh2gZ2lOZGbw2yK9fgwfU.VpWvsEjbXEFtAjMvG', 'USER') -- password = 123456
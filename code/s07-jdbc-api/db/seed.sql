-- ============================================================================
--  Session 7 — seed data. Loaded once, only when the tables are empty.
--  Twelve students across four courses: enough rows to make a WHERE clause
--  and a JOIN mean something, small enough to read in full.
-- ============================================================================

INSERT INTO courses (id, title, credits) VALUES
    (1, 'Java Programming',      12),
    (2, 'Database Fundamentals', 10),
    (3, 'Web Development',        8),
    (4, 'Software Engineering',  14);

INSERT INTO students (name, email, course_id, mark, active) VALUES
    ('Ada Lovelace',    'ada@example.com',      1, 88.50, TRUE),
    ('Grace Hopper',    'grace@example.com',    1, 91.00, TRUE),
    ('Alan Turing',     'alan@example.com',     1, 76.25, TRUE),
    ('Katherine Johnson','katherine@example.com',2, 95.75, TRUE),
    ('Margaret Hamilton','margaret@example.com',2, 89.00, TRUE),
    ('Barbara Liskov',  'barbara@example.com',  2, 84.50, TRUE),
    ('Tim Berners-Lee', 'tim@example.com',      3, 79.00, FALSE),
    ('Guido van Rossum','guido@example.com',    3, 68.50, TRUE),
    ('Bjarne Stroustrup','bjarne@example.com',  3, 73.00, TRUE),
    ('Dennis Ritchie',  'dennis@example.com',   4, 97.25, TRUE),
    ('Ken Thompson',    'ken@example.com',      4, 82.00, TRUE),
    ('Linus Torvalds',  'linus@example.com',    4, 65.00, FALSE);

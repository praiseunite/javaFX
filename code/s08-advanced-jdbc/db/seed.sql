-- ============================================================================
--  Session 8 — seed data. Loaded once, only when the tables are empty.
--
--  Four courses with fees, eight students, and a handful of enrolments — some
--  paid, some not, so the ledger in the lab has something real to reconcile.
-- ============================================================================

INSERT INTO courses (id, title, credits, fee) VALUES
    (1, 'Java Programming',      12, 450.00),
    (2, 'Database Fundamentals', 10, 380.00),
    (3, 'Web Development',        8, 320.00),
    (4, 'Software Engineering',  14, 520.00);

INSERT INTO students (name, email, course_id, mark, active) VALUES
    ('Ada Lovelace',     'ada@example.com',       1, 88.50, TRUE),
    ('Grace Hopper',     'grace@example.com',     1, 91.00, TRUE),
    ('Alan Turing',      'alan@example.com',      1, 76.25, TRUE),
    ('Katherine Johnson','katherine@example.com', 2, 95.75, TRUE),
    ('Margaret Hamilton','margaret@example.com',  2, 89.00, TRUE),
    ('Barbara Liskov',   'barbara@example.com',   2, 84.50, TRUE),
    ('Tim Berners-Lee',  'tim@example.com',       3, 79.00, FALSE),
    ('Dennis Ritchie',   'dennis@example.com',    4, 97.25, TRUE);

-- Enrolments: student ids 1..8 map onto the rows above in order.
INSERT INTO enrolments (student_id, course_id, paid) VALUES
    (1, 1, TRUE),
    (2, 1, TRUE),
    (3, 1, FALSE),
    (4, 2, TRUE),
    (5, 2, FALSE),
    (6, 2, FALSE),
    (7, 3, TRUE),
    (8, 4, FALSE);

-- Payments: the paid enrolments have one payment each, for the course fee.
INSERT INTO fee_payments (enrolment_id, amount) VALUES
    (1, 450.00),
    (2, 450.00),
    (4, 380.00),
    (7, 320.00);

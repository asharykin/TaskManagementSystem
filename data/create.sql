CREATE SCHEMA t;

DROP TABLE IF EXISTS t.users;

DROP TABLE IF EXISTS t.tasks;

DROP TABLE IF EXISTS t.comments;

CREATE TABLE t.users (
       id BIGSERIAL PRIMARY KEY,
       username TEXT NOT NULL UNIQUE,
       password TEXT NOT NULL,
       role TEXT NOT NULL CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE t.tasks (
       id BIGSERIAL PRIMARY KEY,
       title TEXT NOT NULL,
       description TEXT,
       status TEXT NOT NULL CHECK (status IN ('WAITING', 'IN_PROGRESS', 'COMPLETED')),
       priority TEXT NOT NULL CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
       executor_id BIGINT NOT NULL REFERENCES t.users (id),
       author_id BIGINT NOT NULL REFERENCES t.users (id)
);

CREATE TABLE t.comments (
      id BIGSERIAL PRIMARY KEY,
      task_id BIGINT NOT NULL REFERENCES t.tasks (id),
      content TEXT NOT NULL,
      author_id INT NOT NULL REFERENCES t.users (id)
);
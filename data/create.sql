CREATE TABLE users (
       id BIGSERIAL PRIMARY KEY,
       username TEXT NOT NULL UNIQUE,
       password TEXT NOT NULL,
       role TEXT NOT NULL CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE tasks (
       id BIGSERIAL PRIMARY KEY,
       title TEXT NOT NULL,
       description TEXT,
       status TEXT NOT NULL CHECK (status IN ('WAITING', 'IN_PROGRESS', 'COMPLETED')),
       priority TEXT NOT NULL CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
       executor_id BIGINT NOT NULL REFERENCES users (id),
       author_id BIGINT NOT NULL REFERENCES users (id)
);

CREATE TABLE comments (
      id BIGSERIAL PRIMARY KEY,
      task_id BIGINT NOT NULL REFERENCES tasks (id),
      content TEXT NOT NULL,
      author_id INT NOT NULL REFERENCES users (id)
);
CREATE TABLE member (
    id   BIGINT PRIMARY KEY,
    name TEXT   NOT NULL
);

CREATE TABLE book (
    id    BIGINT PRIMARY KEY,
    title TEXT   NOT NULL
);

CREATE TABLE loan (
    id          UUID        PRIMARY KEY,
    member_id   BIGINT      NOT NULL REFERENCES member (id),
    book_id     BIGINT      NOT NULL REFERENCES book (id),
    borrowed_at TIMESTAMPTZ NOT NULL,
    returned_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX one_active_loan_per_book ON loan (book_id) WHERE returned_at IS NULL;

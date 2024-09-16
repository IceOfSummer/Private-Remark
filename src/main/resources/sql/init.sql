CREATE TABLE metadata(
    name CHAR(32) PRIMARY KEY,
    value VARCHAR(128)
);

CREATE TABLE remark(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    path VARCHAR(128),
    line_number INTEGER NOT NULL,
    content VARCHAR(512),
    current_line_content VARCHAR(128)
);

CREATE INDEX path_to_id ON remark(path);

INSERT INTO metadata VALUES('db_ver', '0');
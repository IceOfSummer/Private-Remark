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

CREATE TABLE remark_holder(
    remark_id INTEGER PRIMARY KEY ,
    offset_in_parent INTEGER NOT NULL,
    parent_identifier_name VARCHAR(64) NOT NULL
);

INSERT INTO metadata VALUES('db_ver', '0');
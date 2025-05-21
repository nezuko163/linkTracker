create table links.gh_branches
(
    branch VARCHAR(255) PRIMARY KEY,
    link_id   BIGINT NOT NULL,
    FOREIGN KEY (link_id) REFERENCES links.links(id) ON DELETE CASCADE
);

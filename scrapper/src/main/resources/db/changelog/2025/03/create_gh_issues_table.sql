create table links.gh_issues
(
    issue BIGSERIAL PRIMARY KEY,
    link_id   BIGINT NOT NULL,
    FOREIGN KEY (link_id) REFERENCES links.links(id) ON DELETE CASCADE
);

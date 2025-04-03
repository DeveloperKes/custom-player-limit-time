package net.kesgroom.cplt.models;

import net.kesgroom.cplt.orm.Entity;
import net.kesgroom.cplt.orm.annotations.Column;
import net.kesgroom.cplt.orm.annotations.OneToMany;
import net.kesgroom.cplt.orm.annotations.PrimaryKey;
import net.kesgroom.cplt.orm.annotations.Table;

import java.sql.Date;
import java.util.List;

@Table(name = "Players")
public class Player implements Entity {
    @PrimaryKey(auto_increment = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String username;

    @Column(nullable = false)
    private Boolean banned;

    @Column(name = "last_connection")
    private Date last_connection;

    @OneToMany(mappedBy = "player")
    private List<Session> sessions;
}

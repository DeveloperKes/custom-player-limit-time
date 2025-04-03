package net.kesgroom.cplt.models;

import net.kesgroom.cplt.orm.Entity;
import net.kesgroom.cplt.orm.annotations.Column;
import net.kesgroom.cplt.orm.annotations.ManyToOne;
import net.kesgroom.cplt.orm.annotations.PrimaryKey;
import net.kesgroom.cplt.orm.annotations.Table;

import java.sql.Date;

@Table(name = "Sessions")
public class Session implements Entity {
    @PrimaryKey(auto_increment = true)
    private int id;

    @Column()
    private Date login_time;

    @Column()
    private String comment;

    @ManyToOne
    @Column()
    private Player player;
}

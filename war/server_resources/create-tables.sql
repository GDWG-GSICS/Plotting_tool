
    drop table if exists plot_configuration;

    drop table if exists user;

    create table plot_configuration (
        plot_configuration_id bigint not null auto_increment,
        channel varchar(10),
        date varchar(20),
        mode varchar(100),
        name varchar(255) not null,
        ref_sat_instr varchar(100),
        sat_instr varchar(100),
        scene_tb double precision,
        server varchar(100),
        source varchar(100),
        type varchar(100),
        version varchar(10),
        year varchar(10),
        user_id bigint not null,
        idx integer,
        primary key (plot_configuration_id)
    );

    create table user (
        user_id bigint not null auto_increment,
        name varchar(20) not null unique,
        password_hash varchar(255) not null,
        primary key (user_id)
    );

    alter table plot_configuration 
        add index FKD1A93618C1753508 (user_id), 
        add constraint FKD1A93618C1753508 
        foreign key (user_id) 
        references user (user_id);

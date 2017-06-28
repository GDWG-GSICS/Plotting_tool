
<div align="center">
  <a href=http://gsics.wmo.int/>
    <img src="https://raw.githubusercontent.com/GDWG-GSICS/Plotting_tool/master/src/org/eumetsat/usd/gcp/client/resources/images/GSICS_logo_OPE.jpg" alt="GSICS Homepage" />
  </a>
</div>

<h1 align="center">GSICS Plotting Tool</h1>

<div align="center">
 A web application that allows visualising the GSICS products stored in any GSICS server.
</div>

<br />

## Getting Started
Try it out [here][GSICS Plotting Tool Demo] and check the [user guide] if you need help using the tool.

## In a Nutshell
The GSICS Plotting Tool is a [GWT] web application which navigates the different [THREDDS] GSICS catalogs and provides interactive time-series plots of the calibration products.

It makes use of the open-source JavaScript charting library [dygraphs].

### Features
- Shows error bands around data series
- Interactive pan and zoom
- Displays values on mouseover
- Allows changing the time range being shown
- Adjustable scene brightness temperature and bias range
- Toggle plots visibility
- Export plots as PNG or as embeddable URLs
- Save plots (user registration required)
- Download netCDF datasets being plotted

## How to Install
In order to host this web application, the server shall meet the following software requirements:
-	Tomcat 7 or 8 installed.
-	Java 7 or 8 installed.
-	Open HTTP outbound port (80), in order to access external HTTP GSICS servers.
-	MySQL database v4 or above, setup as stated by SQL queries below (required by save plots feature).

```
create database gsics_plotter_db;

grant SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, ALTER on gsics_plotter_db.* to 'plotter'@'localhost' identified by 'plotter2012';

use gsics_plotter_db;

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
```

After building the project, <code>plotter.war</code> will be created in the project root folder. In order to deploy it, copy the <code>plotter.war</code> file into the <code>webapps</code> directory in your server's tomcat installation.

## How to Contribute
Read the [contributing guideline].




[GSICS Homepage]:http://gsics.wmo.int/
[Logo]:src/org/eumetsat/usd/gcp/client/resources/images/GSICS_logo_OPE.jpg
[GSICS Plotting Tool Demo]:http://gsics.tools.eumetsat.int/plotter
[User Guide]:src/org/eumetsat/usd/gcp/client/resources/pdf/GSICS_Plotting_Tool_UserGuide.pdf
[GWT]:http://gwtproject.org
[THREDDS]:http://www.unidata.ucar.edu/software/thredds/current/tds/
[dygraphs]:http://dygraphs.com/
[contributing guideline]:CONTRIBUTING.md

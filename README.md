
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


[GSICS Homepage]:http://gsics.wmo.int/
[Logo]:src/org/eumetsat/usd/gcp/client/resources/images/GSICS_logo_OPE.jpg
[GSICS Plotting Tool Demo]:http://gsics.tools.eumetsat.int/plotter
[User Guide]:src/org/eumetsat/usd/gcp/client/resources/pdf/GSICS_Plotting_Tool_UserGuide.pdf
[GWT]:http://gwtproject.org
[THREDDS]:http://www.unidata.ucar.edu/software/thredds/current/tds/
[dygraphs]:http://dygraphs.com/

// @formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2016
 */
// @formatter:on
package org.eumetsat.usd.gcp.server.data;

import org.eumetsat.usd.gcp.shared.exception.FormulaException;

/**
 * Calibration Record (corresponding to one time instant) of a GSICS NetCDF calibration product.
 * 
 * @author USC/C/PBe
 */
public interface CalibrationRecord
{
    /**
     * Get the Brightness Temperature Bias.
     * 
     * @return Brightness Temperature Bias.
     * @throws FormulaException
     */
    double getTbBias() throws FormulaException;

    /**
     * Get the Brightness Temperature Bias Uncertainty.
     * 
     * @return Brightness Temperature Bias Uncertainty.
     * @throws FormulaException
     */
    double getTbUncertainty() throws FormulaException;

    /**
     * Check if this calibration record is valid.
     * 
     * @return <code>true</code> if the calibration record is valid, <code>false</code> otherwise.
     */
    boolean isValid();

}
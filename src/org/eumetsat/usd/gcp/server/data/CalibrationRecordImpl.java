package org.eumetsat.usd.gcp.server.data;

import java.util.Map;

import org.eumetsat.usd.gcp.shared.exception.FormulaException;

import com.google.inject.Inject;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

/**
 * Implementation for the common -default- GSICS calibration record.
 * 
 * @author USC/C/PBe
 */
public class CalibrationRecordImpl implements CalibrationRecord
{
    /** Radiance to Tb conversion formula. */
    private String radToTbConvFormula;

    /** Tb to radiance conversion formula. */
    private String tbToRadConvFormula;

    /** Conversion variables, indexed by name. */
    private Map<String, Double> convVars;

    /** Standard radiance variable name. */
    private String radVarName;

    /** Standard tb variable name. */
    private String tbVarName;

    /**
     * Variables which are required to be on the netCDF GSICS products.
     */

    /** Regression offset in [mW m-2 sr-1(cm-1)-1]. */
    private double offset;
    /** Standard error of regression offset in [mW m-2 sr-1(cm-1)-1]. */
    private double offsetSe;
    /** Regression slope. */
    private double slope;
    /** Standard deviation of regression slope. */
    private double slopeSe;
    /** Regression coefficients covariance in [mW m-2 sr-1(cm-1)-1]. */
    private double covariance;
    /** Brightness temperature in [K] (constant in time). */
    private double sceneTb;

    /**
     * Constructor.
     * 
     * @param radToTbConvFormula
     *            formula to convert from rad to tb.
     * @param tbToRadConvFormula
     *            formula to convert from tb to rad.
     * @param convVars
     *            conversion variables.
     * @param tbVarName
     *            standard brightness temperature variable name.
     * @param radVarName
     *            standard radiance variable name.
     * @param offset
     *            regression offset in [mW m-2 sr-1(cm-1)-1].
     * @param offsetSe
     *            standard error of regression offset in [mW m-2 sr-1(cm-1)-1].
     * @param slope
     *            regression slope.
     * @param slopeSe
     *            standard deviation of regression slope.
     * @param covariance
     *            regression coefficients covariance in [mW m-2 sr-1(cm-1)-1].
     * @param sceneTb
     *            scene brightness temperature.
     */
    @Inject
    public CalibrationRecordImpl(final String radToTbConvFormula, final String tbToRadConvFormula,
            final Map<String, Double> convVars, final String tbVarName, final String radVarName, final double offset,
            final double offsetSe, final double slope, final double slopeSe, final double covariance,
            final double sceneTb)
    {
        this.radToTbConvFormula = radToTbConvFormula;
        this.tbToRadConvFormula = tbToRadConvFormula;

        this.convVars = convVars;

        this.radVarName = radVarName;
        this.tbVarName = tbVarName;

        this.offset = offset;
        this.offsetSe = offsetSe;
        this.slope = slope;
        this.slopeSe = slopeSe;
        this.covariance = covariance;
        this.sceneTb = sceneTb;
    }

    @Override
    public double getTbBias() throws FormulaException
    {
        // Convert scene tb to scene rad.
        double sceneRad = convertTbToRad(sceneTb);

        // Compute radiance bias.
        double radBias = computeRadBias(sceneRad, this.offset, this.slope);

        // Convert bias back to brightness temperature.
        double tbBias = convertRadToTb(radBias + sceneRad) - sceneTb;

        return tbBias;
    }

    @Override
    public double getTbUncertainty() throws FormulaException
    {
        // Convert scene tb to scene rad.
        double sceneRad = convertTbToRad(sceneTb);

        // Compute radiance uncertainty.
        double radUncertainty = computeRadUncertainty(sceneRad, this.offsetSe, this.slopeSe, this.covariance);

        // Convert uncertainty back to brightness temperature.
        double tbUncertainty = convertRadToTb(radUncertainty + sceneRad) - sceneTb;

        return tbUncertainty;
    }

    /**
     * Compute radiance bias as stated in Req. 6.2. in EUM/OPS/TEN/11/3804.
     * 
     * @param sceneRad
     *            brightness temperature in [rad]
     * @param offset
     *            regression offset in [mW m-2 sr-1(cm-1)-1]
     * @param slope
     *            regression slope
     * 
     * @return double bias in [mW m-1 sr-1 (cm-1)-1]
     */
    private static double computeRadBias(final double sceneRad, final double offset, final double slope)
    {
        double radBias = offset + slope * sceneRad - sceneRad;

        return radBias;
    }

    /**
     * Compute radiance uncertainty as stated in Req. 6.2. in EUM/OPS/TEN/11/3804.
     * 
     * @param sceneRad
     *            scene radiance in [rad]
     * @param offsetSe
     *            standard error of regression offset in [mW m-2 sr-1(cm-1)-1]
     * @param slopeSe
     *            standard error of regression slope
     * @param covariance
     *            regression coefficients covariance in [mW m-2 sr-1(cm-1)-1]
     * 
     * @return double rad uncertainty in [mW m-1 sr-1 (cm-1)-1]
     */
    private static double computeRadUncertainty(final double sceneRad, final double offsetSe, final double slopeSe,
            final double covariance)
    {
        double radUncertainty = Math.sqrt(Math.pow(offsetSe, 2) + Math.pow(slopeSe, 2) * Math.pow(sceneRad, 2) + 2
                * covariance * sceneRad);

        return radUncertainty;
    }

    /**
     * Convert radiance to brightness temperature dynamically using formula defined in the netCDF GSICS product file.
     * 
     * @param rad
     *            radiance [mW m-1 sr-1 (cm-1)-1].
     * @return brightness temperature [K].
     * @throws FormulaException
     *             when there is an error with formula parsing.
     */
    private double convertRadToTb(final double rad) throws FormulaException
    {
        // check that all variables have values.
        for (String variableName : convVars.keySet())
        {
            if (convVars.get(variableName) == null)
            {
                throw new FormulaException("'" + variableName + "' value is missing.");
            }
        }

        // do the conversion.
        try
        {
            Calculable calc = new ExpressionBuilder(radToTbConvFormula).withVariables(convVars).withVariable(
                    radVarName, rad).build();

            return calc.calculate();

        } catch (ArithmeticException ae)
        {
            throw new FormulaException("error converting from tb to radiance: " + ae.getMessage());
        } catch (UnknownFunctionException ufe)
        {
            throw new FormulaException("error converting from radiance to tb: " + ufe.getMessage());
        } catch (UnparsableExpressionException upe)
        {
            throw new FormulaException("error converting from radiance to tb: " + upe.getMessage());
        }
    }

    /**
     * Convert brightness temperature to radiance dynamically using formula defined in the netCDF GSICS product file.
     * 
     * @param tb
     *            brightness temperature [K].
     * @return radiance [mW m-1 sr-1 (cm-1)-1].
     * @throws FormulaException
     *             when there is an error with formula parsing.
     */
    private double convertTbToRad(final double tb) throws FormulaException
    {
        // check that all variables have values.
        for (String variableName : convVars.keySet())
        {
            if (convVars.get(variableName) == null)
            {
                throw new FormulaException("'" + variableName + "' value is missing.");
            }
        }

        // do the conversion.
        try
        {
            Calculable calc = new ExpressionBuilder(tbToRadConvFormula).withVariables(convVars).withVariable(tbVarName,
                    tb).build();

            return calc.calculate();

        } catch (ArithmeticException ae)
        {
            throw new FormulaException("error converting from tb to radiance: " + ae.getMessage());
        } catch (UnknownFunctionException ufe)
        {
            throw new FormulaException("error converting from tb to radiance: " + ufe.getMessage());
        } catch (UnparsableExpressionException upe)
        {
            throw new FormulaException("error converting from tb to radiance: " + upe.getMessage());
        }
    }

    @Override
    public boolean isValid()
    {
        // Check if any variable has an out of bound value.
        if (Double.compare(offset, Double.POSITIVE_INFINITY) == 0
                || Double.compare(offsetSe, Double.POSITIVE_INFINITY) == 0
                || Double.compare(slope, Double.POSITIVE_INFINITY) == 0
                || Double.compare(slopeSe, Double.POSITIVE_INFINITY) == 0
                || Double.compare(covariance, Double.POSITIVE_INFINITY) == 0)
        {
            return false;
        }

        // Check that all variables have values.
        for (String variableName : convVars.keySet())
        {
            if (convVars.get(variableName) == null)
            {
                return false;
            }
        }

        return true;
    }
}

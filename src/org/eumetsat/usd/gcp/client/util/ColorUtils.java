// @formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe
 * COPYRIGHT: EUMETSAT 2012
 */
// @formatter:on
package org.eumetsat.usd.gcp.client.util;

import java.util.Vector;

/**
 * Color Utilities.
 * 
 * @author USD/C/PBe
 */
public final class ColorUtils
{
    /**
     * Private Constructor to make it non-instantiable.
     */
    private ColorUtils()
    {

    }

    /**
     * Generates n different high-contrast colors using in string format: "rgb(r,g,b)", using the
     * golden ratio. A silver color for uncertainty plots is added between each distinct color.
     * 
     * @param n
     *            number of plots.
     * @return n different high-contrast colors, spaced by silver color for uncertainty plots.
     */
    public static Vector<String> createPlotColors(final int n)
    {
        Vector<String> colors = new Vector<String>(n * 2);

        double saturation = 0.70;
        double value = 0.80;

        double goldenRatioConjugate = 0.618033988749895;

        double hueFactor = 0.0;
        for (int i = 0; i < n; i++)
        {
            colors.add(ColorUtils.hsvToRGB(hueFactor * 360, saturation, value));

            // Add silver for the uncertainty plot.
            colors.add("silver");

            // Calculate next most distinct color.
            hueFactor += goldenRatioConjugate;
            hueFactor %= 1;
        }

        return colors;
    }

    /**
     * Converts the HSV values into a RGB string in the format "rgb(r,g,b)".
     * 
     * @param hue
     *            the hue of the color [0,360).
     * @param saturation
     *            the saturation of the color [0.0, 1.0].
     * @param value
     *            the value of the color [0.0, 1.0].
     * @return color in the format "rgb(r,g,b)".
     */
    public static String hsvToRGB(final double hue, final double saturation, final double value)
    {
        double red = 0.0;
        double green = 0.0;
        double blue = 0.0;

        if (Double.compare(saturation, 0.0) == 0)
        {
            red = value;
            green = value;
            blue = value;

        } else
        {
            int i = (int) Math.floor(hue / 60);

            double f = (hue / 60) - i;
            double p = value * (1 - saturation);
            double q = value * (1 - (saturation * f));
            double t = value * (1 - (saturation * (1 - f)));

            switch (i)
            {
                case 1:
                    red = q;
                    green = value;
                    blue = p;
                    break;
                case 2:
                    red = p;
                    green = value;
                    blue = t;
                    break;
                case 3:
                    red = p;
                    green = q;
                    blue = value;
                    break;
                case 4:
                    red = t;
                    green = p;
                    blue = value;
                    break;
                case 5:
                    red = value;
                    green = p;
                    blue = q;
                    break;
                case 6: // fall through
                case 0:
                default:
                    red = value;
                    green = t;
                    blue = p;
                    break;
            }
        }

        red = Math.floor(255 * red + 0.5);
        green = Math.floor(255 * green + 0.5);
        blue = Math.floor(255 * blue + 0.5);

        return ("rgb(" + (int) red + "," + (int) green + "," + (int) blue + ")");
    }

}

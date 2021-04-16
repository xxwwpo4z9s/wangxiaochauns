package feature;

/**
 * Inverse Fourier transformation, using DCT because only use real coefficients
 * @author wanggang
 * @version May 13, 2015
 */

public class DCT
{
    int numCepstra;     // number of mfcc coeffs
    int M;              // number of Mel filters

    /**
     * Constructor
     * @param numCepstra length of array, i.e., number of features
     * @param M number of Mel filters.
     */
    public DCT(int numCepstra, int M)
    {
        this.numCepstra = numCepstra;
        this.M = M;
    }

    public double[] performDCT(double [] y)
    {
        double [] cepc = new double[numCepstra];
        // perform DCT
        for (int n = 1; n <= numCepstra; n++)
        {
            for (int i = 1; i <= M; i++)
            {
                cepc[n - 1] += y[i - 1] * Math.cos(Math.PI * (n - 1) / M *(i - 0.5));
            }
        }
        return cepc;
    }
}

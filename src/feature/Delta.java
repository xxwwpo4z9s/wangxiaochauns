package feature;

import utils.ArrayWriter;

/**
 * Calculate delta and delta-delta coefficients of MFCC
 *
 * @reference [1] Spectral Features for Automatic Text-Independent Spearker
 *                Recognition, Tomi Kinnunen, pp83
 * @author wanggang
 * @version May 13, 2015
 */
public class Delta
{
    int M;  // regression window size, i.e., number of frames to take into account

    public Delta(){}

    public void setRegressionWindow(int M)
    {
        this.M = M;
    }

    public double[][] performDelta2D(double[][] data)
    {
        int noOfMfcc = data[0].length;  // number of MFCC coefficients
        int frameCount = data.length;   // frames
        // Calculate sum of mSquare, i.e., denominator
        double mSqSum = 0;
        for (int i = -M; i < M; i++)
        {
            mSqSum += Math.pow(i, 2);
        }
        // Calculate numerator
        double [][] delta = new double[frameCount][noOfMfcc];
        for (int i = 0; i < noOfMfcc; i++) {
            // handle the boundary
            // 0 padding results best result
            // from 0 to M
            for (int k = 0; k < M; k++) {
                //delta[k][i] = 0;
                delta[k][i] = data[k][i];   // 0 padding
            }
            // from frameCount-M to frameCount
            for (int k = frameCount - M; k < frameCount; k++)
            {
                //delta[1][i] = 0;
                delta[k][i] = data[k][i];
            }
            for (int j = M; j < frameCount - M; j++)
            {
                // travel from -M to +M
                double sumDataNumM = 0;
                for (int m = -M; m <= M; m++)
                {
                    System.out.println("Current m -->\t" + m +
                            "current j -->\t" + j + " data[m+j][i] -->\t" +
                            data[m + j][i]);
                    sumDataNumM += m * data[m + j][i];
                }
                // divide
                delta[j][i] = sumDataNumM / mSqSum;
            }
        } // end of loop

        System.out.println("Delta 2d **************");
        ArrayWriter.print2DTabbedDoubleArrayToConsole(delta);

        return delta;
    }

    public double[] performDelta1D(double[] data) {
        int frameCount = data.length;

        double mSqSum = 0;
        for (int i = -M; i < M; i++) {
            mSqSum += Math.pow(i, 2);
        }
        double[] delta = new double[frameCount];

        for (int k = 0; k < M; k++) {
            delta[k] = data[k]; // 0 padding
        }
        // from frameCount-M to frameCount
        for (int k = frameCount - M; k < frameCount; k++) {
            delta[k] = data[k];
        }
        for (int j = M; j < frameCount - M; j++) {
            // travel from -M to +M
            double sumDataMulM = 0;
            for (int m = -M; m <= +M; m++) {
                System.out.println("Current m -->\t"+m+ "current j -->\t"+j +
                 "data [m+j] -->\t"+data[m + j]);
                sumDataMulM += m * data[m + j];
            }
            // 3. divide
            delta[j] = sumDataMulM / mSqSum;
        }
        System.out.println("Delta 1d **************");
        ArrayWriter.printDoubleArrayToConsole(delta);
        return delta;
    }
}

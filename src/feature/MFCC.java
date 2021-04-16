package feature;

import utils.ArrayWriter;

/**
 * INTRODUCTION:
 *
 * MFCC (Mel-scale Frequency Cepstral Coefficients)
 * In signal processing field, mel-frequency cepstrum (MFC) presents the short-time
 * power spectrum. It firstly turns the audio into log-based power spectrum according
 * to the Mel-scale, and then transformed via linear cosine transform. All the MFCC
 * combine to a MFC.
 *
 * METHOD:
 *
 * (1) Pre-emphasis
 * (2) Frame Blocking
 * (3) Windowing
 * (4) FFT
 * (5) Power spectrum calculation
 * (6) Mel Filter Bank
 * (7) Logarithm scaling
 * (8) DCT transform
 *
 * Input: Framed samples
 * Output: MFCC coefficients
 *
 * @reference [1] https://blog.csdn.net/audio-algorithm/artical/details/80884493
 * @reference [2] https://blog.csdn.net/xiaoyaoren3134/article/details/48678553
 *
 * @author wangxc
 * @version Feb 8, 2020
 */

public class MFCC
{
    private double preEmphasisAlpha = 0.97; // \mu: H(Z)=1-\mu z^{-1}
    private int numMelFilters = 30;         // how many Mel filters
    private int numCepstra;                 // number of mfcc coeffs
    private double bin[];
    private int samplePerframe;
    private double samplingRate;
    private double upperFilterFreq;
    private double lowerFilterFreq = 80.00;

    FFT fft;
    DCT dct;

    /**
     * Constructor
     * @param samplePerFrame
     * @param samplingRate
     * @param numCepstra
     */
    public MFCC(int samplePerFrame, int samplingRate, int numCepstra)
    {
        this.samplePerframe = samplePerFrame;
        this.samplingRate = samplingRate;
        this.numCepstra = numCepstra;
        upperFilterFreq = samplingRate / 2.0;
        fft = new FFT();
        dct = new DCT(this.numCepstra, numMelFilters);
    }

    /**
     * Perform the MFCC
     * @param framedSignal the framed signal
     * @return the MFCC coefficients
     */
    public double [] doMFCC(float[] framedSignal)
    {
        // Magnitude Spectrum
        bin = magnitudeSpectrum(framedSignal);
        // pre-emphasis
        framedSignal = preEmphasis(framedSignal);

        // cbin = frequencies of the channels in terms of FFT bin indices
        // (cbin[i] for the i-th channel)

        // prepare filter for melFilter
        int cbin[] = fftBinIndices();
        // process Mel Filterbank
        double [] fbank = melFilter(bin, cbin); // magnitudeSpectrum and bin indices

        System.out.println("after mel filter");
        ArrayWriter.printDoubleArrayToConsole(fbank);

        // Non-linear transformation
        double [] f = nonLinearTransforation(fbank);

        System.out.println("after non-linear transformation");
        ArrayWriter.printDoubleArrayToConsole(f);

        // Cepstral coefficients via DCT
        double [] cepc = dct.performDCT(f);

        System.out.println("after DCT");
        ArrayWriter.printDoubleArrayToConsole(cepc);

        return cepc;
    }

    /**
     * Internal method, calculate magnitude spectrum
     * @param frame
     * @return
     */
    private double [] magnitudeSpectrum(float [] frame)
    {
        double [] magSpectrum = new double [frame.length];
        // FFT
        fft.computeFFT(frame);
        System.out.println("FFT success");
        // Calculate magnitude spectrum
        for (int k = 0; k < frame.length; k++)
        {
            magSpectrum[k] = Math.sqrt(fft.real[k] * fft.real[k] + fft.imag[k] * fft.imag[k]);
        }
        return magSpectrum;
    }

    /**
     * Pre-emphasis method, leverage the high frequency resolution.
     * Using first-order FIR high-pass filter, y(n) = x(n)-ax(n-1)
     * a is the pre-emphasis alpha.
     * @param inputSignal the input samples.
     * @return the output samples.
     */
    private float[] preEmphasis(float inputSignal [])
    {
        System.out.println("inside pre-Emphasis");
        float outputSignal[] = new float[inputSignal.length];
        // apply pre-emphasis to each sample
        for (int n = 1; n < inputSignal.length; n++)
        {
            outputSignal[n] = (float)(inputSignal[n] - preEmphasisAlpha *
                    inputSignal[n - 1]); // First-order differential function
        }
        return outputSignal;
    }

    /**
     * Internal method, decide the bin (and the central freq) of each mel filter.
     * @return
     */
    private int [] fftBinIndices()
    {
        int [] cbin = new int[numMelFilters + 2]; // some reference using +1
        cbin[0] = (int) Math.round(lowerFilterFreq / samplingRate * samplePerframe); // cbin0
        cbin[cbin.length - 1] = (samplePerframe / 2); // cbin24
        for (int i = 1; i <= numMelFilters; i++) // from cbin1 to cbin23
        {
            double fc = centerFreq(i); // center freq of i-th mel filter.
            cbin[i] = (int) Math.round(fc / samplingRate * samplePerframe); // bin of i-th filter
        }
        return cbin;
    }

    /**
     * Internal method, construct mel filter
     * @param bin magnitude spectrum (| |^2)
     * @param cbin
     * @return mel filtered coefficients
     */
    private double [] melFilter(double [] bin, int [] cbin)
    {
        double [] temp = new double[numMelFilters + 2];
        for (int k = 1; k <= numMelFilters; k++)
        {
            double num1 = 0.0;
            double num2 = 0.0;
            for (int i = cbin[k - 1]; i <= cbin[k]; i++) // the bin is increasing
            {
                num1 += ((i - cbin[k - 1] + 1.0) / (cbin[k] - cbin[k -1] + 1.0)) * bin[i];
            }
            for (int i = cbin[k] + 1; i <= cbin[k + 1]; i++)
            {
                num2 += (1.0 - ((i - cbin[k]) / (cbin[k + 1] - cbin[k] + 1.0))) * bin[i];
            }
            temp[k] = num1 + num2;
        }
        double [] fbank = new double[numMelFilters];
        for (int i = 0; i < numMelFilters; i++)
        {
            fbank[i] = temp[i + 1];
        }
        return fbank;
    }

    /**
     * Internal method, perform Logarithm
     * @param fbank
     * @return
     */
    private double [] nonLinearTransforation(double [] fbank)
    {
        double [] f = new double[fbank.length];
        final double FLOOR = -50;
        for (int i = 0; i < fbank.length; i++)
        {
            f[i] = Math.log(fbank[i]);
            // check if In() returns a value less than the floor
            if (f[i] < FLOOR)
            {
                f[i] = FLOOR;
            }
        }
        return f;
    }

    /**
     * Internal method, find the center freq in freq domain.
     * @param i
     * @return
     */
    private double centerFreq(int i )
    {
        double melFlow;
        double melFHigh;
        melFlow = freqToMel(lowerFilterFreq);
        melFHigh = freqToMel(upperFilterFreq);
        double temp = melFlow + ((melFHigh - melFlow) / (numMelFilters + 1)) * i;
        return inverseMel(temp);
    }

    private double freqToMel(double freq)
    {
        return 2595 * log10(1 + freq / 700);
    }

    private double log10(double value)
    {
        return Math.log(value) / Math.log(10);
    }

    private double inverseMel(double x)
    {
        double temp = Math.pow(10, x / 2595) - 1;
        return 700 * temp;
    }
}

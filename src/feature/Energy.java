package feature;

/**
 * Calculate energy of PCM frame.
 */
public class Energy
{
    private int samplePerFrame; // according to samplingRate;

    /**
     * Constructor
     * @param samplePerFrame
     */
    public Energy(int samplePerFrame)
    {
        this.samplePerFrame = samplePerFrame;
    }

    /**
     * Energy
     * @param framedSignal
     * @return energy of given PCM frame
     */
    public double [] calcEnergy(float[][] framedSignal)
    {
        double [] energyValue = new double[framedSignal.length];

        for (int i = 0; i < framedSignal.length; i++)
        {
            float sum = 0;
            for (int j = 0; j < samplePerFrame; j++)
            {
                sum += Math.pow(framedSignal[i][j], 2);
            }
            energyValue[i] = Math.log(sum); // 取了log
        }
        return energyValue;
    }
}

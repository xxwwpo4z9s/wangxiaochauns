package feature;

/**
 * Calculate energy of PCM frame.
 */
public class Energy
{

    public Energy()
    {

    }

    /**
     * Energy
     * @param framedSignal
     * @return energy of given PCM frame
     */
    public double [] calcLogEnergy(float[][] framedSignal, int samplePerFrame)
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

    public double[] calcEnergy(float[][] framedSignal){
        double [] energyValue = new double[framedSignal.length];

        for (int i = 0; i < framedSignal.length; i++)
        {
            float sum = 0;
            for (int j = 0; j < framedSignal[i].length; j++)
            {
                sum += Math.pow(framedSignal[i][j], 2);
            }
            energyValue[i] = sum;
        }
        return energyValue;
    }

    public double calcFrameEnergy(float[] signal){
        float sum = 0;
        for (float v : signal) {
            sum += Math.pow(v, 2);
        }
        return sum;
    }
}

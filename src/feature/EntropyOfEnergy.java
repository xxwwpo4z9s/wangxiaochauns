package feature;
import java.lang.Math;
import java.util.Arrays;

public class EntropyOfEnergy {

    private int nShortBlocks;
    private double EPS = 1e-7;

    public EntropyOfEnergy(int nShortBlocks){
        this.nShortBlocks = nShortBlocks;
    }

    public double [] calcEntropyOfEnergy(float[][] framedSignal){
        Energy energy = new Energy();
        double[] signalEnergy = energy.calcEnergy(framedSignal);
        double[] entropy = new double[framedSignal.length];

        for (int i = 0; i < framedSignal.length; i++){
            double frameEnergy = signalEnergy[i];
            int frameLength = framedSignal[i].length;
            int subWindowLength = frameLength / nShortBlocks;

            int index = 0;
            double frameEntropy = 0;
            while (index < frameLength){
                float[] subWinSignal = Arrays.copyOfRange(framedSignal[i], index, subWindowLength + index);
                double subWinEnergy = energy.calcFrameEnergy(subWinSignal);
                double s = subWinEnergy / (frameEnergy + EPS);
                s = s * Math.log(s + EPS) / Math.log(2);
                frameEntropy += s;

                index += subWindowLength;
            }
            entropy[i] = -1 * frameEntropy;
        }
        return entropy;
    }
}

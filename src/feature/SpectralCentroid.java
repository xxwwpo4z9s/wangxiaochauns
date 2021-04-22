package feature;


public class SpectralCentroid{

    public SpectralCentroid(){

    }

    public double [] calcSpectralCentroid(float[][] framedSignal){
        double [] spectralCentroidValue = new double[framedSignal.length];
        
        for (int i = 0; i < framedSignal.length; i++){
            double sumCentroid = 0;
            double sumIntensities = 0;
            int size = framedSignal[i].length;
            for (int j = 0; j < size; j++){
                if (framedSignal[i][j] > 0){
                    sumCentroid += j * framedSignal[i][j];
                    sumIntensities += framedSignal[i][j];
                }
            }
            spectralCentroidValue[i] = sumCentroid / sumIntensities;

        }
        return spectralCentroidValue;
    }
}

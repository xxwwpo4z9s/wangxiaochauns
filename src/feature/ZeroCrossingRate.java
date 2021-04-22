package feature;

public class ZeroCrossingRate{

    public ZeroCrossingRate(){
    
    }
    /**
     * zcr
     * @param framedSignal
     * @return zcr of given PCM frame
     */
    public int [] calcZeroCrossingRate(float[][] framedSignal){
        int [] zcr = new int[framedSignal.length];

        for (int i = 0; i < framedSignal.length; i++){
            int numZC = 0;
            int size = framedSignal[i].length;
            for (int j = 0; j < size - 1; j++){
                if ((framedSignal[i][j] >=0 && framedSignal[i][j + 1] < 0) || (framedSignal[i][j] < 0 && framedSignal[i][j + 1] >=0)){
                    numZC++;
                }
            }
            zcr[i] = numZC;
        }
        return zcr;
    }
}

import audio.FeatureExtract;
import audio.PreProcess;
import audio.WaveData;
import feature.FeatureVector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MFCCFeatureMain {
    private static final int SAMPLING_RATE = 1020;
    private static final int SAMPLE_PER_FRAME = 256;
    private static final int FEATURE_DIMENSION = 39;
    private FeatureExtract featureExtract;
    private WaveData waveData;
    private PreProcess prp;
    private List<double[]> allFeaturesList = new ArrayList<double[]>();

    public MFCCFeatureMain()
    {
        waveData = new WaveData();
    }

    // Sample
    public static void main(String[] args)
    {
        MFCCFeatureMain mfcc = new MFCCFeatureMain();
        String filename = "data/1.wav"; // test speech file
        double [] feature = mfcc.getFeature(filename);
        System.out.println("Feature output: ");
        for (int i = 0; i < feature.length; i++)
        {
            if (i % 8 != 0)
                System.out.println(feature[i] + "\t");
            else
                System.out.println(feature[i] + "\n");
        }
        System.out.println("Finished!");
    }

    private double[] getFeature(String filename)
    {
        int totalFrames = 0;
        FeatureVector feature = extractFeatureFromFile(new File(filename));
        for (int k = 0; k < feature.getNoOfFrames(); k++) {
            allFeaturesList.add(feature.getFeatureVector()[k]);
            totalFrames++;
        }
        System.out.println("帧数： " + totalFrames + "，特征列表大小： " + allFeaturesList.size());
        // 行代表帧数，列代表特征
        double allFeatures[][] = new double[totalFrames][FEATURE_DIMENSION];
        for (int i = 0; i < totalFrames; i++) {
            double[] tmp = allFeaturesList.get(i);
            allFeatures[i] = tmp;
        }
        // 输出特征
        for (int i = 0; i < totalFrames; i++) {
            for (int j = 0; j < FEATURE_DIMENSION; j++) {
                System.out.println(allFeatures[i][j]);
            }
        }
        // 计算每帧对应特征的平均值
        double [] avgFeatures = new double[FEATURE_DIMENSION];
        for (int j = 0; j < FEATURE_DIMENSION; j++) { // 循环每列
            double tmp = 0.0d;
            for (int i = 0; i < totalFrames; i++) { // 循环每行
                tmp += allFeatures[i][j];
            }
            avgFeatures[j] = tmp / totalFrames;
        }
        return avgFeatures;
}

    private FeatureVector extractFeatureFromFile(File speechFile)
    {
        float [] arrAmp;
        arrAmp = waveData.extractAmplitudeFromFile(speechFile);
        System.out.println("arrAmp: " + arrAmp);
        prp = new PreProcess(arrAmp, SAMPLE_PER_FRAME, SAMPLING_RATE);
        featureExtract = new FeatureExtract(prp.framedSignal, SAMPLING_RATE, SAMPLE_PER_FRAME);
        featureExtract.makeMfccFeatureVector();
        return featureExtract.getFeatureVector();
    }
}

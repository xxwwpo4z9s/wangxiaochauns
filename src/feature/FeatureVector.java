package feature;

import java.io.Serializable;

/**
 * Combine the MFCC features into a vector.
 *
 * @author wangxc
 * @version Feb 8, 2020
 */

public class FeatureVector implements Serializable
{
    private static final long serialVersionID = -8560345372655736399L;

    // 2d array of feature vector
    // size: noOfFrame * noOfFeatures
    private double [][] mfccFeature;
    private double [][] featureVector;
    private int noOfFrames;
    private int noOfFeatures;

    public FeatureVector(){}

    public double[][] getMfccFeature() {
        return mfccFeature;
    }

    public void setMfccFeature(double[][] mfccFeature) {
        this.mfccFeature = mfccFeature;
    }

    public int getNoOfFrames() {
        return featureVector.length;
    }

    public void setNoOfFrames(int noOfFrames) {
        this.noOfFrames = noOfFrames;
    }

    public int getNoOfFeatures() {
        return featureVector[0].length;
    }

    public void setNoOfFeatures(int noOfFeatures) {
        this.noOfFeatures = noOfFeatures;
    }

    public double[][] getFeatureVector(){return featureVector;}
    public void setFeatureVector(double[][] featureVector)
    {
        this.featureVector = featureVector;
    }
}

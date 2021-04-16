package audio;

import utils.Indicator;

/**
 * Pre-processing of the original signal, include:
 * (1) Framing
 * (2) Windowing
 *
 * @author wanggang
 * @verstion May 13, 2015
 */

public class PreProcess
{
    float [] originalSignal;// initial extracted PCM
    float [] normalizedSignal; // 归一化后的信号
    float [] afterEndPtDetection;// after endPointDetection
    int samplePerFrame; // 帧长
    public int noOfFrames;
    //int framedArrayLength;// how many samples in framed array
    public float [][] framedSignal;
    float [] hammingWindow;
    EndPointDetection epd;
    int samplingRate;

    /**
     * constructor, all steps are called frm here
     *
     * @param originalSignal extracted PCM data
     * @param samplePerFrame
     *            how many samples in one frame,=660 << frameDuration, typically
     *            30; samplingFreq, typically 22Khz
     */
    public PreProcess(float[] originalSignal, int samplePerFrame, int samplingRate) {
        this.originalSignal = originalSignal;
        this.samplePerFrame = samplePerFrame;
        this.samplingRate = samplingRate;

        this.normalizedSignal = normalizePCM(); // 归一化
        epd = new EndPointDetection(this.normalizedSignal, this.samplingRate);
        afterEndPtDetection = epd.doEndPointDetection();
        // ArrayWriter.printFloatArrayToFile(afterEndPtDetection, "endPt.txt");
        doFraming();
        doWindowing();
    }

    /**
     * 不做端点检测的构造函数
     * @param originalSignal extracted PCM data
     * @param normalization 是否归一化的开关
     * @param samplePerFrame 帧长
     * @param shiftedFrame 帧移
     */
    public PreProcess(float [] originalSignal, boolean normalization, boolean endDetection,
                      int samplePerFrame, int shiftedFrame, int windowType)
    {
        this.originalSignal = originalSignal;
        // 幅值归一化
        if (normalization) // 如果归一化true
            this.normalizedSignal = normalizePCM();
        else
            this.normalizedSignal = this.originalSignal;
        // 端点检测
        if (endDetection) // 如果做端点检测
        {
            epd = new EndPointDetection(this.normalizedSignal, this.samplingRate);
            afterEndPtDetection = epd.doEndPointDetection();
        }
        else
            this.afterEndPtDetection = this.normalizedSignal;

        // 分帧
        doFraming(samplePerFrame, shiftedFrame);
        // 加窗
        doWindowing(samplePerFrame, windowType);
    }

    /**
     * 返回原始信号
     * @return
     */
    public float [] getOriginalSignal() {
        return originalSignal;
    }

    /**
     * 返回归一化后的信号
     * @return
     */
    public float [] getNormalizedSignal(){
        return normalizedSignal;
    }

    /**
     * 返回端点检测后的信号
     * @return
     */
    public float [] getAfterEndPtDetection(){
        return afterEndPtDetection;
    }

    /**
     * 返回分帧后的信号
     * @return
     */
    public float [][] getFramedSignal(){
        return framedSignal;
    }

    /**
     * Internal method, normalization: x[i] = x[i]/max{X}
     */
    private float [] normalizePCM() {
        float [] tmpSignal = new float[originalSignal.length];
        float max = originalSignal[0];
        for (int i = 1; i < originalSignal.length; i++) {
            if (max < Math.abs(originalSignal[i])) {
                max = Math.abs(originalSignal[i]);
            }
        }
        // System.out.println("max PCM =  " + max);
        for (int i = 0; i < originalSignal.length; i++) {
            tmpSignal[i] = originalSignal[i] / max;
        }
        return tmpSignal;
    }

    /**
     * divides the whole signal into frames of samplerPerFrame
     */
    private void doFraming()
    {
        // calculate no of frames, for framing
        noOfFrames = 2 * afterEndPtDetection.length / samplePerFrame - 1;
        System.out.println("noOfFrames       " + noOfFrames + "  samplePerFrame     " + samplePerFrame
        				+ "  EPD length   " + afterEndPtDetection.length);
        framedSignal = new float[noOfFrames][samplePerFrame];
        for (int i = 0; i < noOfFrames; i++) {
            int startIndex = (i * samplePerFrame / 2);
            for (int j = 0; j < samplePerFrame; j++) {
                framedSignal[i][j] = afterEndPtDetection[startIndex + j];
            }
        }
    }

    /**
     * 自定义帧长和帧移的分帧函数
     * @param samplePerFrame 帧长
     * @param shiftedFrame 帧移
     */
    private void doFraming(int samplePerFrame, int shiftedFrame)
    {
        // 一共分成多少帧 (总帧数-帧长+帧移)/帧移
        noOfFrames = (this.afterEndPtDetection.length - samplePerFrame + shiftedFrame) / shiftedFrame;
        System.out.println("Number of frames:\t" + noOfFrames + "\nsamplePerFrame:\t" + samplePerFrame
                + "\nShiftedFrame:\t" + shiftedFrame + "\nEPD length   " + afterEndPtDetection.length);
        framedSignal = new float[noOfFrames][samplePerFrame];
        for (int i = 0; i < noOfFrames; i++) {
            int startIndex = (i * shiftedFrame);
            for (int j = 0; j < samplePerFrame; j++) {
                framedSignal[i][j] = afterEndPtDetection[startIndex + j];
            }
        }
    }

    /**
     * does hamming window on each frame
     */
    private void doWindowing()
    {
        // prepare hammingWindow
        hammingWindow = new float[samplePerFrame + 1];
        // prepare for through out the data
        for (int i = 1; i <= samplePerFrame; i++) {

            hammingWindow[i] = (float) (0.54 - 0.46 * (Math.cos(2 * Math.PI * i / samplePerFrame)));
        }
        // do windowing
        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 0; j < samplePerFrame; j++) {
                framedSignal[i][j] = framedSignal[i][j] * hammingWindow[j + 1];
            }
        }
    }

    /**
     * 根据窗函数加窗
     * @param windowType 窗函数类型
     * @param samplePerFrame 帧长
     */
    private void doWindowing(int samplePerFrame, int windowType)
    {
        float [] window = new float[samplePerFrame + 1]; // +1的目的是方便计算
        // 根据窗函数类型，进行加窗
        if (windowType == Indicator.HANNING_WINDOW)
        {
            for (int i = 1; i <= samplePerFrame; i++)
            {
                window[i] = (float)(0.5 * (1.0 - Math.cos(2 * Math.PI * i / (samplePerFrame))));
                // Note: Matlab的hanning函数是除以(帧长+1)
            }
        }
        else if (windowType == Indicator.HAMMING_WINDOW)
        {
            for (int i = 1; i <= samplePerFrame; i++)
            {
                window[i] = (float)(0.54 - 0.46 * Math.cos(2 * Math.PI * i / samplePerFrame));
            }
        }
        else if (windowType == Indicator.RECT_WINDOW)
        {
            for (int i = 1; i <= samplePerFrame; i++)
            {
                window[i] = 1;
            }
        }
        else
        {
            System.out.println("未知的窗函数类型!");
        }

        System.out.println("这里被执行了!");
        System.out.println("窗函数长度：" + window.length);

        // do windowing
        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 0; j < samplePerFrame; j++) {
                framedSignal[i][j] = framedSignal[i][j] * window[j + 1];
            }
        }
    }
}

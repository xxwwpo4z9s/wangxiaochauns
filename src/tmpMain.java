import audio.PreProcess;
import audio.WaveData;
import feature.Energy;
import utils.DrawPanel;
import utils.Indicator;

import javax.swing.*;
import java.io.File;

public class tmpMain
{
    private WaveData waveData;

    public tmpMain()
    {
        this.waveData = new WaveData();
    }

    public static void main(String[] args)
    {
        tmpMain tmp = new tmpMain();
        // test
        float [] arrAmp;
        String filename = "data/bluesky3.wav";
        File file = new File(filename);
        arrAmp = tmp.waveData.extractAmplitudeFromFile(file);
        tmp.waveData.printAudioFormat();
        // Note: 这里读进来的是原始幅值，和Matlab的AudioRead函数不同. AudioRead函数已经做了归一化
        //for (float fmp : arrAmp)
        //    System.out.print(fmp + " ");

        JFrame frame_original = new JFrame();
        DrawPanel drawPanel_original = new DrawPanel(arrAmp);
        frame_original.add(drawPanel_original);
        frame_original.setTitle("语音波形图");
        frame_original.setSize(800, 400);
        frame_original.setLocationRelativeTo(null);
        frame_original.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_original.setVisible(true);

        // 预处理
        PreProcess pro = new PreProcess(arrAmp, false, false,
                200, 80, Indicator.HANNING_WINDOW);
        //float [] norAmp = pro.getNormalizedSignal();
        // Note: 归一化了，和Matlab的AudioRead函数功能一致，已校验
        //for (float fmp : norAmp)
        //  System.out.print(fmp + " ");

        float [][] framedSignal = pro.getFramedSignal();
        Energy energy = new Energy(framedSignal[0].length);
        // 计算短时能量
        double [] energyd;
        energyd = energy.calcEnergy(framedSignal);
        // 打印短时能量的长度
        System.out.println("短时能量长度:" + energyd.length);
        float [] energyf = new float[energyd.length];
        for (int i = 0; i < energyd.length; i++)
        {
            energyf[i] = (float)energyd[i];
        }
        // 打印计算的能量
        //for (float fmp : energyf)
        //    System.out.print(fmp + " ");

        JFrame frame_energy = new JFrame();
        DrawPanel drawPanel_energy = new DrawPanel(energyf);
        frame_energy.add(drawPanel_energy);
        frame_energy.setTitle("短时能量图");
        frame_energy.setSize(800, 400);
        frame_energy.setLocationRelativeTo(null);
        frame_energy.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_energy.setVisible(true);

    }

}

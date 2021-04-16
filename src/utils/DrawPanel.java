package utils;

import javax.swing.*;
import java.awt.*;

public class DrawPanel extends JPanel
{
    private float [] data = null;
    private int dataType = -1;
    public DrawPanel(float [] data){
        this.data = data;
        this.dataType = dataType;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int ww = getWidth();
        int hh = getHeight();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, ww, hh);
        int len = data.length;
        int step = len / ww;
        if (step == 0)
            step = 1;
        int inverse_step = ww / len;
        if (inverse_step == 0)
            inverse_step = 1;
        int prex = 0;
        int prey = 0; // 上一个坐标
        int x = 0;
        int y = 0; // 当前坐标
        g.setColor(Color.BLACK);
        for (int i = 0; i < ww; i++)
        {
            x = i * inverse_step;
            if (i * step < len)
            {
                y = (int)(hh * (0.75- 0.5 * ((data[i * step] - findMaxMin()[1]) / (findMaxMin()[0] - findMaxMin()[1]))));
                if (i != 0)
                {
                    g.drawLine(x, y, prex, prey);
                }
            }
            // Log
            // System.out.print(y);
            // System.out.print(" ");
            //System.out.print(i * step + " ");
            prex = x;
            prey = y;
        }
    }

    private float[] findMaxMin()
    {
        float max = data[0];//将数组的第一个元素赋给max
        float min = data[0];//将数组的第一个元素赋给min
        for (int i = 1; i< data.length; i++){//从数组的第二个元素开始赋值，依次比较
            if(data[i] > max){//如果arr[i]大于最大值，就将arr[i]赋给最大值
                max = data[i];
            }
            if(data[i] < min){//如果arr[i]小于最小值，就将arr[i]赋给最小值
                min = data[i];
            }
        }
        //System.out.println("最大值是:"+max);
        //System.out.println("最小值是:"+min);
        return new float[]{max, min};
    }
}

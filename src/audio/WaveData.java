package audio;

import javax.sound.sampled.*;
import java.io.*;

/**
 * Extract PCM data from audio byte array
 *
 * @author wanggang
 * @version May 13, 2015
 */
public class WaveData
{
    private byte[] arrFile;
    private byte[] audioBytes;
    private float[] audioData;
    private FileOutputStream fos;
    private ByteArrayInputStream bis;
    private AudioInputStream audioInputStream;
    private AudioFormat format; // 音频格式,可进一步返回帧率
    private double durationSec;

    public WaveData(){}

    public byte[] getAudioBytes(){return audioBytes;}
    public float[] getAudioData(){return audioData;}
    public double getDurationSec(){return durationSec;}
    public AudioFormat getAudioFormat(){return format;}

    // 打印读取音频文件的格式
    public void printAudioFormat()
    {
        System.out.println(format.getEncoding() + " => "
                + format.getSampleRate()+" hz, "
                + format.getSampleSizeInBits() + " bit, "
                + format.getChannels() + " channel, "
                + format.getFrameRate() + " frames/second, "
                + format.getFrameSize() + " bytes/frame");
    }

    /**
     * Extract amplitude array from wavefile
     * @param waveFile
     * @return
     */
    public float[] extractAmplitudeFromFile(File waveFile)
    {
        // Create FileInputSteam
        try (FileInputStream fis = new FileInputStream(waveFile);)
        {
            // Create byte array from wavefile
            arrFile = new byte[(int)waveFile.length()];
            fis.read(arrFile);
        }
        catch (Exception e)
        {
            System.err.println("SomeException: " + e.toString());
        }
        return extractAmplitudeFromFileByteArray(arrFile);
    }

    /**
     * Internal method for extractAmplitudeFromFile
     * @param arrFile
     * @return
     */
    private float[] extractAmplitudeFromFileByteArray(byte[] arrFile)
    {
        bis = new ByteArrayInputStream(arrFile);
        return extractAmplitudeFromFileByteArrayInputStream(bis); // callback
    }

    /**
     * Internal method for extracting amplitude array
     * the format we are using: 16bit, 22kHz, 1 channel, littleEndian
     * @param bis
     * @return PCM audio data
     */
    private float[] extractAmplitudeFromFileByteArrayInputStream(ByteArrayInputStream bis)
    {
        try
        {
            audioInputStream = AudioSystem.getAudioInputStream(bis);
        }
        catch (UnsupportedAudioFileException e)
        {
            System.out.println("Unsupported file type, during extracting amplitude.");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println("IOException during extracting amplitude.");
            e.printStackTrace();
        }

        float milliseconds = (long) ((audioInputStream.getFrameLength() * 1000)
                / audioInputStream.getFormat().getFrameRate());
        durationSec = milliseconds / 1000.0;
        return extractFloatDataFromAudioInputStream(audioInputStream);
    }

    private float[] extractFloatDataFromAudioInputStream(AudioInputStream audioInputStream)
    {
        format = audioInputStream.getFormat();
        audioBytes = new byte[(int)(audioInputStream.getFrameLength() * format.getFrameSize())];
        // calculate durationSec
        float milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat()
                .getFrameRate());
        durationSec = milliseconds / 1000.0;
        System.out.println("The current signal has duration "+durationSec+" Sec");
        try {
            audioInputStream.read(audioBytes);
        } catch (IOException e) {
            System.out.println("IOException during reading audioBytes");
            e.printStackTrace();
        }
        return extractFloatDataFromAmplitudeByteArray(format, audioBytes);
    }

    public float[] extractFloatDataFromAmplitudeByteArray(AudioFormat format, byte[] audioBytes) {
        // convert
        audioData = null;
        if (format.getSampleSizeInBits() == 16) {
            int nlengthInSamples = audioBytes.length / 2;
            audioData = new float[nlengthInSamples];
            if (format.isBigEndian()) {
                for (int i = 0; i < nlengthInSamples; i++) {
                    /* First byte is MSB (high order) */
                    int MSB = audioBytes[2 * i];
                    /* Second byte is LSB (low order) */
                    int LSB = audioBytes[2 * i + 1];
                    audioData[i] = MSB << 8 | (255 & LSB);
                }
            } else {
                for (int i = 0; i < nlengthInSamples; i++) {
                    /* First byte is LSB (low order) */
                    int LSB = audioBytes[2 * i];
                    /* Second byte is MSB (high order) */
                    int MSB = audioBytes[2 * i + 1];
                    audioData[i] = MSB << 8 | (255 & LSB);
                }
            }
        } else if (format.getSampleSizeInBits() == 8) {
            int nlengthInSamples = audioBytes.length;
            audioData = new float[nlengthInSamples];
            if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = audioBytes[i];
                }
            } else {
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = audioBytes[i] - 128;
                }
            }
        }// end of if..else
        System.out.println("PCM Returned===============" + audioData.length);
        return audioData;
    }

    /**
     * Save to file
     * @param name
     * @param fileType the file type
     * @param audioInputStream
     */
    public void saveToFile(String name, AudioFileFormat.Type fileType,
                           AudioInputStream audioInputStream)
    {
        File myFile = new File(name);
        if (!myFile.exists())
            myFile.mkdir();

        if (audioInputStream == null)
        {
            return;
        }
        // reset to the beginnning of the captured data
        try {
            audioInputStream.reset();
        } catch (Exception e) {
            return;
        }
        myFile = new File(name + ".wav");
        int i = 0;
        while (myFile.exists()) {
            String temp = String.format(name + "%d", i++);
            myFile = new File(temp + ".wav");
        }
        try {
            if (AudioSystem.write(audioInputStream, fileType, myFile) == -1)
            {
            }
        } catch (Exception ex) {
        }
        System.out.println(myFile.getAbsolutePath());
        // JOptionPane.showMessageDialog(null, "File Saved !", "Success",
        // JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Save byte array to file
     * @param fileName
     * @param arrFile
     */
    public void saveFileByteArray(String fileName, byte[] arrFile) {
        try {
            fos = new FileOutputStream(fileName);
            fos.write(arrFile);
            fos.close();
        } catch (Exception ex) {
            System.err.println("Error during saving wave file " + fileName + " to disk" + ex.toString());
        }
        System.out.println("WAV Audio data saved to " + fileName);
    }
}

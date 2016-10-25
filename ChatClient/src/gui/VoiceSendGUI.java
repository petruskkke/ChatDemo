package gui;

import client.Client;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * This panel use send voice message
 */
public class VoiceSendGUI {

    private JLabel tagLabel;

    private JButton finishButton;

    private JPanel Record;

    private String filename = null;

    AudioFormat af = null;

    TargetDataLine td = null;

    SourceDataLine sd = null;

    ByteArrayInputStream bais = null;

    ByteArrayOutputStream baos = null;

    AudioInputStream ais = null;

    Boolean stopflag = false;

    /**
     * Init this panel
     *
     * @param frame
     * @param client
     * @param receiver
     */
    public VoiceSendGUI(JFrame frame, Client client, String receiver) {
        try {
            capture();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        finishButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                stopflag = true;
                try {
                    save();
                    client.voiceMessSend(receiver, filename);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                frame.dispose();
            }
        });
    }

    /**
     * Show this panel in window
     *
     * @param client
     * @param sendVoiceButton
     * @param receiver
     */
    public static void showGUI(Client client, JButton sendVoiceButton, String receiver) {
        JFrame frame = new JFrame("VoiceSendGUI");
        frame.setContentPane(new VoiceSendGUI(frame, client, receiver).Record);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        Point loc = sendVoiceButton.getLocationOnScreen();
        frame.setLocation(loc);
        frame.setVisible(true);
    }

    /**
     * Capture voice from computer
     *
     * @throws LineUnavailableException
     */
    public void capture() throws LineUnavailableException {
        stopflag = false;
        try {
            af = getAudioFormat();
        } catch (Exception e) {
            return;
        }
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
        td = (TargetDataLine) (AudioSystem.getLine(info));
        try {
            td.open(af);
        } catch (LineUnavailableException e) {
            return;
        }
        td.start();
        RecordThread record = new RecordThread();
        Thread t = new Thread(record);
        t.start();
    }

    /**
     * Save voice in file
     *
     * @throws IOException
     */
    public void save() throws IOException {
        af = getAudioFormat();
        if (baos != null) {
            byte audioData[] = baos.toByteArray();
            bais = new ByteArrayInputStream(audioData);
            ais = new AudioInputStream(bais, af, audioData.length / af.getFrameSize());
            File file = null;
            try {
                File filePath = new File(System.getProperty("user.dir") + "/src/resources/voice");
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }
                filename = System.currentTimeMillis() + ".mp3";
                file = new File(filePath + "/" + filename);
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ais != null) {
                        ais.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Format voice message
     *
     * @return
     */
    public AudioFormat getAudioFormat() {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 8000f;
        int sampleSize = 16;
        boolean bigEndian = true;
        int channels = 1;
        return new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
    }

    /**
     * A new thread for voice
     */
    class RecordThread implements Runnable {
        byte bts[] = new byte[10000];

        @Override
        public void run() {
            baos = new ByteArrayOutputStream();
            try {
                stopflag = false;
                while (!stopflag) {
                    int i = td.read(bts, 0, bts.length);
                    if (i > 0) {
                        baos.write(bts, 0, i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (baos != null) {
                        baos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    td.drain();
                    td.close();
                }
            }
        }
    }
}



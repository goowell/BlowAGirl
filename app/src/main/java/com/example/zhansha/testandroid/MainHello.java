package com.example.zhansha.testandroid;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import java.lang.reflect.Array;
import java.util.Arrays;

import static android.os.SystemClock.sleep;

public class MainHello extends AppCompatActivity {

    private boolean start = false;
    private int gf_number = 0;

    private AudioRecord ar;
    private int bs = 100;
    private static int SAMPLE_RATE_IN_HZ = 8000;
    private int number = 1;
    private int tal = 1;
    private long currenttime;
    private long endtime;
    private long time = 1;
    //到达该值之后 触发事件
    private static int BLOW_ACTIVI=3000;

    private Button btn;
    private TextView txtV;
    boolean isblow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hello);
        btn = (Button) findViewById(R.id.confirm);
        txtV = (TextView) findViewById(R.id.mainText);

        bs = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        ar = findAudioRecord();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start)
                {
                    start = ! start;
                    txtV.setText("You clicked Gaga!!");
                }
                else
                {
                    start = ! start;
                    txtV.setText("Gaga is gone!!");
                    isblow = false;
                }
            Moniting();
//                if (gf_number<4)
//                {
//                    gf_number++;
//                    Show();
//
//                }
//                else{
//                    gf_number=0;
//                }

            }
        });


    }
    private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        Log.e("xxxxxxxxxx", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        Log.e("main", rate + "Exception, keep trying.",e);
                    }
                }
            }
        }
        return null;
    }
    public void Moniting() {
        try {
            ar.startRecording();
            isblow = true;
            txtV.setText("startRecording");
            // 用于读取的 buffer
            byte[] buffer = new byte[bs];
            while (isblow) {
                number++;
                sleep(8);
                currenttime = System.currentTimeMillis();
                int r = ar.read(buffer, 0, bs) + 1;
                int v = 0;
                Log.e("XXXXXXXXX", "Moniting: "+ Arrays.toString(buffer));
                for (int i = 0; i < buffer.length; i++) {
                    v += (buffer[i] * buffer[i]);
                }
                int value = v / (int) r;
                tal = tal + value;
                endtime = System.currentTimeMillis();
                time = time + (endtime - currenttime);

                if (time >= 500 || number > 5) {

                    int total = tal / number;
                    if (total > BLOW_ACTIVI) {
                        //发送消息通知到界面 触发动画
                        txtV.setText("startRecording"+total);

                        //利用传入的handler 给界面发送通知
//                        BlowActivity.i+=1;
//                        handler.sendEmptyMessage(0); //改变i的值后，发送一个空message到主线程
                        isblow=false;
                        //
                        number = 1;
                        tal = 1;
                        time = 1;
                    }
                }

            }
            ar.stop();
            ar.release();
            bs=100;


        } catch (Exception e) {
            e.printStackTrace();
            txtV.setText(e.getLocalizedMessage());
        }
    }

    private void Show() {
        ImageView iv = (ImageView) findViewById(R.id.gfV);
        switch (gf_number) {
            case 1:
                iv.setImageResource(R.drawable.f1);
                break;
            case 2:
                iv.setImageResource(R.drawable.f2);
                break;
            case 3:
                iv.setImageResource(R.drawable.f3);
                break;
            case 4:
                iv.setImageResource(R.drawable.f4);
                break;
        }
    }

}

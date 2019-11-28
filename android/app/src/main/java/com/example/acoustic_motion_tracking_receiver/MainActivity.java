package com.example.acoustic_motion_tracking_receiver;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    //48K采样率
    int SamplingRate = 44100;
    //格式：单声道
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    //16Bit
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    //是否在录制
    boolean isRecording = false;

    int inputBufferSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button start_button, stop_button;
        final GraphView signal_view;

        start_button = (Button)findViewById(R.id.start);
        stop_button = (Button)findViewById(R.id.stop);

        signal_view = (GraphView)findViewById(R.id.signal_graph);

        stop_button.setEnabled(false);

        final int limit_len = 500000;
        final double[] data_container = new double[limit_len];

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_button.setEnabled(false);
                stop_button.setEnabled(true);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        double fs = 1000, f1 = 40/fs, f2 = 80/fs;
//                        int m=800;
//                        filter bp_filter = new filter(m, f1, f2, "bp", "hamming");
//                        double[] h_bp = bp_filter.get_coeff();
//                        Complex[] signal = new Complex[(int)fs*10];
//                        double laps = 10/(10*fs-1), t, f1_demo = 10, f2_demo = 75, f3_demo = 150;
//
//                        for(int i=0; i<signal.length; i++)
//                        {
//                            t = i*laps;
//                            signal[i] =new Complex(Math.sin(2*Math.PI*f1_demo*t)+Math.sin(2*Math.PI*f2_demo*t)+Math.sin(2*Math.PI*f3_demo*t));
//                        }
//                        Complex[] bp_filtered_sig = new Complex[(int)fs*10];
//                        double[] xs = new double[signal.length];
//                        for(int i=0; i<signal.length; i++)
//                        {
//                            bp_filtered_sig[i] = new Complex(bp_filter.filter(signal[i].real()));
//                            xs[i] = i*(fs/(signal.length-1));
//                        }
//                        Complex[] fft_sig = SignalProcessingUtil.FFT(signal), filtered_fft = SignalProcessingUtil.FFT(bp_filtered_sig);
//                        double[] ys = new double[signal.length];
//                        for(int i=0; i<signal.length; i++)
//                        {
//                            ys[i] = filtered_fft[i].abs();
//                        }
//                        draw_line_graph(signal_view, xs, ys);
                        int data_len = StartRecord(data_container, limit_len);
                        double[] received_data = new double[data_len];
                        for(int i=0; i<data_len; i++)
                        {
                            received_data[i] = data_container[i];
                        }
                        double[] delta_distance = FMCW.get_distance(received_data);
                        draw_line_graph(signal_view, delta_distance);
                    }
                });
                thread.start();
            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
                start_button.setEnabled(true);
                stop_button.setEnabled(false);
            }
        });
    }

    private void draw_line_graph(GraphView line_view, double[] ys)
    {
        line_view.removeAllSeries();
        int y_len = ys.length;
        int len = y_len;
        double min_x=0, min_y=0, max_x=y_len, max_y=0;
        DataPoint[] data = new DataPoint[len];
        for(int i=0; i<len; i++)
        {
            data[i] = new DataPoint(i, ys[i]);
            min_y = min_y>ys[i]?ys[i]:min_y;
            max_y = max_y<ys[i]?ys[i]:max_y;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(data);
        line_view.addSeries(series);
        line_view.getViewport().setMinX(min_x);
        line_view.getViewport().setMaxX(max_x);
        line_view.getViewport().setMinY(min_y);
        line_view.getViewport().setMaxY(max_y);
    }

    private void draw_line_graph(GraphView line_view, double[] xs, double[] ys)
    {
        line_view.removeAllSeries();
        int x_len = xs.length, y_len = ys.length;
        int len = x_len<=y_len?x_len:y_len;
        double min_x=0, min_y=0, max_x=0, max_y=0;
        DataPoint[] data = new DataPoint[len];
        for(int i=0; i<len; i++)
        {
            data[i] = new DataPoint(xs[i], ys[i]);
            min_x = min_x>xs[i]?xs[i]:min_x;
            max_x = max_x<xs[i]?xs[i]:max_x;
            min_y = min_y>ys[i]?ys[i]:min_y;
            max_y = max_y<ys[i]?ys[i]:max_y;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(data);
        line_view.addSeries(series);
        line_view.getViewport().setMinX(min_x);
        line_view.getViewport().setMaxX(max_x);
        line_view.getViewport().setMinY(min_y);
        line_view.getViewport().setMaxY(max_y);
    }

    public int StartRecord(double[] data_container, int limit_len)
    {
        inputBufferSize = AudioRecord.getMinBufferSize(SamplingRate, channelConfiguration, audioEncoding);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SamplingRate, channelConfiguration, audioEncoding, inputBufferSize);
        int size_short = inputBufferSize/2, size_float = inputBufferSize/4;
        short[] buffer = new short[size_short];
        int start_pos = 0;

        try{
//            FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/record.wav");

            isRecording = true;
            audioRecord.startRecording();
            while (isRecording) {
//                int bufferReadResult = audioRecord.read(buffer, 0, inputBufferSize);

                int bufferReadResult = audioRecord.read(buffer, 0, size_short);
                for(int i=0; i<bufferReadResult; i++)
                {
                    data_container[start_pos+i] = (double) buffer[i];
                }
                start_pos+=bufferReadResult;
                if(start_pos+bufferReadResult>=limit_len)
                    isRecording = false;
            }
//            byte[] to_write = new byte[start_pos*2];
//            for(int i=0; i<start_pos; i++)
//            {
//                to_write[2*i] = (byte)(data_container[i]&0xff);
//                to_write[2*i+1] = (byte)((data_container[i]>>8)&0xff);
//            }
//
//            long audio_len = start_pos*2;
//
//            long data_len = audio_len + 36;
//            //为wav文件写文件头
//            WriteWaveFileHeader(out, audio_len, data_len, (long)SamplingRate, 1, (long)(16 * SamplingRate * 1 / 8));
//            out.write(to_write);
//            out.close();
            audioRecord.stop();
        }
        catch (Throwable t) {
            Log.e("MainActivity", "录音失败");
        }

        return start_pos;
    }
}

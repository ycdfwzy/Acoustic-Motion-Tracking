package com.example.acoustic_motion_tracking_receiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
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
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity {

    //44.1K采样率
    int SamplingRate = 44100;
    //格式：单声道
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    //16Bit
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    //是否在录制
    boolean isRecording = false;

    BlockingQueue<Double> queue = new LinkedBlockingQueue<>();
    Thread producer_thread, consumer_thread;
    double[] acc_distance = null;

    int inputBufferSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetPermission();

        final Button start_button, stop_button;
        final GraphView signal_view;

        start_button = (Button)findViewById(R.id.start);
        stop_button = (Button)findViewById(R.id.stop);

        signal_view = (GraphView)findViewById(R.id.signal_graph);

        stop_button.setEnabled(false);

        // final int limit_len = 500000;
        // final double[] data_container = new double[limit_len];

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_button.setEnabled(false);
                stop_button.setEnabled(true);
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        int data_len = StartRecord(data_container, limit_len);
//                        double[] received_data = new double[data_len];
//                        System.arraycopy(data_container, 0, received_data, 0, data_len);
//                        double[] delta_distance = FMCW.get_distance(received_data);
//                        draw_line_graph(signal_view, delta_distance);
//                    }
//                });
//                thread.start();
                producer_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // record data
                        StartRecord();
                    }
                });
                consumer_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // analyze data
                        Queue<Double> buffer = new LinkedList<>();
                        try {
                            // get start
                            for (int i = 0; (i < FMCW.total * FMCW.len * 3) && isRecording; i++) {
                                if (!queue.isEmpty()) {
                                    buffer.add(queue.take());
                                } else
                                    i--;
                            }
                            if (!isRecording) return;
                            Object[] tmp = buffer.toArray();
                            double[] pre_data = new double[FMCW.total * FMCW.len * 3];
                            for (int i = 0; i < FMCW.total * FMCW.len * 3; i++)
                                pre_data[i] = (double) tmp[i];
                            int start_idx = FMCW.get_start(pre_data);
                            for (int i = 0; i < start_idx; i++) {
                                buffer.poll();
                            }

                            // analyze
                            double[] received_data = new double[FMCW.total * FMCW.len];
                            while (isRecording) {
                                while (isRecording && buffer.size() < FMCW.total * FMCW.len) {
                                    buffer.add(queue.take());
                                }
                                if (!isRecording) return;

                                for (int i = 0; i < FMCW.total * FMCW.len; i++) {
                                received_data[i] = buffer.poll();
                            }
                            double[] delta_distance = FMCW.get_distance2(received_data);
                            if (acc_distance == null) {
                                acc_distance = delta_distance;
                            } else
                            {
                                double[] temp = acc_distance;
                                acc_distance = new double[temp.length + delta_distance.length];
                                System.arraycopy(temp, 0, acc_distance, 0, temp.length);
                                System.arraycopy(delta_distance, 0, acc_distance, temp.length, delta_distance.length);
                            }
                            draw_line_graph(signal_view, acc_distance);
                        }

                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });
                isRecording = true;
                producer_thread.start();
                consumer_thread.start();
            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
                start_button.setEnabled(true);
                stop_button.setEnabled(false);
                producer_thread.interrupt();
                // consumer_thread.interrupt();
            }
        });
    }

    private void GetPermission() {

        /*在此处插入运行时权限获取的代码*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!=
                PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED
        )
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
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

    public void StartRecord(/*double[] data_container, int limit_len*/) {
        inputBufferSize = AudioRecord.getMinBufferSize(SamplingRate, channelConfiguration, audioEncoding);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SamplingRate, channelConfiguration, audioEncoding, inputBufferSize);
        int size_short = inputBufferSize/2, size_float = inputBufferSize/4;
        short[] buffer = new short[size_short];
//        int start_pos = 0;

        try {
            audioRecord.startRecording();
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, size_short);
                for (int i = 0; i < bufferReadResult; i++) {
//                    data_container[start_pos+i] = (double) buffer[i];
                    queue.put((double) buffer[i]);
                }
//                start_pos+=bufferReadResult;
//                if(start_pos+bufferReadResult>=limit_len)
//                    isRecording = false;
            }
            audioRecord.stop();
        } catch (InterruptedException e) {

        }
        catch (Throwable t) {
            Log.e("MainActivity", "录音失败");
        }

//        return start_pos;
    }
}

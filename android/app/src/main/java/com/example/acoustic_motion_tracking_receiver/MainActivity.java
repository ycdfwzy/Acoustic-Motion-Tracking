package com.example.acoustic_motion_tracking_receiver;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button start_button;
        final GraphView signal_view, freq_view;

        start_button = (Button)findViewById(R.id.start);

        signal_view = (GraphView)findViewById(R.id.signal_graph);
//        freq_view = (GraphView)findViewById(R.id.freq_graph);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        double fs = 1000, f1 = 40/fs, f2 = 80/fs;
                        int m=800;
                        filter bp_filter = new filter(m, f1, f2, "bp", "hamming");
                        double[] h_bp = bp_filter.get_coeff();
                        Complex[] signal = new Complex[(int)fs*10];
                        double laps = 10/(10*fs-1), t, f1_demo = 10, f2_demo = 75, f3_demo = 150;

                        for(int i=0; i<signal.length; i++)
                        {
                            t = i*laps;
                            signal[i] =new Complex(Math.sin(2*Math.PI*f1_demo*t)+Math.sin(2*Math.PI*f2_demo*t)+Math.sin(2*Math.PI*f3_demo*t));
                        }
                        Complex[] bp_filtered_sig = new Complex[(int)fs*10];
                        double[] xs = new double[signal.length];
                        for(int i=0; i<signal.length; i++)
                        {
                            bp_filtered_sig[i] = new Complex(bp_filter.filter(signal[i].real()));
                            xs[i] = i*(fs/(signal.length-1));
                        }
                        Complex[] fft_sig = SignalProcessingUtil.FFT(signal), filtered_fft = SignalProcessingUtil.FFT(bp_filtered_sig);
                        double[] ys = new double[signal.length];
                        for(int i=0; i<signal.length; i++)
                        {
                            ys[i] = filtered_fft[i].abs();
                        }
                        draw_line_graph(signal_view, xs, ys);
//                        draw_line_graph(freq_view, xs, bp_filtered_sig);
                    }
                });
                thread.start();
            }
        });
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
}

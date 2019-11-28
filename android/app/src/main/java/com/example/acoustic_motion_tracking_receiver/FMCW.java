package com.example.acoustic_motion_tracking_receiver;

import java.util.Arrays;

public class FMCW {
    public static int total = 88;
    public static double fs = 44100;
    public static double T = 0.04;
    public static double f0 = 18000;
    public static double f1 = 20500;
    public static double[] chirp_data = SignalProcessingUtil.chirp_linear(fs, f0, T, f1);
    public static int FFTlen = 1024 * 16;  // reduce FFTlen if too slow

    public static double[] pseudo_T = null;

    public static double[] get_distance(double[] received_signal) {
        int len = ((int)(fs*T)+1)*2;
        // pseudo-transmitted signal
        if (pseudo_T == null) {
            pseudo_T = new double[len*total];
            for (int i = 0; i < total; i++) {
                System.arraycopy(chirp_data, 0, pseudo_T, len*i, chirp_data.length);
            }
        }

        // filtering
        double[] filtered_signal = filtering(received_signal, 800, f0-1000, f1+1000, fs, "bp", "hamming");

        // find start position
        int start_idx = 0;
        double[] c = SignalProcessingUtil.xcorr(filtered_signal, chirp_data);
        double max_c = max(c);
        for (int i = 0; i < c.length; i++) {
            if (c[i] > max_c/2) {
                start_idx = i + 1 - filtered_signal.length;
                break;
            }
        }

        // dot product
        double[] s = new double[len*total];
        for (int i = 0; i < s.length; i++)
            s[i] = pseudo_T[i] * filtered_signal[start_idx+i];

        // fft to get distance
        Complex[] tmp_s = new Complex[FFTlen];
        for (int i = len/2; i < FFTlen; i++)
            tmp_s[i] = new Complex();
        double[] delta_distance = new double[total];
        for (int i = 0; i < total; i++) {
            for (int j = 0; j < len/2; j++)
                tmp_s[j] = new Complex(s[i*len+j]);
            Complex[] FFTout = SignalProcessingUtil.FFT(tmp_s);
            int max_arg = maxarg(FFTout);
            delta_distance[i] = (max_arg+1) * 340 * T / (f1-f0);
        }

        return delta_distance;
    }

    public static double[] filtering(double[] raw_signal, int taps, double f1, double f2, double fs, String type, String window) {
        filter bp_filter = new filter(taps, f1/fs, f2/fs, "bp", "hamming");
        double[] h_bp = bp_filter.get_coeff();

        double[] bp_filtered_sig = new double[raw_signal.length];
        for(int i=0; i<raw_signal.length; i++) {
            if (i%100==0) System.out.println(i);
            bp_filtered_sig[i] = bp_filter.filter(raw_signal[i]);
        }
        return bp_filtered_sig;
    }

    public static double max(double[] data) {
        double res = data[0];
        for (double d: data)
            if (d > res) res = d;
        return res;
    }

    public static int maxarg(Complex[] data) {
        double max_data = data[0].abs();
        int max_arg = 0;
        for (int i = 0; i < data.length/2; i++)
            if (data[i].abs() > max_data) {
                max_data = data[i].abs();
                max_arg = i;
            }
        return max_arg;
    }

}

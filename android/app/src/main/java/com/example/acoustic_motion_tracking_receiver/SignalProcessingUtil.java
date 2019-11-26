package com.example.acoustic_motion_tracking_receiver;

import static java.lang.Math.max;

public class SignalProcessingUtil {
    public static Complex[] FFT(Complex[] ori)
    {
        int ori_len = ori.length;
        int N = next2pow(ori_len);

        if(N==1)
        {
            return new Complex[] {ori[0]};
        }
        Complex[] x = new Complex[N];
        for(int i=0; i<N; i++)
        {
            if(i<ori_len)
                x[i] = ori[i];
            else
                x[i] = new Complex(0, 0);
        }

        Complex[] even = new Complex[N/2];
        for (int i = 0; i < N/2; i++)
        {
            even[i] = x[2*i];
        }
        Complex[] q = FFT(even);
        Complex[] odd  = even;
        for (int i = 0; i < N/2; i++) {
            odd[i] = x[2*i + 1];
        }
        Complex[] r = FFT(odd);

        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }

    public static Complex[] IFFT(Complex[] x) {
        int ori_len = x.length;
        long N = next2pow(ori_len);
        Complex[] y = new Complex[(int)N];

        // take conjugate
        for (int i = 0; i < N; i++) {
            if(i<ori_len)
                y[i] = x[i].conjugate();
            else
                y[i] = new Complex(0,0);
        }

        // compute forward FFT
        y = FFT(y);

        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].times(1.0 / N);
        }

        return y;
    }

    public static int next2pow(int n)
    {
        int i=0, ori = n;
        for(; i<32 && n!=0; i++)
        {
            n = n>>1;
        }
        int res = 1<<(i-1);
        if(res<ori)
        {
            res = res<<1;
        }
        return res;
    }

    // calculate convolution of X × Y
    public static double[] conv(double[] X, double[] Y) {
        int n = max(X.length, Y.length);
        int N = next2pow(n);
        Complex[] new_X = new Complex[N+N];
        Complex[] new_Y = new Complex[N+N];
        for (int i = 0; i < X.length; i++)
            new_X[i] = new Complex(X[i]);
        for (int i = X.length; i < N+N; i++)
            new_X[i] = new Complex();
        for (int i = 0; i < Y.length; i++)
            new_Y[i] = new Complex(Y[i]);
        for (int i = Y.length; i < N+N; i++)
            new_Y[i] = new Complex();

        Complex[] fft_X = FFT(new_X);
        Complex[] fft_Y = FFT(new_Y);
        Complex[] fft_Z = new Complex[N+N];
        for (int i = 0; i < fft_Z.length; i++)
            fft_Z[i] = fft_X[i].times(fft_Y[i]);
        Complex[] Z = IFFT(fft_Z);
        double[] ret_Z = new double[X.length+Y.length-1];
        for (int i = 0; i < ret_Z.length; i++)
            ret_Z[i] = Z[i].real();

        return ret_Z;
    }

    // calculate cross correlation
    // for more please consult wikipedia https://en.wikipedia.org/wiki/Cross-correlation
    public static double[] xcorr(double[] X, double[] Y) {
        int N = max(X.length, Y.length);
        double[] new_X = new double[N];
        double[] new_Y = new double[N];
        System.arraycopy(X, 0, new_X, 0, X.length);
        System.arraycopy(Y, 0, new_Y, 0, Y.length);

        // flip Y
        for (int i = 0; i + i < N; i++) {
            double tmp = new_Y[i];
            new_Y[i] = new_Y[N - 1 - i];
            new_Y[N - 1 - i] = tmp;
        }
        return conv(new_X, new_Y);
    }

}

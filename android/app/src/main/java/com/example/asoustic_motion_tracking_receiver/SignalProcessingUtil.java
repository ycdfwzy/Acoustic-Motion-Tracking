package com.example.asoustic_motion_tracking_receiver;

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
}

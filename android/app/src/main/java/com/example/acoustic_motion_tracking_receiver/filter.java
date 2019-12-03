package com.example.acoustic_motion_tracking_receiver;

public class filter {
    public int idx, taps;
    public double[] h, samples;

    public filter(int taps, double f1, double f2, String type, String window)
    {
        this.idx = 0;
        this.taps = taps;
        double[] w = new double[taps];
        this.samples = new double[taps];
        for(int i=0; i<taps; i++)
        {
            w[i]=1;
            samples[i] = 0;
        }
        if(type.equals("lp"))
        {
            this.h = lowpass_coeff(taps, f1);
        }
        else if(type.equals("hp"))
        {
            this.h = highpass_coeff(taps, f1);
        }
        else if(type.equals("bp"))
        {
            this.h = bandpass_coeff(taps, f1, f2);
        }
        else if(type.equals("sb"))
        {
            this.h = bandstop_coeff(taps, f1, f2);
        }

        if(window.equals("hamming"))
        {
            w = window_hamming(taps);
        }
        else if(window.equals("hanning"))
        {
            w = window_hanning(taps);
        }
        else if(window.equals("triangle"))
        {
            w = window_triangle(taps);
        }
        else if(window.equals("blackman"))
        {
            w = window_blackman(taps);
        }

        if(!window.equals(""))
        {
            for(int i=0; i<taps; i++)
            {
                this.h[i]*=w[i];
            }
        }

    }

    public double[] get_coeff()
    {
        return this.h;
    }

    public static double sinc(double x)
    {
        if (x==0)
            return 1;
        return Math.sin(Math.PI*x)/(Math.PI*x);
    }

    public static double[] lowpass_coeff(int taps, double f)
    {
        int[]  n = new int[taps];
        double[] h = new double[taps];
        for(int i=0; i<taps; i++)
        {
            n[i] = i-(int)(taps/2);
            h[i] = 2*f*sinc(2*f*n[i]);
        }
        return h;
    }

    public static double[] highpass_coeff(int taps, double f)
    {
        int[]  n = new int[taps];
        double[] h = new double[taps];
        for(int i=0; i<taps; i++)
        {
            n[i] = i-(int)(taps/2);
            h[i] = sinc(n[i])-2*f*sinc(2*f*n[i]);
        }
        return h;
    }

    public static double[] bandpass_coeff(int taps, double f1, double f2)
    {
        int[]  n = new int[taps];
        double[] h = new double[taps];
        for(int i=0; i<taps; i++)
        {
            n[i] = i-(int)(taps/2);
            h[i] = 2.0*f1*sinc(2.0*f1*n[i]) - 2.0*f2*sinc(2.0*f2*n[i]);
        }
        return h;
    }

    public static double[] bandstop_coeff(int taps, double f1, double f2)
    {
        int[]  n = new int[taps];
        double[] h = new double[taps];
        for(int i=0; i<taps; i++)
        {
            n[i] = i-(int)(taps/2);
            h[i] = 2.0*f1*sinc(2.0*f1*n[i]) - 2.0*f2*sinc(2.0*f2*n[i]) + sinc(n[i]);
        }
        return h;
    }

    public static double[] window_hamming(int taps)
    {
        double[] w = new double[taps];
        double alpha=0.54, beta = 0.46;
        for(int i=0; i<taps; i++)
        {
            w[i] = alpha - beta * Math.cos(2.0 * Math.PI * i / (taps - 1));
        }
        return w;
    }

    public static double[] window_hanning(int taps)
    {
        double[] w = new double[taps];
        for(int i=0; i<taps; i++)
        {
            w[i] =  Math.sin(((double) Math.PI * i) / (taps - 1)) *
                    Math.sin(((double) Math.PI * i) / (taps - 1));
        }
        return w;
    }

    public static double[] window_triangle(int taps)
    {
        double[] w = new double[taps];
        for(int i=0; i<taps; i++)
        {
            w[i] = 1 - Math.abs((i - (((double)(taps-1)) / 2.0)) / (((double)taps) / 2.0));
        }
        return w;
    }

    public static double[] window_blackman(int taps)
    {
        double[] w = new double[taps];
        double alpha0 = 0.42;
        double alpha1 = 0.5;
        double alpha2 = 0.08;
        for(int i=0; i<taps; i++)
        {
            w[i] = alpha0 - alpha1 * Math.cos(2.0 * Math.PI * i / (taps - 1))
                    - alpha2 * Math.cos(4.0 * Math.PI * i / (taps - 1));
        }
        return w;
    }

    double filter(double new_sample)
    {
        double res = 0;
        this.samples[this.idx] = new_sample;
        for(int i=0; i<this.taps; i++)
        {
            res += this.samples[(this.idx + i) % this.taps] * this.h[i];

        }
        this.idx = (this.idx + 1) % this.taps;
        return res;
    }
}

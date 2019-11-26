package com.example.acoustic_motion_tracking_receiver;

public class Complex implements Comparable<Complex>{
    public double real, imagine;

    public Complex()  { this(0); }
    public Complex(double r)  { this(r, 0); }

    public Complex(double r, double i)
    {
        this.real = r;
        this.imagine = i;
    }

    public Complex(Complex c)
    {
        this.real = c.real;
        this.imagine = c.imagine;
    }

    public double real() { return this.real; }
    public double imag() { return this.imagine; }

    public double norm() { return Math.sqrt(this.real*this.real + this.imagine*this.imagine); }

    public void setReal(double r) { this.real = r; }
    public void setImag(double i) { this.imagine = i; }

    public Complex conjugate()
    {
        return new Complex(this.real, -this.imagine);
    }
    public Complex plus(Complex c)
    {
        return new Complex(this.real+c.real, this.imagine+c.imagine);
    }
    public Complex minus(Complex c)
    {
        return new Complex(this.real-c.real, this.imagine-c.imagine);
    }

    public Complex times(double d)
    {
        return new Complex(this.real*d, this.imagine*d);
    }
    public Complex times(Complex c)
    {
        double r = this.real*c.real - this.imagine*c.imagine;
        double i = this.real*c.imagine + this.imagine*c.real;
        return new Complex(r, i);
    }
    public Complex divide(Complex c)
    {
        if(c.real==0.0&&c.imagine==0.0)
        {
            System.err.println("除数不能为0！");
            return new Complex(0, 0);
        }
        double r = (this.real*c.real + this.imagine*c.imagine) / (c.real*c.real + c.imagine*c.imagine);
        double i = (this.imagine*c.real - this.real*c.imagine) / (c.real*c.real + c.imagine*c.imagine);
        return new Complex(r, i);
    }

    public double abs()
    {
        return Math.sqrt(this.real*this.real+this.imagine*this.imagine);
    }

    public int compareTo(Complex p) {
        if (Math.abs(real-p.real) < 1e-6) {
            return imagine > p.imagine ? 1 : -1;
        }
        return real > p.real ? 1 : -1;
    }

}

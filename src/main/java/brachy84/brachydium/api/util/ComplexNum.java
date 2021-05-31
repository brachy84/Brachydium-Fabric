package brachy84.brachydium.api.util;

public class ComplexNum {

    /**
     * The real part of the complex number
     */
    private double r;

    /**
     * The imaginary part of the real number
     */
    private double i;

    public ComplexNum(int a, int i) {
        this.r = a;
        this.i = i;
    }

    public ComplexNum(double real, double imaginary) {
        this.r = real;
        this.i = imaginary;
    }

    public double real() {
        return r;
    }

    /** An accessor method. Returns the imaginary part of the complex number */
    public double imaginary() {
        return i;
    }

    /** Compute the magnitude of a complex number */
    public double modulus() {
        return Math.sqrt(r * r + i * i);
    }

    public double angle() {
        return Math.toDegrees(Math.atan(i / r));
    }

    public ComplexNum conjugate() {
        return new ComplexNum(r, -i);
    }

    public double euler() {
        double angle = angle();
        return Math.cos(Math.toRadians(angle)) + Math.sin(Math.toRadians(angle));
    }

    @Override
    public String toString() {
        return "{" + r + "," + i + "}";
    }

    public static ComplexNum add(ComplexNum a, ComplexNum b) {
        return new ComplexNum(a.r + b.r, a.i + b.i);
    }

    public ComplexNum add(ComplexNum a) {
        return new ComplexNum(this.r + a.r, this.i + a.i);
    }

    /** A static class method to multiply complex numbers */
    public static ComplexNum multiply(ComplexNum a, ComplexNum b) {
        return new ComplexNum(a.r * b.r - a.i * b.i, a.r * b.i + a.i * b.r);
    }

    /** An instance method to multiply complex numbers */
    public ComplexNum multiply(ComplexNum a) {
        return new ComplexNum(r * a.r - i * a.i, r * a.i + i * a.r);
    }
}

/**
 * Name: Muhammad Rasul
 * Email: rasulhashir@gmail.com
 *
 * Java handles references in a strange way
 * when references are assigned within a method, this
 * class is meant to manually set all the references 
 * after the Qr factorization method that swaps these
 * references around for the sake of efficiency
 */

import java.util.ArrayList;
public class MatrixTuple
{
    public ArrayList<ArrayList<Double>> m1;
    public ArrayList<ArrayList<Double>> m2;
    public ArrayList<ArrayList<Double>> m3;
    public ArrayList<ArrayList<Double>> m4;

    public MatrixTuple(ArrayList<ArrayList<Double>> m1, ArrayList<ArrayList<Double>> m2, ArrayList<ArrayList<Double>> m3, ArrayList<ArrayList<Double>> m4)
    {
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;
        this.m4 = m4;
    }


}
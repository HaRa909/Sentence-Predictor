/**
 * Name: Muhammad Rasul
 * Email: rasulhashir@gmail.com
 *
 * This file is meant to be a class
 * that stores two matrices or arraylist
 * of arraylists in a pair, meant to be
 * a return type for QR factorization method.
 */

import java.util.ArrayList;
public class matrixpair
{
    public ArrayList<ArrayList<Double>> m1;
    public ArrayList<ArrayList<Double>> m2;

    public matrixpair(ArrayList<ArrayList<Double>> m1, ArrayList<ArrayList<Double>> m2)
    {
        this.m1 = m1;
        this.m2 = m2;
    }


}

/**
 * Name: Muhammad Rasul
 * Email: rasulhashir@gmail.com
 * 
 * This file has some methods that can be used
 * for matrix operations, matrices being represented
 * by an arraylist of arraylists.
 */


import java.util.ArrayList;
public class Matrix {

    //what we define 0 to be due to guaranteed Floating point error
    static final double EPSILON = 1e-10; 

    //Exit conditions for QR algorithm
    static final double TOLERANCE = 1e-6; //the error we are okay with
    static int maxIterations = 1000; // max amount of algorithm iterations


    /**
     * Frobenius norm that ignores the diagonal entries in the matrix
     * @param M matrix we want this modified frobenius norm of
     */
    public static double frobeniusNormminusdiag(ArrayList<ArrayList<Double>> M) {
        double norm = 0;
        for (int i = 0; i < M.size(); i++) {
            for (int j = 0; j < M.get(0).size(); j++) {
                if (i != j) {
                    norm += M.get(i).get(j);
                }
            }
        }
        norm = Math.sqrt(norm);
        return norm;
    }

    /**
     * 
     * QR algorithm ran until either tolerance is good enough
     * or the amount of iterations passes the maximum. QR
     * algorithm is used to obtain eigenvalues and 
     * eigenvectors of a matrix in an iterative and numerically
     * stable manner.
     * 
     * 
     * @param A The matrix we are running QR algorithm on
     * @return (m1,m2) 
     * m1 is the matrix whose diagonals are eigenvalues
     * m2 is the matrix whose columns are eigenvectors
     * the diagonal entry (i,i) associated with eigenvector
     * in column i
     * 
     */
    public static matrixpair QRalgorithm(ArrayList<ArrayList<Double>> A) {

        //Matrices that will be written to and returned
        ArrayList<ArrayList<Double>> Ak = new ArrayList(A.size());
        ArrayList<ArrayList<Double>> Q_total = new ArrayList<>(A.size());

        //Temporary matrices made to make process more efficient
        ArrayList<ArrayList<Double>> tempQ = new ArrayList<>(A.size());
        ArrayList<ArrayList<Double>> tempR = new ArrayList<>(A.size());

        ArrayList<ArrayList<Double>> Q = new ArrayList(A.size());
        ArrayList<ArrayList<Double>> R = new ArrayList(A.size());

        /*
        Combining all operations of instantiating these matrices.
        Qtot starts as an identity matrix
        Ak starts as the matrix input A
        tempQ and tempR are just square matrices
        that will be constantly overwritten
        as to prevent repeated instantiation of
        objects
        */
        for (int i = 0; i < A.size(); i++) {
            ArrayList<Double> rowQtot = new ArrayList(A.size());
            ArrayList<Double> rowAk = new ArrayList(A.size());
            ArrayList<Double> rowtempQ = new ArrayList(A.size());
            ArrayList<Double> rowtempR = new ArrayList(A.size());
            ArrayList<Double> rowR = new ArrayList(A.size());
            ArrayList<Double> rowQ = new ArrayList(A.size());
            for (int j = 0; j < A.size(); j++) {
                rowAk.add(A.get(i).get(j));
                rowtempQ.add(0.0);
                rowtempR.add(0.0);
                rowQ.add(0.0);
                rowR.add(0.0);

                if (i == j) {
                    rowQtot.add(1.0);
                } else {
                    rowQtot.add(0.0);
                }
            }
            Q_total.add(rowQtot);
            tempQ.add(rowtempQ);
            tempR.add(rowtempR);
            Ak.add(rowAk);
            Q.add(rowQ);
            R.add(rowR);
        }

        boolean tolerance_Reached = false;
        System.out.println("QR Algorithm running");

        for (int i = 0; i < maxIterations; i++) {

            //Tells the user the calculation's progress
            if (i % (maxIterations / 100) == 0 && i % (maxIterations / 100) == 0) {
                System.out.printf("%.2f%%\n", (100.0 * i / maxIterations));
            }

            //we QR factor Ak and use tempQ as the scratch matrices,
            MatrixTuple QRT1T2 = QRfactor(Ak, tempQ, tempR, Q, R);
            Q = QRT1T2.m1;
            R = QRT1T2.m2;
            tempQ = QRT1T2.m3;
            tempR = QRT1T2.m4;
            
            //multiply R and Q, put this producton tempR
            //overwrite Ak with this new R matrix

            ArrayList<ArrayList<Double>> swappointer = Ak;
            Ak = multiplication(R, Q, tempR);

       
            //old R matrix no longer needed
            tempR = swappointer;
          
            //overwrite Q_total with product of prev Q_total
            //and the obtained Q matrix, but we need to temporarily
            //hold the address of Q_total
            swappointer = Q_total;
            Q_total = multiplication(Q_total, Q, tempQ);
            

            //no longer need the old Q product matrix, set
            //it to a scratch matrix
            tempQ = swappointer;

           
            
            //if tolerance is good enough, exit algorithm
            if (frobeniusNormminusdiag(Ak) < TOLERANCE) {
                tolerance_Reached = true;
                break;
            }
        }


        System.out.println("100%");
        if(tolerance_Reached)
        {
            System.out.println("Desired Tolerance reached");
        }
        
        
        return new matrixpair(Ak, Q_total);
    }

    /**
     * Will produce a deep copy of a matrix
     * @param toclone matrix we want to copy
     * @return a deep copy of toclone
     */
    public static ArrayList<ArrayList<Double>> duplicatematrix(ArrayList<ArrayList<Double>> toclone) {
        ArrayList<ArrayList<Double>> cloned = new ArrayList(toclone.size());

        for (ArrayList<Double> row : toclone) {
            ArrayList<Double> clonedrow = new ArrayList(row.size());
            for (Double element : row) {
                clonedrow.add(element);
            }
            cloned.add(clonedrow);
        }
        return cloned;
    }


    /**
     * QR decomposition of a matrix A via Householder reflectors.
     * This is done for numerical stability.
     * @param A matrix we want QR decomposition of
     * @param temp1 matrix we want to use as a scratch matrix
     * @return Q and R matrices in matrixpair (Q,R)
     */
    @SuppressWarnings("")
    public static MatrixTuple QRfactor
    (ArrayList<ArrayList<Double>> A, ArrayList<ArrayList<Double>> temp1, 
    ArrayList<ArrayList<Double>> temp2, ArrayList<ArrayList<Double>> Q,
    ArrayList<ArrayList<Double>> R) {
        
        /*
        Since we are using Householder reflectors, we require
        a copy of matrix A and the identity matrix, set R to A
        and set Q to identity matrix
        */
        for (int i = 0; i < A.size(); i++) {
            for (int j = 0; j < A.size(); j++) {
                R.get(i).set(j,A.get(i).get(j));
                if (i == j) {
                    Q.get(i).set(j,1.0);
                
                } else {
                    Q.get(i).set(j,0.0);
                }
            }
        }


        /*
        instantiate vector x with all 0s
        optimization to reduce creation and
        destruction of this vector.
        */
        ArrayList<Double> x = new ArrayList<>(R.size());
        for (int i = 0; i < R.size(); i++) {
            x.add(0.0);
        }

        /*
        we must run A.size() - 1 iterations of multiplications
        against A to obtain our upper trianglular matrix R
        using our householder reflector matrices.
        */
        for (int k = 0; k < A.size() - 1; k++) {

            /*
            choose the kth column, starting from the kth column
            and from the kth row downwards we copy the column of 
            matrix R to x, everything else is set to 0s because
            of the submatrix we look at, whose dimensions are
            determined by R.size() - k
            */
            x = getColumn(R, k, k, R.size(), x);
            
            //obtain the euclidean norm of vector x
            double g = Math.sqrt(dotproduct(x, x));

            //subtract magnitude from first element of
            //vector
            x.set(k, x.get(k) - g);

            //get the new magnitude of this vector
            g = Math.sqrt(dotproduct(x, x));

            //if the magnitude of g isn't zero, then we carry on
            if (Math.abs(g) > EPSILON) {

                //modify x by dividing it each of its elements by magnitude,
                //giving us a unit vector
                for (int i = 0; i < x.size(); i++) {
                    x.set(i, x.get(i) / g);
                }
                
                //gives us temporary step in calculation to help simplify
                //the equation R = R(I - 2*x*xtranspose)
                ArrayList<Double> tempstep = transposedmultiplicationvectoronright(R, x);


                //preparing to swap the old R matrix
                ArrayList<ArrayList<Double>> swappointer = R;

                R = matrixmatrixsubtractionwithouterproduct(R, x, tempstep, temp1);

                //old R matrix no longer needed, make it a scratch matrix now
                temp1 = swappointer;

                //we will prepare to swap the old and new matrices, making the old
                //one the new temp matrix
                swappointer = Q;

                //multiplies the reflector matrix Qk against the other Q matrices in
                //an efficient manner
                Q = matrixmatrixsubtractionmultiplicationouterproduct(Q, x, temp1, temp2);

                //let the new temp2 be the matrix we no longer need, the old Q matrix
                temp2 = swappointer;        
            }
        }
        return new MatrixTuple(Q, R, temp1, temp2);
    }



    /**
     * Combination of many steps of matrix math
     * @param A matrix A
     * @param x householder reflector matrix
     * @param product stores A * (2 * x * xtranspose)
     * @param difference stores A - A * (2 * x * xtranspose)
     * @return difference
     */
    public static ArrayList<ArrayList<Double>> matrixmatrixsubtractionmultiplicationouterproduct
    (ArrayList<ArrayList<Double>> A, ArrayList<Double> x, ArrayList<ArrayList<Double>> product,
    ArrayList<ArrayList<Double>> difference) {
        for(int i = 0; i < x.size(); i++)
        {
            for(int j = 0; j < x.size(); j++)
            {
                double entry = 0; 
                for(int k = 0; k < x.size(); k++)
                {
                    double twoouterproductki = x.get(k) * x.get(i) * 2;
                    entry += A.get(j).get(k) * twoouterproductki;
                }
                product.get(j).set(i,entry);
                double subtract = A.get(j).get(i) - product.get(j).get(i);


                difference.get(j).set(i,subtract);
            }
        }
        return difference;
    }

    /**
     * Combination of steps of matrix math
     * @param A matrix A
     * @param x vector 1 we will do outer product with
     * @param u vector 2 we do outer product with
     * @return A - 2 * x * u^T
     */
    public static ArrayList<ArrayList<Double>> matrixmatrixsubtractionwithouterproduct
    (ArrayList<ArrayList<Double>> A, ArrayList<Double> x, ArrayList<Double> u
    ,ArrayList<ArrayList<Double>> temp) {


        for (int i = 0; i < A.size(); i++) {
            for (int j = 0; j < A.get(0).size(); j++) {
                double tosubtract = x.get(i) * 2 * u.get(j);
                temp.get(i).set(j, A.get(i).get(j) - tosubtract);
            }
        }
        return temp;
    }


    

    /**
     * transpose the matrix, then multiply
     * the vector, we don't actually transpose the vector
     * we just change how we multiply it
     * @param A matrix A
     * @param v vector v
     * @return Atranspose * v
     */
    public static ArrayList<Double> transposedmultiplicationvectoronright
    (ArrayList<ArrayList<Double>> A, ArrayList<Double> v) {
        if (A.size() != v.size()) {
            System.out.println("vector and matrix not of same size");
            return null;
        }
        ArrayList<Double> productvector = new ArrayList(A.size());

        for (int j = 0; j < A.get(0).size(); j++) {
            double sum = 0;
            for (int k = 0; k < A.size(); k++) {
                sum += v.get(k) * A.get(k).get(j);
            }
            productvector.add(sum);
        }

        return productvector;
    }
    

    /**
     * Gets a column of variable size somewhere from the matrix.
     * The vector will have everything unspecified set to 0
     * @param matrix matrix we are getting a vector of
     * @param column the column we want to take the vector from
     * @param top the first row we want to start reading from
     * @param bottom the last row we want to read from
     * @param towriteto the vector we are writing to (to save space)
     * @return the modified vector
     */
    public static ArrayList<Double> getColumn
    (ArrayList<ArrayList<Double>> matrix, int column, int top, int bottom, ArrayList<Double> towriteto) {
 
        for (int i = 0; i < top; i++) {
            towriteto.set(i, 0.0);
        }
        for (int i = top; i < bottom; i++) {
            towriteto.set(i, matrix.get(i).get(column));
        }
        return towriteto;
    }

    /**
     * This is for deflating the matrix by an eigenvakue
     * @param m matrix we want to deflate
     * @param eigenvalue eigenvalue we want to deflate by
     * @return deflated matrix
     */
    public static ArrayList<ArrayList<Double>> geteigenmatrix
    (ArrayList<ArrayList<Double>> m, double eigenvalue) {
        ArrayList<ArrayList<Double>> changedmatrix = new ArrayList(m.size());
        for (int i = 0; i < m.size(); i++) {
            ArrayList<Double> row = new ArrayList(m.size());
            for (int j = 0; j < m.size(); j++) {
                if (i != j) {
                    row.add(m.get(i).get(j));
                } else {
                    row.add((m.get(i).get(j) - eigenvalue));
                }
            }
            changedmatrix.add(row);
        }

        return changedmatrix;
    }

    
    /**
     * will swap 2 elements of a vector
     * @param b vector to modify
     * @param ind1 index to swap
     * @param ind2 index to swap to
     */
    public static void swapvector(ArrayList<Double> b, int ind1, int ind2) {

        if (ind1 != ind2) {
            double temp1 = b.get(ind1);
            b.set(ind1, b.get(ind2));
            b.set(ind2, temp1);
        }
    }

    /**
     * will swap two rows on a matrix
     * @param m matrix
     * @param ind1 row to swap
     * @param ind2 row to swap with
     */
    public static void swap(ArrayList<ArrayList<Double>> m, int ind1, int ind2) {

        if (ind1 != ind2) {
            ArrayList<Double> temp1 = new ArrayList<>(m.get(ind1));
            ArrayList<Double> temp2 = new ArrayList<>(m.get(ind2));
            m.set(ind2, temp1);
            m.set(ind1, temp2);
        }
    }

    /**
     * Multiplies a matrix by a vector transposed to the left
     * @param x vector to transpose and multply against
     * @param B matrix to multiply against vector
     * @return vector obtained as a product of multiplication
     */
    public static ArrayList<Double> multiplicationvectoronleft
    (ArrayList<Double> x, ArrayList<ArrayList<Double>> B) {

        int n = x.size(); 
        if (B.size() != n) {
            System.out.println
            ("multiplicationvectoronleft CAN'T MULTIPLY, SIZES OF A AND B DON'T MATCH");
            return null;
        }
        int p = B.get(0).size();

        ArrayList<Double> C = new ArrayList<>(p);

        for (int i = 0; i < p; i++) {
            C.add(0.0);
        }

        for (int i = 0; i < p; i++) {

            for (int k = 0; k < n; k++) {
              
                C.add(C.get(i) + x.get(k) * B.get(k).get(i));
            }
        }

        return C;
    }

    /**
     * multiplies the matrix by the vector to the right
     * @param m1 the matrix
     * @param m2 the vector
     * @return the product vector of the 
     * matrix vector multiplication m1 * m2
     */
    public static ArrayList<Double> multiplicationvectoronright
    (ArrayList<ArrayList<Double>> m1, ArrayList<Double> m2) {
  
        if (m1.get(0).size() != m2.size()) {
            System.out.println("Matrices not of same size");
            return null;
        }
        ArrayList<Double> productvector = new ArrayList(m1.size());

        for (int j = 0; j < m1.size(); j++) {
            double sum = 0;

            for (int k = 0; k < m1.get(0).size(); k++) {
                sum += m1.get(j).get(k) * m2.get(k);
            }
            productvector.add(sum);

        }

        return productvector;
    }

    /**
     * multiplies all the element of vectors against each other,
     * then adds all of them together, returning this summation
     * @param m1 vector 1
     * @param m2 vector 2
     * @return summation of the ith elements of each vector
     * multiplied against each other
     */
    public static double dotproduct(ArrayList<Double> m1, ArrayList<Double> m2) {

        if (m1.size() != m2.size()) {
            System.out.println("Matrices not of same size");
            return Double.MIN_VALUE;
        }
        double product = 0.0;

        for (int j = 0; j < m1.size(); j++) {
            product += m1.get(j) * m2.get(j);
        }

        return product;
    }

    /**
     * Transposes a matrix then multiplies another matrix against it
     * @param A matrix to transpose
     * @param B matrix to multiply against A from the right
     * @param C matrix to store the product
     */
    public static ArrayList<ArrayList<Double>> transposedmult
    (ArrayList<ArrayList<Double>> A,ArrayList<ArrayList<Double>> B,ArrayList<ArrayList<Double>> C)
    {
        for(int i = 0; i < A.size(); i++)
        {
            for(int j = 0; j < A.size(); j++)
            {
                double sum = 0;
                for(int k = 0; k < A.size(); k++)
                {
                    sum += A.get(j).get(k) * B.get(i).get(k);
                }
                C.get(j).set(i,sum);
            }
        }
        return C;
    }

    /**
     * Multiplies 2 matrices normally against each other
     * @param A first matrix
     * @param B second matrix
     * @param C = A * B
     */
    public static ArrayList<ArrayList<Double>> multiplication
    (ArrayList<ArrayList<Double>> A, ArrayList<ArrayList<Double>> B, ArrayList<ArrayList<Double>> C) {
        int m = A.size(); 
        int n = A.get(0).size(); 
        if (B.size() != n) {
            System.out.println("multiplication method CAN'T MULTIPLY, SIZES OF A AND B DON'T MATCH");
            return null;
        }
        int p = B.get(0).size();

      
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < m; j++) {
                double product = 0;
                for (int k = 0; k < n; k++) {
                    
                    product += A.get(j).get(k) * B.get(k).get(i);
                }
                C.get(j).set(i, product);

            }

        }

        return C;
    }


    /**
     * subtracts a matrix by another, then returns the difference
     * @param matrix1 first matrix
     * @param matrix2 matrix to subtract by
     * @return new matrix that's matrix1 - matrix2
     */
    public static ArrayList<ArrayList<Double>> matrixmatrixsubtraction
    (ArrayList<ArrayList<Double>> matrix1, ArrayList<ArrayList<Double>> matrix2) {
        if (matrix1.size() != matrix2.size() || matrix1.get(0).size() != matrix2.get(0).size()) {
            System.out.println("SUBTRACTION WON'T WORK SINCE MISMATCHING SIZES");
            return null;
        }
        ArrayList<ArrayList<Double>> difference = new ArrayList(matrix1.size());
        for (ArrayList<Double> row : matrix1) {
            ArrayList<Double> newrow = new ArrayList<>(matrix1.get(0).size());
            for (double element : row) {
                newrow.add(element);
            }
            difference.add(newrow);
        }
        for (int i = 0; i < matrix1.size(); i++) {
            for (int j = 0; j < matrix1.size(); j++) {
                
                difference.get(i).set(j, matrix1.get(i).get(j) - matrix2.get(i).get(j));
            }
        }
        return difference;
    }

    /**
     * turns a vector into a unit vector by
     * dividing each element by its magnitude
     * @param v the vector we want to turn into
     * a unit vector
     */
    public static void unitvector(ArrayList<Double> v) {
       
        double scalingfactor = dotproduct(v, v);
        scalingfactor = Math.sqrt(scalingfactor);
        for (int i = 0; i < v.size(); i++) {
            v.set(i, v.get(i) / scalingfactor);
        }

    }

    /**
     * obtains the outer product between two vector
     * @param v1 vector on the left
     * @param v2 vector to be transposed on the right
     * @return new outer product matrix
     */
    public static ArrayList<ArrayList<Double>> outerproduct
    (ArrayList<Double> v1, ArrayList<Double> v2) {

        ArrayList<ArrayList<Double>> product = new ArrayList<>(v1.size());

        for (int i = 0; i < v1.size(); i++) {
            ArrayList<Double> row = new ArrayList<>(v2.size());
            for (int j = 0; j < v2.size(); j++) {
    
                row.add(v1.get(i) * v2.get(j));
            }
            product.add(row);
        }

        return product;
    }

    /**
     * Takes a matrix and returns a new one whose columns and rows are switched.
     * This is used for the stationary distribution calculation, since the matrix must
     * be transposed for this calculation to work.
     * @param matrix matrix we want a 
     * @return a new matrix based on the parameter matrix whose columns and rows
     * are switched
     */
    public static ArrayList<ArrayList<Double>> transpose(ArrayList<ArrayList<Double>> matrix) {

        //should be comlumn sized for rows
        ArrayList<ArrayList<Double>> transposed = new ArrayList(matrix.get(0).size());
        for(int i = 0; i < matrix.get(0).size(); i++)
        {
            ArrayList<Double> newrow = new ArrayList<>(matrix.size());
            for(int j = 0; j < matrix.size(); j++)
            {
                newrow.add(matrix.get(j).get(i));
            }
            transposed.add(newrow);
        }

        
        return transposed;
    }


    /**
     * prints a matrix as a string
     * @param in matrix to write as a string
     */
    public static <T> void tostring(ArrayList<ArrayList<T>> in) {
        System.out.println();
        for (int i = 0; i < in.size(); i++) {
            for (int j = 0; j < in.get(0).size(); j++) {
                System.out.print(in.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }

    /**
     * prints a vector as a string
     * @param in vector to write as a string
     */
    public static <T> void vectostring(ArrayList<T> in) {
        System.out.println();
        for (int i = 0; i < in.size(); i++) {
            System.out.print(in.get(i) + " ");

        }
        System.out.println();
    }


    


}

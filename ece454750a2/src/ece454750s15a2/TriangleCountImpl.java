/**
 * ECE 454/750: Distributed Computing
 * <p/>
 * Code written by Wojciech Golab, University of Waterloo, 2015
 * Addendum Author(s):
 * 1) Simarpreet Singh s244sing@uwaterloo.ca
 * Wrote:
 *  a) Adjacency Matrix Graph representation
 *  b) EnumerateTriangles AMG implementation
 *  c) Trace calculator with Strassen
 * <p/>
 */

package ece454750s15a2;

import java.io.*;
import java.io.IOError;
import java.io.IOException;
import java.lang.Integer;
import java.lang.Iterable;
import java.lang.RuntimeException;
import java.lang.System;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TriangleCountImpl {
    private byte[] input;
    private int numCores;

    public TriangleCountImpl(byte[] input, int numCores) {
        this.input = input;
        this.numCores = numCores;
    }

    public class AdjMatrixGraph {
        private int V;
        private int E;
        private int[][] adjMatrix;

        public AdjMatrixGraph(int V) {
            if (V < 0) throw new RuntimeException("Number of vertices cannot be negative.");
            this.V = V;
            this.E = 0;
            this.adjMatrix = new int[V][V];
        }

        private class AdjIterator implements Iterator<Integer>, Iterable<Integer> {
            int v, w = 0;
            AdjIterator(int v) { this.v = v; }

            public Iterator<Integer> iterator() { return this; }

            public boolean hasNext() {
                while (w < V) {
                    if (adjMatrix[v][w] == 1)
                        return true;
                    w++;
                }
                return false;
            }

            public Integer next() {
                if (hasNext())
                    return w++;
                else
                    throw new NoSuchElementException();
            }

            public void remove()  { throw new UnsupportedOperationException();  }
        }

        public int getVCount() {
            return this.V;
        }
        public int getECount() {
            return this.E;
        }

        public void addEdge(int x, int y) {
            if(this.adjMatrix[x][y] == 0) // Only increment if new edge.
                this.E++;
            this.adjMatrix[x][y] = 1;
            this.adjMatrix[y][x] = 1;
        }

        public int contains(int x, int y) {
            return this.adjMatrix[x][y];
        }

        public Iterable<Integer> adjMatrix(int v) {
            return new AdjIterator(v);
        }

        public String toString() {
            String NEWLINE = System.getProperty("line.separator");
            StringBuilder s = new StringBuilder();
            s.append("V = " + V + " " + "E = " + E + NEWLINE);
            for (int v = 0; v < V; v++) {
                s.append(v + ": ");
                for (int w : adjMatrix(v)) {
                    s.append(w + " ");
                }
                s.append(NEWLINE);
            }
            return s.toString();
        }

    }

    public class AdjListGraph {
        private int V;
        private int E;
        private ArrayList<HashSet<Integer>> adjList;

        public AdjListGraph(int V, int E) {
            if (V < 0) throw new RuntimeException("Number of vertices cannot be negative.");
            this.V = V;
            this.E = 0;
            this.adjList = new ArrayList<HashSet<Integer>>();
            for(int i = 0; i < V; ++i) {
                this.adjList.add(new HashSet<Integer>());
            }
        }

        public void addEdge(int i, int j) {
            if(this.adjList.get(i).add(j) && this.adjList.get(j).add(i))
                this.E += 1;
        }

        public void removeEdge(int i, int j) {
            if(this.adjList.get(i).remove(j) && this.adjList.get(j).remove(i))
                this.E -= 1;
        }

        public boolean hasEdge(int i, int j) {
            return (this.adjList.get(i).contains(j) || this.adjList.get(j).contains(i));
        }

        public int getNumVertices() {
            return this.V;
        }

        public int getTotalNumEdges() {
            return this.E;
        }

        public int getRelativeEdges(int i) {
            return this.adjList.get(i).size();
        }

        public HashSet<Integer> get(int i) {
            return this.adjList.get(i);
        }

    }

    public List<String> getGroupMembers() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("s244sing");
        ret.add("cpinn");
        return ret;
    }

    public int getTrace(ArrayList<ArrayList<Integer>> matrix) {
        int traceVal = 0;
        for (int i = 0; i < matrix.size(); i++) {
            System.out.println("[getTrace] matrix["+i+"]["+i+"] = " + matrix.get(i).get(i));
            traceVal += matrix.get(i).get(i);
        }
        return traceVal;
    }

    public class StrassenOld {
        /**
         * Function to multiply matrices
         **/
        public int[][] multiply(int[][] A, int[][] B) {
            int n = A.length;
            int[][] R = new int[n][n];
            /** base case **/
            if (n == 1)
                R[0][0] = A[0][0] * B[0][0];
            else {
                int[][] A11 = new int[n / 2][n / 2];
                int[][] A12 = new int[n / 2][n / 2];
                int[][] A21 = new int[n / 2][n / 2];
                int[][] A22 = new int[n / 2][n / 2];
                int[][] B11 = new int[n / 2][n / 2];
                int[][] B12 = new int[n / 2][n / 2];
                int[][] B21 = new int[n / 2][n / 2];
                int[][] B22 = new int[n / 2][n / 2];

                /** Dividing matrix A into 4 halves **/
                split(A, A11, 0, 0);
                split(A, A12, 0, n / 2);
                split(A, A21, n / 2, 0);
                split(A, A22, n / 2, n / 2);
                /** Dividing matrix B into 4 halves **/
                split(B, B11, 0, 0);
                split(B, B12, 0, n / 2);
                split(B, B21, n / 2, 0);
                split(B, B22, n / 2, n / 2);

                /**
                 M1 = (A11 + A22)(B11 + B22)
                 M2 = (A21 + A22) B11
                 M3 = A11 (B12 - B22)
                 M4 = A22 (B21 - B11)
                 M5 = (A11 + A12) B22
                 M6 = (A21 - A11) (B11 + B12)
                 M7 = (A12 - A22) (B21 + B22)
                 **/

                int[][] M1 = multiply(add(A11, A22), add(B11, B22));
                int[][] M2 = multiply(add(A21, A22), B11);
                int[][] M3 = multiply(A11, sub(B12, B22));
                int[][] M4 = multiply(A22, sub(B21, B11));
                int[][] M5 = multiply(add(A11, A12), B22);
                int[][] M6 = multiply(sub(A21, A11), add(B11, B12));
                int[][] M7 = multiply(sub(A12, A22), add(B21, B22));

                /**
                 C11 = M1 + M4 - M5 + M7
                 C12 = M3 + M5
                 C21 = M2 + M4
                 C22 = M1 - M2 + M3 + M6
                 **/
                int[][] C11 = add(sub(add(M1, M4), M5), M7);
                int[][] C12 = add(M3, M5);
                int[][] C21 = add(M2, M4);
                int[][] C22 = add(sub(add(M1, M3), M2), M6);

                /** join 4 halves into one result matrix **/
                join(C11, R, 0, 0);
                join(C12, R, 0, n / 2);
                join(C21, R, n / 2, 0);
                join(C22, R, n / 2, n / 2);
            }
            /** return result **/
            return R;
        }

        // Strassen Helper Functions

        /**
         * Funtion to sub two matrices
         **/
        public int[][] sub(int[][] A, int[][] B) {
            int n = A.length;
            int[][] C = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    C[i][j] = A[i][j] - B[i][j];
            return C;
        }

        /**
         * Funtion to add two matrices
         **/
        public int[][] add(int[][] A, int[][] B) {
            int n = A.length;
            int[][] C = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    C[i][j] = A[i][j] + B[i][j];
            return C;
        }

        /**
         * Funtion to split parent matrix into child matrices
         **/
        public void split(int[][] P, int[][] C, int iB, int jB) {
            for (int i1 = 0, i2 = iB; i1 < C.length; i1++, i2++)
                for (int j1 = 0, j2 = jB; j1 < C.length; j1++, j2++)
                    C[i1][j1] = P[i2][j2];
        }

        /**
         * Funtion to join child matrices intp parent matrix
         **/
        public void join(int[][] C, int[][] P, int iB, int jB) {
            for (int i1 = 0, i2 = iB; i1 < C.length; i1++, i2++)
                for (int j1 = 0, j2 = jB; j1 < C.length; j1++, j2++)
                    P[i2][j2] = C[i1][j1];
        }

    }

    public static class Strassen {
        static int LEAF_SIZE = 256;

        public static int[][] ijkAlgorithmVector(Vector<Vector<Integer>> A,
                                                 Vector<Vector<Integer>> B) {
            int n = A.size();

            // initialise C
            int[][] C = new int[n][n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < n; k++) {
                        C[i][j] += A.get(i).get(k) * B.get(k).get(j);
                    }
                }
            }
            return C;
        }

        public static int[][] ijkAlgorithm(ArrayList<ArrayList<Integer>> A,
                                           ArrayList<ArrayList<Integer>> B) {
            int n = A.size();

            // initialise C
            int[][] C = new int[n][n];

            for (int i = 0; i < n; i++) {
                for (int k = 0; k < n; k++) {
                    for (int j = 0; j < n; j++) {
                        C[i][j] += A.get(i).get(k) * B.get(k).get(j);
                    }
                }
            }
            return C;
        }

        public static int[][] ikjAlgorithm(ArrayList<ArrayList<Integer>> A,
                                           ArrayList<ArrayList<Integer>> B) {
            int n = A.size();

            // initialise C
            int[][] C = new int[n][n];

            for (int i = 0; i < n; i++) {
                for (int k = 0; k < n; k++) {
                    for (int j = 0; j < n; j++) {
                        C[i][j] += A.get(i).get(k) * B.get(k).get(j);
                    }
                }
            }
            return C;
        }

        public static int[][] ikjAlgorithm(int[][] A, int[][] B) {
            int n = A.length;

            // initialise C
            int[][] C = new int[n][n];

            for (int i = 0; i < n; i++) {
                for (int k = 0; k < n; k++) {
                    for (int j = 0; j < n; j++) {
                        C[i][j] += A[i][k] * B[k][j];
                    }
                }
            }
            return C;
        }

        private static int[][] add(int[][] A, int[][] B) {
            int n = A.length;
            int[][] C = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    C[i][j] = A[i][j] + B[i][j];
                }
            }
            return C;
        }

        private static int[][] subtract(int[][] A, int[][] B) {
            int n = A.length;
            int[][] C = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    C[i][j] = A[i][j] - B[i][j];
                }
            }
            return C;
        }

        private static int nextPowerOfTwo(int n) {
            int log2 = (int) Math.ceil(Math.log(n) / Math.log(2));
            return (int) Math.pow(2, log2);
        }

        public static int[][] strassen(int[][] A, int[][] B) {
            // Make the matrices bigger so that you can apply the strassen
            // algorithm recursively without having to deal with odd
            // matrix sizes
            int n = A.length;
            int m = nextPowerOfTwo(n);
            int[][] APrep = new int[m][m];
            int[][] BPrep = new int[m][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    APrep[i][j] = A[i][j];
                    BPrep[i][j] = B[i][j];
                }
            }

            int[][] CPrep = strassenR(APrep, BPrep);
            int[][] C = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    C[i][j] = CPrep[i][j];
                }
            }
            return C;
        }

        public static int[][] strassen(ArrayList<ArrayList<Integer>> A,
                                       ArrayList<ArrayList<Integer>> B) {
            // Make the matrices bigger so that you can apply the strassen
            // algorithm recursively without having to deal with odd
            // matrix sizes
            int n = A.size();
            int m = nextPowerOfTwo(n);
            int[][] APrep = new int[m][m];
            int[][] BPrep = new int[m][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    APrep[i][j] = A.get(i).get(j);
                    BPrep[i][j] = B.get(i).get(j);
                }
            }

            int[][] CPrep = strassenR(APrep, BPrep);
            int[][] C = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    C[i][j] = CPrep[i][j];
                }
            }
            return C;
        }

        private static int[][] strassenR(int[][] A, int[][] B) {
            int n = A.length;

            if (n <= LEAF_SIZE) {
                return ikjAlgorithm(A, B);
            } else {
                // initializing the new sub-matrices
                int newSize = n / 2;
                int[][] a11 = new int[newSize][newSize];
                int[][] a12 = new int[newSize][newSize];
                int[][] a21 = new int[newSize][newSize];
                int[][] a22 = new int[newSize][newSize];

                int[][] b11 = new int[newSize][newSize];
                int[][] b12 = new int[newSize][newSize];
                int[][] b21 = new int[newSize][newSize];
                int[][] b22 = new int[newSize][newSize];

                int[][] aResult = new int[newSize][newSize];
                int[][] bResult = new int[newSize][newSize];

                // dividing the matrices in 4 sub-matrices:
                for (int i = 0; i < newSize; i++) {
                    for (int j = 0; j < newSize; j++) {
                        a11[i][j] = A[i][j]; // top left
                        a12[i][j] = A[i][j + newSize]; // top right
                        a21[i][j] = A[i + newSize][j]; // bottom left
                        a22[i][j] = A[i + newSize][j + newSize]; // bottom right

                        b11[i][j] = B[i][j]; // top left
                        b12[i][j] = B[i][j + newSize]; // top right
                        b21[i][j] = B[i + newSize][j]; // bottom left
                        b22[i][j] = B[i + newSize][j + newSize]; // bottom right
                    }
                }

                // Calculating p1 to p7:
                aResult = add(a11, a22);
                bResult = add(b11, b22);
                int[][] p1 = strassenR(aResult, bResult);
                // p1 = (a11+a22) * (b11+b22)

                aResult = add(a21, a22); // a21 + a22
                int[][] p2 = strassenR(aResult, b11); // p2 = (a21+a22) * (b11)

                bResult = subtract(b12, b22); // b12 - b22
                int[][] p3 = strassenR(a11, bResult);
                // p3 = (a11) * (b12 - b22)

                bResult = subtract(b21, b11); // b21 - b11
                int[][] p4 = strassenR(a22, bResult);
                // p4 = (a22) * (b21 - b11)

                aResult = add(a11, a12); // a11 + a12
                int[][] p5 = strassenR(aResult, b22);
                // p5 = (a11+a12) * (b22)

                aResult = subtract(a21, a11); // a21 - a11
                bResult = add(b11, b12); // b11 + b12
                int[][] p6 = strassenR(aResult, bResult);
                // p6 = (a21-a11) * (b11+b12)

                aResult = subtract(a12, a22); // a12 - a22
                bResult = add(b21, b22); // b21 + b22
                int[][] p7 = strassenR(aResult, bResult);
                // p7 = (a12-a22) * (b21+b22)

                // calculating c21, c21, c11 e c22:
                int[][] c12 = add(p3, p5); // c12 = p3 + p5
                int[][] c21 = add(p2, p4); // c21 = p2 + p4

                aResult = add(p1, p4); // p1 + p4
                bResult = add(aResult, p7); // p1 + p4 + p7
                int[][] c11 = subtract(bResult, p5);
                // c11 = p1 + p4 - p5 + p7

                aResult = add(p1, p3); // p1 + p3
                bResult = add(aResult, p6); // p1 + p3 + p6
                int[][] c22 = subtract(bResult, p2);
                // c22 = p1 + p3 - p2 + p6

                // Grouping the results obtained in a single matrix:
                int[][] C = new int[n][n];
                for (int i = 0; i < newSize; i++) {
                    for (int j = 0; j < newSize; j++) {
                        C[i][j] = c11[i][j];
                        C[i][j + newSize] = c12[i][j];
                        C[i + newSize][j] = c21[i][j];
                        C[i + newSize][j + newSize] = c22[i][j];
                    }
                }
                return C;
            }
        }
    }

    public boolean checkForTriangle(int[][] A, int[][] A_sq) {
        int n = A.length;
        for(int i = 0; i < n; ++i) {
            for(int j = 0; j < n; ++j) {
                if(A[i][j] == 1 && A_sq[i][j] >= 1) {
                    return true;
                }
            }
        }

        // no triangles were found
        return false;
    }


    public List<Triangle> enumerateTrianglesMM() throws IOException {
        // TODO: Utilize ncores and parallelize

        AdjMatrixGraph adjacencyMatrix = getAdjacencyMatrix(input);
        ArrayList<Triangle> ret = new ArrayList<Triangle>();

        // Logic 1: Computing trace and calculating triangles.
        /* Distinct Triangles: trace(A^3) / 6
            Strassen strassenator = new Strassen();
            ArrayList<ArrayList<Integer>> stage1 = adjacencyMatrix.adjMatrix;
            ArrayList<ArrayList<Integer>> stage2 = strassenator.strassen(stage1, stage1);
            ArrayList<ArrayList<Integer>> stage3 = strassenator.strassen(stage2, stage1);

            int adjMatrixTrace = this.getTrace(stage3);
            int totalTriangles = adjMatrixTrace / 6;
            System.out.println("Total number of Triangles = " + totalTriangles);
            //System.out.println(adjacencyMatrix);
        */

        Strassen strassenator = new Strassen();
        int[][] stage1 = adjacencyMatrix.adjMatrix;
        int[][] stage2 = strassenator.strassen(stage1, stage1);
        //System.out.println(adjacencyMatrix);
        //System.out.println("Stage1\n" + Arrays.deepToString(stage1));
        //System.out.println("Stage2\n" + Arrays.deepToString(stage2));

        boolean triangleFound = checkForTriangle(stage1, stage2);
        if (triangleFound) {
            System.out.println("A triangle was found!");
        }

        return ret;
    }

    public List<Triangle> enumerateTriangles() throws IOException {
        AdjListGraph adjacencyList = getAdjacencyList(input);
        ArrayList<Triangle> ret = new ArrayList<Triangle>();
        int triangleCounter = 0;

        System.out.println("numVertices = " + adjacencyList.getNumVertices());
        System.out.println("numEdges    = " + adjacencyList.getTotalNumEdges());

        for (int i = 0; i < adjacencyList.getNumVertices(); i += 1) {
            for (int j = 0; j < adjacencyList.getNumVertices(); j += 1) {
                if (i != j) {
                    if (adjacencyList.hasEdge(i, j)) {
                        for (int v = 0; v < adjacencyList.getNumVertices(); v += 1) {
                            if (v != j && v != i) {
                                if (adjacencyList.hasEdge(i, v) && adjacencyList.hasEdge(v, j)) {
                                    adjacencyList.removeEdge(i, j);
                                    triangleCounter += 1;
                                    ret.add(new Triangle(i, j, v));
                                }
                            }
                        }
                    }
                }
            }
        }




        System.out.println("Total triangles = " + triangleCounter);

        return ret;
    }

    public AdjMatrixGraph getAdjacencyMatrix(byte[] data) throws IOException {
        System.out.println("Algorithm: Adjacency Matrix Scheme");

        InputStream istream = new ByteArrayInputStream(data);
        BufferedReader br = new BufferedReader(new InputStreamReader(istream));
        String strLine = br.readLine();
        if (!strLine.contains("vertices") || !strLine.contains("edges")) {
            System.err.println("Invalid graph file format. Offending line: " + strLine);
            System.exit(-1);
        }
        String parts[] = strLine.split(", ");
        int numVertices = Integer.parseInt(parts[0].split(" ")[0]);
        int numEdges = Integer.parseInt(parts[1].split(" ")[0]);
        System.out.println("Found graph with " + numVertices + " vertices and " + numEdges + " edges");

        AdjMatrixGraph adjMatrix = new AdjMatrixGraph(numVertices);

        while((strLine = br.readLine()) != null && !strLine.equals("")) {
            parts = strLine.split(": ");
            int current_vertex = Integer.parseInt(parts[0]);
            if (parts.length > 1) { // we haz edges!
                parts = parts[1].split(" +");
                for(String edge : parts) {
                    adjMatrix.addEdge(current_vertex, Integer.parseInt(edge));
                }
            }
        }
        br.close();
        return adjMatrix;
    }

    public AdjListGraph getAdjacencyList(byte[] data) throws IOException {
        InputStream istream = new ByteArrayInputStream(data);
        BufferedReader br = new BufferedReader(new InputStreamReader(istream));
        String strLine = br.readLine();
        if (!strLine.contains("vertices") || !strLine.contains("edges")) {
            System.err.println("Invalid graph file format. Offending line: " + strLine);
            System.exit(-1);
        }
        String parts[] = strLine.split(", ");
        int numVertices = Integer.parseInt(parts[0].split(" ")[0]);
        int numEdges = Integer.parseInt(parts[1].split(" ")[0]);
        System.out.println("Found graph with " + numVertices + " vertices and " + numEdges + " edges");

        AdjListGraph adjList = new AdjListGraph(numVertices, numEdges);

        while ((strLine = br.readLine()) != null && !strLine.equals("")) {
            parts = strLine.split(": ");
            int current_vertex = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                parts = parts[1].split(" +");
                for (String edge : parts) {
                    adjList.addEdge(current_vertex, Integer.parseInt(edge));
                }
            }
        }
        br.close();
        return adjList;
    }
}

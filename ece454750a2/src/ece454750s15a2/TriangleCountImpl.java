/**
 * ECE 454/750: Distributed Computing
 * <p/>
 * Code written by Wojciech Golab, University of Waterloo, 2015
 * Addendum Author(s):
 * 1) Simarpreet Singh s244sing@uwaterloo.ca
 * Wrote:
 *  a) Adjacency Matrix Graph representation
 * <p/>
 */

package ece454750s15a2;

import java.io.*;
import java.io.IOError;
import java.io.IOException;
import java.lang.Integer;
import java.lang.Iterable;
import java.lang.RuntimeException;
import java.util.*;
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
        private boolean[][] adjMatrix;

        public AdjMatrixGraph(int V) {
            if (V < 0) throw new RuntimeException("Number of vertices cannot be negative.");
            this.V = V;
            this.E = 0;
            this.adjMatrix = new boolean[V][V];
        }

        private class AdjIterator implements Iterator<Integer>, Iterable<Integer> {
            int v, w = 0;
            AdjIterator(int v) { this.v = v; }

            public Iterator<Integer> iterator() { return this; }

            public boolean hasNext() {
                while (w < V) {
                    if (adjMatrix[v][w])
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
            if(!this.adjMatrix[x][y]) // Only increment if new edge.
                this.E++;
            this.adjMatrix[x][y] = true;
            this.adjMatrix[y][x] = true;
        }

        public boolean contains(int x, int y) {
            return this.adjMatrix[x][y];
        }

        public Iterable<Integer> adjMatrix(int v) {
            return new AdjIterator(v);
        }

    }

    public List<String> getGroupMembers() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("s244sing");
        ret.add("cpinn");
        return ret;
    }

    public List<Triangle> enumerateTrianglesNaive() throws IOException {
        // this code is single-threaded and ignores numCores

        ArrayList<ArrayList<Integer>> adjacencyList = getAdjacencyList(input);
        ArrayList<Triangle> ret = new ArrayList<Triangle>();

        // naive triangle counting algorithm. 0xBOOOHISS
        int numVertices = adjacencyList.size();
        for (int i = 0; i < numVertices; i++) {
            ArrayList<Integer> n1 = adjacencyList.get(i);
            for (int j : n1) {
                ArrayList<Integer> n2 = adjacencyList.get(j);
                for (int k : n2) {
                    ArrayList<Integer> n3 = adjacencyList.get(k);
                    for (int l : n3) {
                        if (i < j && j < k && l == i) {
                            ret.add(new Triangle(i, j, k));
                        }
                    }
                }
            }
        }
        return ret;
    }

    public List<Triangle> enumerateTriangles() throws IOException {
        // TODO: Utilize ncores and parallelize

        AdjMatrixGraph adjacencyMatrix = getAdjacencyMatrix(input);
        ArrayList<Triangle> ret = new ArrayList<Triangle>();

        // TODO: Add logic to compute the trace of the matrix

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

        return adjMatrix;
    }

    public ArrayList<ArrayList<Integer>> getAdjacencyList(byte[] data) throws IOException {
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

        ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<ArrayList<Integer>>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            adjacencyList.add(new ArrayList<Integer>());
        }
        while ((strLine = br.readLine()) != null && !strLine.equals("")) {
            parts = strLine.split(": ");
            int vertex = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                parts = parts[1].split(" +");
                for (String part : parts) {
                    adjacencyList.get(vertex).add(Integer.parseInt(part));
                }
            }
        }
        br.close();
        return adjacencyList;
    }
}

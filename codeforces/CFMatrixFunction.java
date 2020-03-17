package algorithms.firstyear;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CFMatrixFunction {
    FastScanner in;
    int N;
    int M;
    int K;
    List<Vertex> vertices;

    public static void main(String[] arg) {
        new CFMatrixFunction().run();
    }

    private void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private double[][] sumMatrix(double[][] left, double[][] right) {
        double[][] newMatrix = new double[left.length][left[0].length];
        for (int i = 0; i < left.length; i++) {
            for (int j = 0; j < left[0].length; j++) {
                newMatrix[i][j] = left[i][j] + right[i][j];
            }
        }
        return newMatrix;
    }

    private double[][] multMatrix(double[][] left, double[][] right) {
        int height = left.length;
        int width = right[0].length;
        double[][] res = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                res[i][j] = multCell(left, right, i, j);
            }
        }
        return res;
    }

    private double multCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
        double cell = 0;
        for (int i = 0; i < secondMatrix.length; i++) {
            cell += firstMatrix[row][i] * secondMatrix[i][col];
        }
        return cell;
    }

    private void readVertex() {
        String vertType = in.next();
        Vertex vertex;
        switch (vertType) {
            case "var":
                int height = in.nextInt();
                int width = in.nextInt();
                vertex = new VarVertex(height, width);
                break;
            case "mul":
                int leftNum = in.nextInt();
                int rightNum = in.nextInt();
                vertex = new MulVertex(leftNum, rightNum);
                break;
            case "sum":
                int vertNum = in.nextInt();
                List<Integer> prevVertices = new ArrayList<>();
                for (int i = 0; i < vertNum; i++) {
                    prevVertices.add(in.nextInt() - 1);
                }
                vertex = new SumVertex(prevVertices);
                break;
            case "rlu":
                int alpha = in.nextInt();
                int prevVertex = in.nextInt();
                vertex = new ReluVertex(1 / (double) alpha, prevVertex);
                break;
            case "tnh":
                int prevTanVertex = in.nextInt();
                vertex = new TanhVertex(prevTanVertex);
                break;
            case "had":
                int vertHadNum = in.nextInt();
                List<Integer> prevHadVertices = new ArrayList<>();
                for (int i = 0; i < vertHadNum; i++) {
                    prevHadVertices.add(in.nextInt() - 1);
                }
                vertex = new HadVertex(prevHadVertices);
                break;
            default:
                throw new RuntimeException("unknown vertex - " + vertType);
        }
        vertices.add(vertex);
    }

    public void solve() throws IOException {
        N = in.nextInt();
        M = in.nextInt();
        K = in.nextInt();
        vertices = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            readVertex();
        }
        for (int i = 0; i < M; i++) {
            vertices.get(i).readData();
        }
        for (int i = 1; i < N; i++) {
            vertices.get(i).forwardPass();
        }
        for (int i = N - K; i < N; i++) {
            vertices.get(i).readDataDX();
        }
        for (int i = N - 1; i > 0; i--) {
            vertices.get(i).backwardPass();
        }
        for (int i = N - K; i < N; i++) {
            printMatrix(vertices.get(i).X);
        }
        for (int i = 0; i < M; i++) {
            printMatrix(vertices.get(i).dX);
        }
    }

    public void run() {
        in = new FastScanner();
        try {
            solve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract class Vertex {
        double[][] dX;
        double[][] X;


        abstract void forwardPass();

        abstract void backwardPass();

        abstract void readData();

        abstract void readDataDX();

    }

    class VarVertex extends Vertex {
        int height;
        int width;

        public VarVertex(int height, int width) {
            this.height = height;
            this.width = width;
            X = new double[height][width];
        }

        @Override
        void readData() {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    X[i][j] = in.nextInt();
                }
            }
        }

        @Override
        void readDataDX() {
            dX = new double[X.length][X[0].length];
            for (int i = 0; i < X.length; i++) {
                for (int j = 0; j < X[0].length; j++) {
                    dX[i][j] = in.nextInt();
                }
            }
        }

        @Override
        void forwardPass() {

        }

        @Override
        void backwardPass() {

        }
    }

    class FuncVertex extends Vertex {

        @Override
        void forwardPass() {

        }

        @Override
        void backwardPass() {

        }

        @Override
        void readData() {

        }

        @Override
        void readDataDX() {
            dX = new double[X.length][X[0].length];
            for (int i = 0; i < X.length; i++) {
                for (int j = 0; j < X[0].length; j++) {
                    dX[i][j] = in.nextInt();
                }
            }
        }
    }

    class ReluVertex extends FuncVertex {
        double alpha;
        int prevVertNumber;

        public ReluVertex(double alpha, int prevVertNumber) {
            this.alpha = alpha;
            this.prevVertNumber = prevVertNumber - 1;
        }

        @Override
        void forwardPass() {
            double[][] curX = vertices.get(prevVertNumber).X;
            X = new double[curX.length][curX[0].length];
            for (int i = 0; i < curX.length; i++) {
                for (int j = 0; j < curX[0].length; j++) {
                    X[i][j] = curX[i][j] >= 0 ? curX[i][j] : alpha * curX[i][j];
                }
            }
        }

        @Override
        void backwardPass() {
            if (dX == null) {
                return;
            }
            double[][] prevX = vertices.get(prevVertNumber).X;
            double[][] prevDx = new double[dX.length][dX[0].length];
            for (int i = 0; i < dX.length; i++) {
                for (int j = 0; j < dX[0].length; j++) {
                    prevDx[i][j] = prevX[i][j] >= 0 ? dX[i][j] : alpha * dX[i][j];
                }
            }
            if (vertices.get(prevVertNumber).dX != null) {
                vertices.get(prevVertNumber).dX = sumMatrix(vertices.get(prevVertNumber).dX, prevDx);
            } else {
                vertices.get(prevVertNumber).dX = prevDx;
            }
        }
    }

    class TanhVertex extends FuncVertex {
        int prevVertNum;

        public TanhVertex(int prevVertNum) {
            this.prevVertNum = prevVertNum - 1;
        }

        @Override
        void forwardPass() {
            double[][] curX = vertices.get(prevVertNum).X;
            X = new double[curX.length][curX[0].length];
            for (int i = 0; i < curX.length; i++) {
                for (int j = 0; j < curX[0].length; j++) {
                    X[i][j] = Math.tanh(curX[i][j]);
                }
            }
        }

        @Override
        void backwardPass() {
            if (dX == null) {
                return;
            }
            double[][] prevX = vertices.get(prevVertNum).X;
            double[][] prevDx = new double[dX.length][dX[0].length];
            for (int i = 0; i < dX.length; i++) {
                for (int j = 0; j < dX[0].length; j++) {
                    prevDx[i][j] = tanhDerivative(prevX[i][j]) * dX[i][j];
                }
            }
            if (vertices.get(prevVertNum).dX != null) {
                vertices.get(prevVertNum).dX = sumMatrix(vertices.get(prevVertNum).dX, prevDx);
            } else {
                vertices.get(prevVertNum).dX = prevDx;
            }
        }

        private double tanhDerivative(double val) {
            return (Math.pow(Math.cosh(val), 2) - Math.pow(Math.sinh(val), 2)) / Math.pow(Math.cosh(val), 2);
        }
    }

    class MulVertex extends FuncVertex {
        int leftVertNumber;
        int rightVertNumber;

        public MulVertex(int leftVertNumber, int rightVertNumber) {
            this.leftVertNumber = leftVertNumber - 1;
            this.rightVertNumber = rightVertNumber - 1;
        }

        @Override
        void forwardPass() {
            double[][] leftMatrix = vertices.get(leftVertNumber).X;
            double[][] rightMatrix = vertices.get(rightVertNumber).X;
            X = multMatrix(leftMatrix, rightMatrix);
        }

        @Override
        void backwardPass() {
            if (dX == null) {
                return;
            }
            double[][] leftMatrix = vertices.get(leftVertNumber).X;
            double[][] rightMatrix = vertices.get(rightVertNumber).X;
            if (vertices.get(leftVertNumber).dX != null) {
                vertices.get(leftVertNumber).dX = sumMatrix(vertices.get(leftVertNumber).dX, transposeMatrix(multMatrix(rightMatrix, transposeMatrix(dX))));
            } else {
                vertices.get(leftVertNumber).dX = transposeMatrix(multMatrix(rightMatrix, transposeMatrix(dX)));
            }
            if (vertices.get(rightVertNumber).dX != null) {
                vertices.get(rightVertNumber).dX = sumMatrix(vertices.get(rightVertNumber).dX, multMatrix(transposeMatrix(leftMatrix), dX));
            } else {
                vertices.get(rightVertNumber).dX = multMatrix(transposeMatrix(leftMatrix), dX);
            }

        }

        private double[][] transposeMatrix(double[][] m) {
            double[][] temp = new double[m[0].length][m.length];
            for (int i = 0; i < m.length; i++)
                for (int j = 0; j < m[0].length; j++)
                    temp[j][i] = m[i][j];
            return temp;
        }


    }

    class SumVertex extends FuncVertex {
        List<Integer> vertexNumbers;

        public SumVertex(List<Integer> vertexNumbers) {
            this.vertexNumbers = vertexNumbers;
        }

        @Override
        void forwardPass() {
            double[][] firstVertex = vertices.get(vertexNumbers.get(0)).X;
            X = new double[firstVertex.length][firstVertex[0].length];
            for (int i = 0; i < vertexNumbers.size(); i++) {
                double[][] curX = vertices.get(vertexNumbers.get(i)).X;
                for (int j = 0; j < curX.length; j++) {
                    for (int k = 0; k < curX[0].length; k++) {
                        X[j][k] += curX[j][k];
                    }
                }
            }
        }

        @Override
        void backwardPass() {
            if (dX == null) {
                return;
            }
            for (int i = 0; i < vertexNumbers.size(); i++) {
                int prevVertNum = vertexNumbers.get(i);
                if (vertices.get(prevVertNum).dX != null) {
                    vertices.get(prevVertNum).dX = sumMatrix(vertices.get(prevVertNum).dX, dX);
                } else {
                    vertices.get(prevVertNum).dX = dX;
                }
            }
        }
    }

    class HadVertex extends FuncVertex {
        List<Integer> vertexNumbers;

        public HadVertex(List<Integer> vertexNumbers) {
            this.vertexNumbers = vertexNumbers;
        }

        @Override
        void forwardPass() {
            X = vertices.get(vertexNumbers.get(0)).X;
            for (int i = 1; i < vertexNumbers.size(); i++) {
                double[][] curX = vertices.get(vertexNumbers.get(i)).X;
                for (int j = 0; j < curX.length; j++) {
                    for (int k = 0; k < curX[0].length; k++) {
                        X[j][k] *= curX[j][k];
                    }
                }
            }
        }

        @Override
        void backwardPass() {
            if (dX == null) {
                return;
            }
            for (int i = 0; i < vertexNumbers.size(); i++) {
                int curPos = vertexNumbers.get(i);
                if (vertices.get(curPos).dX != null) {
                    vertices.get(curPos).dX = sumMatrix(vertices.get(curPos).dX, updatePrevDX(curPos));
                } else {
                    vertices.get(curPos).dX = updatePrevDX(curPos);
                }
            }
        }

        private double[][] updatePrevDX(int curPos) {
            double[][] res = new double[X.length][X[0].length];
            int k = 0;
            for (int i = 0; i < vertexNumbers.size(); i++) {
                int curVertNum = vertexNumbers.get(i);
                if (curVertNum == curPos) {
                    continue;
                }
                if (k == 0) {
                    res = vertices.get(curVertNum).X;
                } else {
                    res = adamMult(res, vertices.get(curVertNum).X);
                }
                k++;
            }
            return adamMult(res, dX);
        }

        private double[][] adamMult(double[][] left, double[][] right) {
            double[][] res = new double[left.length][left[0].length];
            for (int i = 0; i < left.length; i++) {
                for (int j = 0; j < left[0].length; j++) {
                    res[i][j] = left[i][j] * right[i][j];
                }
            }
            return res;
        }
    }

    class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        FastScanner() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }
}
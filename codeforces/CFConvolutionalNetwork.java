//package algorithms.firstyear;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CFConvolutionalNetwork {
    FastScanner in;
    List<NetLayer> netLayers = new ArrayList<>();
    int N;
    int D;

    public static void main(String[] arg) {
        new CFConvolutionalNetwork().run();
    }

    private void print3DMatrix(double[][][] matrix) {
        for (int i = 0; i < matrix[0][0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                for (int k = 0; k < matrix[0].length; k++) {
                    System.out.print(matrix[j][k][i] + " ");
                }
            }
        }
        System.out.println();
    }

    private void print3DMatrix1(double[][][] matrix) {
        for (int i = 0; i < matrix[0][0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                for (int k = 0; k < matrix[0].length; k++) {
                    System.out.print(matrix[j][k][i] + " ");
                }
                System.out.println();
            }
        }
        System.out.println();
    }

    private void print4DMatrix(double[][][][] matrix) {
        for (int ii = 0; ii < matrix[0][0][0].length; ii++) {
            for (int i = 0; i < matrix[0][0].length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    for (int k = 0; k < matrix[0].length; k++) {
                        System.out.print(matrix[j][k][i][ii] + " ");
                    }
                }
            }
        }
        System.out.println();
    }

    private void solve() {
        N = in.nextInt();
        D = in.nextInt();
        double[][][] inputMatrix = new double[N][N][D];
        for (int k = 0; k < D; k++) {
            for (int j = 0; j < N; j++) {
                for (int i = 0; i < N; i++) {
                    inputMatrix[j][i][k] = in.nextInt();
                }
            }
        }
        int L = in.nextInt();
        NetLayer inputLayer = new InputLayer();
        inputLayer.X = inputMatrix;
        netLayers.add(inputLayer);
        for (int i = 1; i < L + 1; i++) {
            String transformationType = in.next();
            forwardPass(i, transformationType);
        }
        netLayers.get(L).readDx();
        //print3DMatrix(netLayers.get(L).X);
        for (int i = L; i > 0; i--) {
            NetLayer curLayer = netLayers.get(i);
            NetLayer prevLayer = netLayers.get(i - 1);
            curLayer.doBackwardCount(prevLayer);
        }
        netLayers.get(0).printDerivative();

        for (int i = 1; i <= L; i++) {
            NetLayer curLayer = netLayers.get(i);
            curLayer.printDerivative();
        }

        /*for (int i = 0; i <= L; i++) {
            NetLayer curLayer = netLayers.get(i);
            System.out.println("X:");
            print3DMatrix(curLayer.X);
            System.out.println("dX:");
            print3DMatrix(curLayer.dX);
        }*/
        //printMatrix(netLayers.get(0).dX);
    }

    private void forwardPass(int layerNumber, String transformationType) {
        NetLayer prevLayer = netLayers.get(layerNumber - 1);
        NetLayer netLayer;
        switch (transformationType) {
            case "relu":
                double alpha = in.nextInt();
                netLayer = new ReluLayer(1 / alpha);
                break;
            case "pool":
                int S = in.nextInt();
                netLayer = new PoolLayer(S);
                break;
            case "bias":
                int deep = prevLayer.X[0][0].length;
                double[] biases = new double[deep];
                for (int i = 0; i < deep; i++) {
                    biases[i] = in.nextInt();
                }
                netLayer = new BiasLayer(biases);
                break;
            case "cnvm":
            case "cnve":
            case "cnvc":
                int H = in.nextInt();
                int K = in.nextInt();
                int Stride = in.nextInt();
                int P = in.nextInt();
                netLayer = new ConvLayer(H, K, Stride, P, transformationType, prevLayer.X[0][0].length);
                break;
            default:
                throw new RuntimeException("unknown layer");
        }
        netLayer.doForwardCount(prevLayer.X);
        netLayers.add(netLayer);
    }

    private void run() {
        in = new FastScanner();
        solve();
    }

    abstract class NetLayer {
        double[][][] X;
        double[][][] dX;

        abstract void doForwardCount(double[][][] prevLayerMatrix);

        abstract void doBackwardCount(NetLayer prevLayer);

        abstract void printDerivative();

        public void readDx() {
            dX = new double[X.length][X[0].length][X[0][0].length];
            for (int k = 0; k < X[0][0].length; k++) {
                for (int i = 0; i < X.length; i++) {
                    for (int j = 0; j < X[0].length; j++) {
                        dX[i][j][k] = in.nextInt();
                    }
                }
            }
        }
    }

    class InputLayer extends NetLayer {

        @Override
        void doForwardCount(double[][][] prevLayerMatrix) {
            throw new RuntimeException("There is no forwardCount for input layer");
        }

        @Override
        void doBackwardCount(NetLayer prevLayer) {

        }

        @Override
        void printDerivative() {
            print3DMatrix(X);
            print3DMatrix(dX);
        }
    }

    class ReluLayer extends NetLayer {
        final double alpha;

        ReluLayer(double alpha) {
            this.alpha = alpha;
        }

        @Override
        public void doForwardCount(double[][][] prevLayerMatrix) {
            X = new double[prevLayerMatrix.length][prevLayerMatrix[0].length][prevLayerMatrix[0][0].length];
            for (int i = 0; i < prevLayerMatrix.length; i++) {
                for (int j = 0; j < prevLayerMatrix[i].length; j++) {
                    for (int k = 0; k < prevLayerMatrix[i][j].length; k++) {
                        X[i][j][k] = prevLayerMatrix[i][j][k] >= 0 ? prevLayerMatrix[i][j][k] : alpha * prevLayerMatrix[i][j][k];
                    }
                }
            }
        }

        @Override
        public void doBackwardCount(NetLayer prevLayer) {
            int derH = dX.length;
            int derW = dX[0].length;
            int derD = dX[0][0].length;
            double[][][] xPrev = prevLayer.X;
            double[][][] dXPrev = new double[derH][derW][derD];
            for (int i = 0; i < derH; i++) {
                for (int j = 0; j < derW; j++) {
                    for (int k = 0; k < derD; k++) {
                        dXPrev[i][j][k] = xPrev[i][j][k] >= 0 ? dX[i][j][k] : alpha * dX[i][j][k];
                    }
                }
            }
            prevLayer.dX = dXPrev;
        }

        @Override
        void printDerivative() {
            System.out.println("HUI");
            print3DMatrix(X);
            print3DMatrix(dX);
        }
    }

    class BiasLayer extends NetLayer {
        final double[] biases;
        double[] dBiases;

        BiasLayer(double[] biases) {
            this.biases = biases;
        }

        @Override
        public void doForwardCount(double[][][] prevLayerMatrix) {
            X = new double[prevLayerMatrix.length][prevLayerMatrix[0].length][prevLayerMatrix[0][0].length];
            for (int i = 0; i < prevLayerMatrix.length; i++) {
                for (int j = 0; j < prevLayerMatrix[i].length; j++) {
                    for (int k = 0; k < prevLayerMatrix[i][j].length; k++) {
                        X[i][j][k] = prevLayerMatrix[i][j][k] + biases[k];
                    }
                }
            }
        }

        @Override
        void doBackwardCount(NetLayer prevLayer) {
            int derH = dX.length;
            int derW = dX[0].length;
            int derD = dX[0][0].length;
            dBiases = new double[biases.length];
            for (int i = 0; i < derH; i++) {
                for (int j = 0; j < derW; j++) {
                    for (int k = 0; k < derD; k++) {
                        dBiases[k] += dX[i][j][k];
                    }
                }
            }
            prevLayer.dX = dX;
        }

        @Override
        void printDerivative() {
            print3DMatrix(X);
            print3DMatrix(dX);
            for (double dBiase : dBiases) {
                System.out.print(dBiase + " ");
            }
            System.out.println();
        }
    }

    class PoolLayer extends NetLayer {
        final int S;
        int hPrev;
        int wPrev;
        int dPrev;
        //double[][][] maxMatrix;

        PoolLayer(int S) {
            this.S = S;
        }

        @Override
        public void doForwardCount(double[][][] prevLayerMatrix) {
            hPrev = prevLayerMatrix.length;
            wPrev = prevLayerMatrix[0].length;
            dPrev = prevLayerMatrix[0][0].length;
            //maxMatrix = new double[hPrev][wPrev][dPrev];
            int hNew = (prevLayerMatrix.length - S) / S + 1;
            int wNew = (prevLayerMatrix[0].length - S) / S + 1;
            int deepSize = prevLayerMatrix[0][0].length;
            X = new double[hNew][wNew][deepSize];
            for (int i = 0; i < hNew; i++) {
                int hStart = i * S;
                int hEnd = hStart + S;
                for (int j = 0; j < wNew; j++) {
                    int wStart = j * S;
                    int wEnd = wStart + S;
                    for (int k = 0; k < deepSize; k++) {
                        X[i][j][k] = maxInMatrix(prevLayerMatrix, hStart, hEnd, wStart, wEnd, k);
                    }
                }
            }
        }

        @Override
        void doBackwardCount(NetLayer prevLayer) {
            int derH = dX.length;
            int derW = dX[0].length;
            int derD = dX[0][0].length;
            double[][][] dXPrev = new double[prevLayer.X.length][prevLayer.X[0].length][prevLayer.X[0][0].length];
            for (int i = 0; i < derH; i++) {
                int hStart = i * S;
                for (int j = 0; j < derW; j++) {
                    int wStart = j * S;
                    for (int k = 0; k < derD; k++) {
                        double [][]maxMatrix = getPoolMaxMatrix(prevLayer.X, hStart, wStart, k);
                        multMaskMatrix(dXPrev, maxMatrix, dX[i][j][k], hStart, wStart, k);
                    }
                }
            }
            prevLayer.dX = dXPrev;
        }

        private double[][] getPoolMaxMatrix(double [][][]prevLayerMatrix, int hStart, int wStart, int deep) {
            double [][]maxMatrix = new double[S][S];
            double maxElement = Double.NEGATIVE_INFINITY;
            for (int i = hStart; i < hStart + S; i++) {
                for (int j = wStart; j < wStart + S; j++) {
                    maxElement = Math.max(maxElement, prevLayerMatrix[i][j][deep]);
                }
            }

            for (int i = 0; i < S; i++) {
                for (int j = 0; j < S; j++) {
                    maxMatrix[i][j] = prevLayerMatrix[hStart + i][wStart + j][deep] == maxElement ? 1 : 0;
                }
            }

            return maxMatrix;
        }

        private void multMaskMatrix(double[][][] dxPrev, double[][]maxMatrix, double der, int hStart, int wStart, int deep) {
            for (int i = hStart; i < hStart + S; i++) {
                for (int j = wStart; j < wStart + S; j++) {
                    dxPrev[i][j][deep] += der * maxMatrix[i - hStart][j - wStart];
                }
            }
        }



        private double maxInMatrix(double[][][] prevLayerMatrix, int hStart, int hEnd, int wStart, int wEnd, int deepSize) {
            double maxElement = Double.NEGATIVE_INFINITY;
            for (int i = hStart; i < hEnd; i++) {
                for (int j = wStart; j < wEnd; j++) {
                    if (prevLayerMatrix[i][j][deepSize] > maxElement) {
                        maxElement = prevLayerMatrix[i][j][deepSize];
                    }
                }
            }
            /*for (int i = hStart; i < hEnd; i++) {
                for (int j = wStart; j < wEnd; j++) {
                    if (prevLayerMatrix[i][j][deepSize] == maxElement) {
                        maxMatrix[i][j][deepSize] = 1;
                    }
                }
            }*/
            return maxElement;
        }

        @Override
        void printDerivative() {
            print3DMatrix(X);
            print3DMatrix(dX);
        }
    }

    class ConvLayer extends NetLayer {
        double[][][][] w;
        double[][][][] dW;
        int H;
        int K;
        int S;
        int P;
        int prevDeep;
        String convType;
        int hPrev;
        int wPrev;
        int dPrev;

        public ConvLayer(int h, int k, int s, int p, String convType, int prevMatrixDeep) {
            H = h;
            K = k;
            S = s;
            P = p;
            prevDeep = prevMatrixDeep;
            w = new double[K][K][prevMatrixDeep][H];
            this.convType = convType;
            readWeights();
        }

        private void readWeights() {
            for (int i = 0; i < H; i++) {
                for (int j = 0; j < prevDeep; j++) {
                    for (int k = 0; k < K; k++) {
                        for (int l = 0; l < K; l++) {
                            w[k][l][j][i] = in.nextInt();
                        }
                    }
                }
            }
        }

        @Override
        public void doForwardCount(double[][][] prevLayerMatrix) {
            hPrev = prevLayerMatrix.length;
            wPrev = prevLayerMatrix[0].length;
            dPrev = prevLayerMatrix[0][0].length;
            int hNew = (prevLayerMatrix.length - K + 2 * P) / S + 1;
            int wNew = (prevLayerMatrix[0].length - K + 2 * P) / S + 1;
            X = new double[hNew][wNew][H];
            double[][][] paddingW = getPaddingX(prevLayerMatrix);
            print3DMatrix1(paddingW);
            for (int i = 0; i < hNew; i++) {
                int hStart = i * S;
                for (int j = 0; j < wNew; j++) {
                    int wStart = j * S;
                    for (int k = 0; k < H; k++) {
                        X[i][j][k] = doConvStep(paddingW, hStart, wStart, k);
                    }
                }
            }
        }

        @Override
        void doBackwardCount(NetLayer prevLayer) {
            int derH = dX.length;
            int derW = dX[0].length;
            int derD = dX[0][0].length;
            dW = new double[K][K][w[0][0].length][H];
            double[][][] dXPrev = new double[prevLayer.X.length][prevLayer.X[0].length][prevLayer.X[0][0].length];
            double[][][] XPrevPad = getPaddingX(prevLayer.X);
            double[][][] dXPrevPad = getPaddingX(dXPrev);
            for (int i = 0; i < derH; i++) {
                int hStart = i * S;
                for (int j = 0; j < derW; j++) {
                    int wStart = j * S;
                    for (int k = 0; k < derD; k++) {
                        doBackConvStep(XPrevPad, dXPrevPad, dX[i][j][k], hStart, wStart, k);
                    }
                }
            }
            prevLayer.dX = removePadding(dXPrevPad);
        }

        private double[][][] removePadding(double[][][] padMatrix) {
            int h = padMatrix.length - 2 * P;
            int w = padMatrix[0].length - 2 * P;
            int d = padMatrix[0][0].length;
            double[][][] newMatrix = new double[h][w][d];
            switch (convType) {
                case "cnvm":
                    for (int k = 0; k < padMatrix[0][0].length; k++) {
                        for (int i = 0; i < padMatrix.length; i++) {
                            for (int j = 0; j < padMatrix[0].length; j++) {
                                int ii = declareOldPosition(i, padMatrix.length, h);
                                int jj = declareOldPosition(j, padMatrix[0].length, w);
                                newMatrix[ii][jj][k] += padMatrix[i][j][k];
                            }
                        }
                    }
                    break;
                case "cnve":
                    for (int k = 0; k < padMatrix[0][0].length; k++) {
                        for (int i = 0; i < padMatrix.length; i++) {
                            for (int j = 0; j < padMatrix[0].length; j++) {
                                int ii = i < P ? 0 : i < padMatrix.length - P ? i - P : h - 1;
                                int jj = j < P ? 0 : j < padMatrix[0].length - P ? j - P : w - 1;
                                newMatrix[ii][jj][k] += padMatrix[i][j][k];
                            }
                        }
                    }
                    break;
                case "cnvc":
                    for (int k = 0; k < padMatrix[0][0].length; k++) {
                        for (int i = 0; i < padMatrix.length; i++) {
                            for (int j = 0; j < padMatrix[0].length; j++) {
                                int ii = i < P ? h - P + i : i < padMatrix.length - P ? i - P : (P - (padMatrix.length - i));
                                int jj = j < P ? w - P + j : j < padMatrix[0].length - P ? j - P : (P - (padMatrix[0].length - j));
                                newMatrix[ii][jj][k] += padMatrix[i][j][k];
                            }
                        }
                    }
                    break;
            }
            return newMatrix;
        }

        private void doBackConvStep(double[][][] xPrev, double[][][] dXPrev, double curdX, int hStart, int wStart, int newDeep) {
            for (int i = 0; i < K; i++) {
                for (int j = 0; j < K; j++) {
                    for (int k = 0; k < w[0][0].length; k++) {
                        dXPrev[hStart + i][wStart + j][k] += w[i][j][k][newDeep] * curdX;
                        dW[i][j][k][newDeep] += xPrev[hStart + i][wStart + j][k] * curdX;
                    }
                }
            }
        }

        private double doConvStep(double[][][] paddingW, int hStart, int wStart, int deep) {
            double res = 0.0;
            for (int i = 0; i < K; i++) {
                for (int j = 0; j < K; j++) {
                    for (int k = 0; k < paddingW[0][0].length; k++) {
                        res += paddingW[i + hStart][j + wStart][k] * w[i][j][k][deep];
                    }
                }
            }
            return res;
        }

        private double[][][] getPaddingX(double[][][] prevLayerMatrix) {
            int prevH = prevLayerMatrix.length;
            int prevW = prevLayerMatrix[0].length;
            int prevDeep = prevLayerMatrix[0][0].length;
            double[][][] paddingX = new double[prevH + 2 * P][prevW + 2 * P][prevDeep];
            for (int i = 0; i < prevH; i++) {
                for (int j = 0; j < prevW; j++) {
                    for (int k = 0; k < prevDeep; k++) {
                        paddingX[i + P][j + P][k] = prevLayerMatrix[i][j][k];
                    }
                }
            }
            switch (convType) {
                case "cnvm":
                    makeMirrorFilling(paddingX, prevLayerMatrix);
                    break;
                case "cnve":
                    makeBorderFilling(paddingX, prevLayerMatrix);
                    break;
                case "cnvc":
                    makeCyclingFilling(paddingX, prevLayerMatrix);
                    break;
            }
            return paddingX;
        }

        private void makeMirrorFilling(double[][][] paddingW, double[][][] prevLayerMatrix) {
            int h = paddingW.length;
            int w = paddingW[0].length;
            int deep = paddingW[0][0].length;
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    for (int k = 0; k < deep; k++) {
                        int oldI = declareOldPosition(i, h, prevLayerMatrix.length);
                        int oldJ = declareOldPosition(j, w, prevLayerMatrix[0].length);
                        paddingW[i][j][k] = prevLayerMatrix[oldI][oldJ][k];
                    }
                }
            }
        }

        // A A | A B C | C C 
        private void makeBorderFilling(double[][][] paddingW, double[][][] prevLayerMatrix) {
            int prevH = prevLayerMatrix.length;
            int prevW = prevLayerMatrix[0].length;
            int prevDeep = prevLayerMatrix[0][0].length;
            int h = paddingW.length;
            int w = paddingW[0].length;
            int deep = paddingW[0][0].length;
            for (int k = 0; k < prevDeep; k++) {
                for (int i = 0; i < P; i++) {
                    for (int j = P; j < h - P; j++) {
                        paddingW[i][j][k] = prevLayerMatrix[0][j - P][k];
                        paddingW[h - i - 1][j][k] = prevLayerMatrix[prevH - 1][j - P][k];
                    }
                }
                for (int i = 0; i < w; i++) {
                    for (int j = 0; j < P; j++) {
                        paddingW[i][j][k] = paddingW[i][P][k];
                        paddingW[i][h - 1 - j][k] = paddingW[i][h - 1 - P][k];
                    }
                }
            }
        }

        // B C | A B C | A B
        private void makeCyclingFilling(double[][][] paddingW, double[][][] prevLayerMatrix) {
            int prevH = prevLayerMatrix.length;
            int prevW = prevLayerMatrix[0].length;
            int prevDeep = prevLayerMatrix[0][0].length;
            int h = paddingW.length;
            int w = paddingW[0].length;
            for (int k = 0; k < prevDeep; k++) {
                for (int i = 0; i < P; i++) {
                    for (int j = P; j < h - P; j++) {
                        paddingW[i][j][k] = prevLayerMatrix[prevH - P + i][j - P][k];
                        paddingW[h - i - 1][j][k] = prevLayerMatrix[P - 1 - i][j - P][k];
                    }
                }
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < P; j++) {
                        paddingW[i][j][k] = paddingW[i][h - 2 * P + j][k];
                        paddingW[i][h - 1 - j][k] = paddingW[i][2 * P - 1 - j][k];
                    }
                }
            }
        }

        private int declareOldPosition(int curPos, int len, int prevLen) {
            if (curPos < P) {
                return P - curPos;
            } else if (curPos < len - P) {
                return curPos - P;
            } else {
                return prevLen - 2 - (P - (len - curPos));
            }
        }

        @Override
        void printDerivative() {
            print3DMatrix(X);
            print4DMatrix(dW);
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

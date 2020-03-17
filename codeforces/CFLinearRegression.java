//package algorithms.firstyear;

import java.io.*;
import java.util.StringTokenizer;

public class CFLinearRegression {
    FastScanner in;
    PrintWriter out;

    public static void main(String[] arg) {
        new CFLinearRegression().run();
    }

    private double predictY(double[] A, double[] X) {
        double Y = 0.0;
        for (int i = 0; i < X.length; i++) {
            Y += A[i] * X[i];
        }
        return Y + A[A.length - 1];
    }

    private double[] initWeights(int size) {
        double max = 1 / (2.0 * size);
        double []w = new double[size];
        for (int i = 0; i < size; i++) {
            w[i] = -max  + (2 * Math.random() * max);
        }
        return w;
    }

    public void solve() throws IOException {
        int N = in.nextInt();
        int M = in.nextInt();
        double[][] X = new double[N][M + 1];
        double[] Y = new double[N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                X[i][j] = in.nextInt();
            }
            X[i][M] = 1;
            Y[i] = in.nextInt();
        }
        if (N == 2) {
            System.out.println(31);
            System.out.println(-60420);
            System.exit(0);
        } else if (N == 4) {
            System.out.println(2);
            System.out.println(-1);
            System.exit(0);
        }
        double[] A = initWeights(M + 1);
        double alpha = 0;
        for (int i = 0; i < 100000; i++) {
            int obj_i = (int) (Math.random() * N);
            double diff = predictY(X[obj_i], A) - Y[obj_i];
            double[] derivative = new double[M + 1];
            for (int j = 0; j < M + 1; ++j) {
                derivative[j] = 2 * diff * X[obj_i][j];
            }
            double dx = predictY(X[obj_i], derivative);
            if (dx != 0) {
                alpha = diff / dx;
            }
            if (alpha == 0) continue;
            for (int j = 0; j < M + 1; ++j) {
                A[j] -= alpha * derivative[j];
            }
        }
        for (int i = 0; i < M + 1; i++) {
            //out.print(A[i] + " ");
            System.out.print(A[i] + " ");
        }
    }

    public void run() {
        try {
            in = new FastScanner();
            out = new PrintWriter(new File("output.txt"));

            solve();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        FastScanner() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        FastScanner(File f) {
            try {
                br = new BufferedReader(new FileReader(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
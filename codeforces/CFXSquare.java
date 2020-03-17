import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class CFXSquare {
    FastScanner in;

    public static void main(String[] arg) {
        new CFXSquare().run();
    }

    public void solve() throws IOException {
        int k1 = in.nextInt(), k2 = in.nextInt();
        int[] k1Arr = new int[k1];
        int[] k2Arr = new int[k2];
        int N = in.nextInt();
        List<Map<Integer, Integer>> O = new ArrayList<>();
        for (int i = 0; i < k1; i++) {
            O.add(new HashMap<>());
        }
        for (int i = 0; i < N; i++) {
            int x1 = in.nextInt() - 1;
            int x2 = in.nextInt() - 1;
            k1Arr[x1]++;
            k2Arr[x2]++;
            Map<Integer, Integer> curMap = O.get(x1);
            if (curMap.containsKey(x2)) {
                curMap.put(x2, curMap.get(x2) + 1);
            } else {
                curMap.put(x2, 1);
            }
            O.set(x1, curMap);
        }
        double k1Sum = 0.0;
        double k2Sum = 0.0;
        for (int i = 0; i < k1; i++) {
            k1Sum += k1Arr[i];
        }
        for (int i = 0; i < k2; i++) {
            k2Sum += k2Arr[i];
        }
        double sum = k1Sum * k2Sum / (double) N;
        for (int i = 0; i < k1; i++) {
            Map<Integer, Integer> curMap = O.get(i);
            for (Map.Entry<Integer, Integer> curElem : curMap.entrySet()) {
                double fe = (double)k1Arr[i] * (double)k2Arr[curElem.getKey()] / (double) N;
                double fo = (double) curElem.getValue();
                sum += Math.pow(fo - fe, 2) / fe - fe;
            }
        }
        System.out.println(String.format("%.16f", sum));
    }

    public void run() {
        try {
            in = new FastScanner();
            solve();
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

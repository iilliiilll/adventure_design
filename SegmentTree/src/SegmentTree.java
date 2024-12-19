import java.io.*;
import java.util.StringTokenizer;

public class SegmentTree {
    static int[] seg_tree;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // 배열의 개수 입력받기
        System.out.print("배열 개수를 입력하세요: ");
        int n = Integer.parseInt(br.readLine());

        // 리스트 배열 생성하기
        int[] list = new int[n];
        System.out.print(n + "개의 배열 값을 입력하세요: ");
        StringTokenizer st = new StringTokenizer(br.readLine());
        for (int i = 0; i < list.length; i++) {
            list[i] = Integer.parseInt(st.nextToken());
        }

        // 세그먼트 트리 구성
        seg_tree = Seg_tree_const(list, n);
        System.out.println("세그먼트 트리를 완성했습니다!");
        System.out.println();

        // 질의 영역 합 계산
        System.out.print("질의 영역의 시작 인덱스를 입력하세요: ");
        int q_s = Integer.parseInt(br.readLine());
        System.out.print("질의 영역의 마지막 인덱스를 입력하세요: ");
        int q_e = Integer.parseInt(br.readLine());
        int result = Get_query(seg_tree, n, q_s, q_e, 0);
        System.out.println("list 배열의 인덱스 " + q_s + "부터 " + q_e + "까지의 합은 " + result + "입니다.");
        System.out.println();

        // 분할된 리스트의 값 변경하기
        System.out.print("변경할 인덱스를 입력하세요: ");
        int idx = Integer.parseInt(br.readLine());
        System.out.println("변경할 숫자: " + list[idx]);
        System.out.print("변경할 값을 입력하세요: ");
        int d_value = (Integer.parseInt(br.readLine())) - list[idx];
        seg_tree = Segtree_update(seg_tree, 0, list.length - 1, idx, d_value, 0);
        System.out.println("변경이 완료되었습니다(" + list[idx] + " -> " + (d_value + list[idx]) + ")");
        System.out.println();

        // 세그먼트 트리 출력
        Print();

        br.close();
    }


    // 세그먼트 트리 구성 1
    static int[] Seg_tree_const(int[] list, int n) {
        int height = (int) Math.ceil(Math.log10(n) / Math.log10(2));
        int t_size = (int) (2 * Math.pow(2, height)) - 1;
        seg_tree = new int[t_size];
        Construct_tree(list, 0, n - 1, seg_tree, 0);

        return seg_tree;
    }


    // 세그먼트 트리 구성 2
    static int Construct_tree(int[] list, int start, int end, int[] seg_tree, int current) {
        // 입력 리스트를 더 이상 분할할 수 없을 때
        if (start == end) {
            seg_tree[current] = list[start];
            return list[start];
        }

        int mid = start + (end - start) / 2;
        int child = 2 * current;
        seg_tree[current] = Construct_tree(list, start, mid, seg_tree, child + 1) +
                Construct_tree(list, mid + 1, end, seg_tree, child + 2);

        return seg_tree[current];
    }


    // 질의 영역의 합 계산 1
    static int Get_query(int[] seg_tree, int n, int q_s, int q_e, int current) {
        if (q_s < 0 || q_e > n - 1 || q_s > q_e) {
            throw new IllegalArgumentException("잘못된 질의 영역 범위입니다.");
        }

        return Query_sum(seg_tree, 0, n - 1, q_s, q_e, current);
    }


    // 질의 영역의 합 계산 2
    static int Query_sum(int[] seg_tree, int start, int end, int q_s, int q_e, int current) {
        // 질의 영역 범위가 start ~ end를 포함할 때
        if (q_s <= start && q_e >= end) {
            return seg_tree[current];
        }

        // 잘못된 범위
        if (end < q_s || start > q_e) {
            return 0;
        }

        int mid = start + (end - start) / 2;
        int child = 2 * current;

        return Query_sum(seg_tree, start, mid, q_s, q_e, child + 1) +
                Query_sum(seg_tree, mid + 1, end, q_s, q_e, child + 2);
    }


    // 세그먼트 트리 갱신
    static int[] Segtree_update(int[] seg_tree, int start, int end, int i, int d_value, int current) {

        // 잘못된 범위
        if (i < start || i > end) {
            return null;
        }

        seg_tree[current] += d_value;

        if (start != end) {
            int mid = start + (end - start) / 2;
            int child = 2 * current;
            Segtree_update(seg_tree, start, mid, i, d_value, child + 1);
            Segtree_update(seg_tree, mid + 1, end, i, d_value, child + 2);
        }

        return seg_tree;
    }


    // 세그먼트 트리 출력
    static void Print() {
        int level = 0;
        int index = 0;

        System.out.println("세그먼트 트리: ");
        while (index < seg_tree.length) {
            // 현재 트리의 높이
            int current_level = (int) Math.pow(2, level);

            // 트리의 높이가 바뀌지 않을 때 형제 노드 출력
            for (int a = 0; a < current_level; a++) {
                System.out.print(seg_tree[index] + " ");
                index++;
            }

            // 트리의 높이가 바뀔 때 줄 바꿈
            System.out.println();
            level++;
        }

    }
}
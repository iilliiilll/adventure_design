import java.io.*;

public class Project_14_2 {

    static final int ALPHA_SIZE = 26;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        Trie_node root = new Trie_node();

        while (true) {
            System.out.print("옵션을 입력하세요(1:삽입, 2:검색, 3:삭제: 4:출력, 5:최장 공통 전위 문자열 찾기, 6:종료): ");
            int option = Integer.parseInt(br.readLine());

            if (option == 1) {
                System.out.print("삽입할 단어를 입력하세요: ");
                String insert = br.readLine();
                char[] iarr = insert.toCharArray();
                root = Word_insert(root, iarr);
                System.out.println();
            } else if (option == 2) {
                System.out.print("검색할 단어를 입력하세요: ");
                String search = br.readLine();
                char[] sarr = search.toCharArray();
                if (Word_search(root, sarr) == 1) {
                    System.out.println("단어가 존재합니다.\n");
                } else {
                    System.out.println("단어가 존재하지 않습니다.\n");
                }
            } else if (option == 3) {
                System.out.print("삭제할 단어를 입력하세요: ");
                String delete = br.readLine();
                char[] darr = delete.toCharArray();
                root = Word_delete(root, darr, 0);
                System.out.println();
            } else if (option == 4) {
                System.out.println("현재 단어들을 출력합니다.");
                char[] word = new char[10];
                Show_tree(root, word, 0);
                System.out.println();
            } else if (option == 5) {
                System.out.println("최장 공통 전위 문자열을 찾습니다.");
                String prefix = Word_lcp(root);
                if (prefix.equals("")) {
                    System.out.println("최장 공통 전위 문자열은 없습니다.\n");
                } else {
                    System.out.println("최장 공통 전위 문자열: " + prefix + "\n");
                }
            } else if (option == 6) {
                System.out.println("종료합니다.\n");
                break;
            } else {
                System.out.println("다시 입력하세요\n");
            }
        }

    }

    // 노드 클래스
    static class Trie_node {
        private int word_end;

        Trie_node[] children = new Trie_node[ALPHA_SIZE];
    }

    // 노드 생성
    static Trie_node Get_node() {
        Trie_node node = new Trie_node();
        node.word_end = 0;

        // 자식 노드 초기화
        for (int i = 0; i < ALPHA_SIZE; i++) {
            node.children[i] = null;
        }

        return node;
    }

    // 단어 삽입
    static Trie_node Word_insert(Trie_node root, char[] word) {
        Trie_node curr = root;
        int i = 0;

        // 단어 순회
        while (i < word.length && 'a' <= word[i] && word[i] <= 'z') {
            int idx = word[i] - 'a';

            // 자식 노드가 없으면 생성
            if (curr.children[idx] == null) {
                curr.children[idx] = Get_node();
            }

            curr = curr.children[idx];
            i++;
        }

        // 단어의 끝 표시
        curr.word_end = 1;

        return root;
    }

    // 단어 검색
    static int Word_search(Trie_node root, char[] word) {
        if (root == null) {
            return 0;
        }

        Trie_node curr = root;
        int i = 0;

        // 단어 순회
        while (i < word.length && 'a' <= word[i] && word[i] <= 'z') {
            int idx = word[i] - 'a';
            curr = curr.children[idx];

            // 노드가 없으면 0 반환
            if (curr == null) {
                return 0;
            }

            i++;
        }

        return curr.word_end;
    }

    // 단어 삭제
    static Trie_node Word_delete(Trie_node curr, char[] word, int depth) {
        if (curr == null) {
            return null;
        }

        if (depth == word.length) {
            // 단어의 끝 도착
            if (curr.word_end == 1) {
                curr.word_end = 0;
            }

            // 자식 노드가 없는 경우
            if (nochildren(curr) == 1) {
                curr = null;
            }
            return curr;
        }

        int idx = word[depth] - 'a';
        curr.children[idx] = Word_delete(curr.children[idx], word, depth + 1);

        // 자식 노드가 없고, 단어의 끝이 아닐 때 노드 삭제
        if (nochildren(curr) == 1 && curr.word_end == 0) {
            curr = null;
        }

        return curr;
    }

    // 단어 삭제 2 - 자식 노드 여부
    static int nochildren(Trie_node curr) {
        for (int i = 0; i < ALPHA_SIZE; i++) {
            if (curr.children[i] != null) {
                return 0;
            }
        }

        return 1;
    }

    // 트리 출력
    static void Show_tree(Trie_node root, char[] word, int level) {
        // 단어의 끝이면 단어 배열 출력
        if (root.word_end == 1) {
            for (int i = 0; i < level; i++) {
                System.out.print(word[i]);
            }
            System.out.println();
        }

        // 자식 노드 순회
        for (int i = 0; i < ALPHA_SIZE; i++) {
            // 자식 노드에 문자 존재
            if (root.children[i] != null) {
                // 해당 문자를 단어 배열에 추가
                word[level] = (char) ('a' + i);
                Show_tree(root.children[i], word, level + 1);
            }
        }
    }

    // 자식 노드의 개수 구하기
    static int count_children(Trie_node curr, int[] idx) {
        int count = 0;
        for (int i = 0; i < ALPHA_SIZE; i++) {
            // 자식 노드에 문자 존재
            if (curr.children[i] != null) {
                count++;
                idx[0] = i;
            }
        }

        return count;
    }

    // 최장 공통 전위 문자열 찾기
    static String Word_lcp(Trie_node root) {
        // 문자를 추가하기 위해 StringBuilder 사용
        StringBuilder common_prefix = new StringBuilder();
        Trie_node curr = root;
        // 0으로 자동 초기화
        int[] idx = new int[1];

        // 자식 노드가 1개이고, 단어의 끝이 아닌 경우
        while (count_children(curr, idx) == 1 && curr.word_end == 0) {
            // 자식 노드로 이동
            curr = curr.children[idx[0]];
            common_prefix.append((char) ('a' + idx[0]));
        }

        return common_prefix.toString();
    }

}
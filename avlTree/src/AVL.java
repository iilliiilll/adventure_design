import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

// 노드 클래스
class Node {
    String word;
    ArrayList<Integer> lineNumbers; // 라인 번호를 저장하기 위한 ArrayList
    Node left, right;
    int height;

    Node(String word, int line) {
        this.word = word;
        this.lineNumbers = new ArrayList<>();
        this.lineNumbers.add(line);
        this.height = 1;
    }
}

// AVL 트리 클래스
class AVLTree {
    private Node root;

    public void insert(String iword, int line) {
        root = insert(root, iword, line);
    }

    // 단어 삽입
    private Node insert(Node current, String iword, int line) {
        // 트리가 비어있다면, 새로운 노드를 만들어서 반환
        if (current == null) {
            return new Node(iword, line);
        }

        int compare = iword.compareTo(current.word); // iword가 current.word보다 크다면 1 반환, 작다면 -1 반환, 같으면 0 반환

        if (compare < 0) { // 새로 삽입된 단어 < 현재 노드의 단어 -> 왼쪽 노드로 이동
            current.left = insert(current.left, iword, line);
        } else if (compare > 0) { // 새로 삽입된 단어 > 현재 노드의 단어 -> 오른쪽 노드로 이동
            current.right = insert(current.right, iword, line);
        } else { // 새로 삽입된 단어 == 현재 노드의 단어 -> 라인 번호 추가
            current.lineNumbers.add(line);
        }

        // 노드의 높이 재설정
        current.height = 1 + Math.max(getHeight(current.left), getHeight(current.right));

        return setBalance(current, iword);
    }

    // 균형 맞추기
    private Node setBalance(Node node, String bword) {
        int balance = getBf(node); // 균형 인수 구하기

        if (balance > 1) { // 왼쪽으로 치우친 편향트리
            if (bword.compareTo(node.left.word) < 0) { // 왼쪽 자식노드의 왼쪽에 삽입
                return rotateLeft(node);
            } else if (bword.compareTo(node.left.word) > 0) { // 왼쪽 자식노드의 오른쪽에 삽입
                node.left = rotateRight(node.left);
                return rotateLeft(node);
            }
        } else if (balance < -1) { // 오른쪽으로 치우친 편향트리
            if (bword.compareTo(node.right.word) > 0) { // 오른쪽 자식노드의 오른쪽에 삽입
                return rotateRight(node);
            } else if (bword.compareTo(node.right.word) < 0) { // 오른쪽 자식노드의 왼쪽에 삽입
                node.right = rotateLeft(node.right);
                return rotateRight(node);
            }
        }

        return node;
    }

    // 트리 높이 구하기
    private int getHeight(Node node) {
        return node == null ? 0 : node.height;
    }

    // 균형 인수 구하기
    private int getBf(Node node) {
        return node == null ? 0 : getHeight(node.left) - getHeight(node.right);
    }

    // RR 회전
    private Node rotateRight(Node currentNode) {
        Node newNode = currentNode.right;
        currentNode.right = newNode.left;
        newNode.left = currentNode;

        // 높이 재설정
        currentNode.height = 1 + Math.max(getHeight(currentNode.left), getHeight(currentNode.right));
        newNode.height = 1 + Math.max(getHeight(newNode.left), getHeight(newNode.right));

        return newNode;
    }

    // LL 회전
    private Node rotateLeft(Node currentNode) {
        Node newNode = currentNode.left;
        currentNode.left = newNode.right;
        newNode.right = currentNode;

        // 높이 재설정
        currentNode.height = 1 + Math.max(getHeight(currentNode.left), getHeight(currentNode.right));
        newNode.height = 1 + Math.max(getHeight(newNode.left), getHeight(newNode.right));

        return newNode;
    }

    public void searchWord(String sword) {
        searchWord(root, sword);
    }

    // 단어 검색
    private void searchWord(Node current, String sword) {
        if (current == null) {
            System.out.println("단어가 존재하지 않습니다.");
            return;
        }

        int compare = sword.compareTo(current.word);

        if (compare == 0) {
            StringBuilder sLineNumbers = new StringBuilder();
            for (int i = 0; i < current.lineNumbers.size(); i++) {
                sLineNumbers.append(current.lineNumbers.get(i));
                if (i < current.lineNumbers.size() - 1) { // 마지막 라인 번호가 아닐 때 콤마 추가
                    sLineNumbers.append(", ");
                }
            }
            System.out.println("단어의 라인 수: " + sLineNumbers.toString());
        } else if (compare < 0) {
            searchWord(current.left, sword);
        } else if (compare > 0) {
            searchWord(current.right, sword);
        }

    }

    public void printInOrder() {
        // 10칸의 공간, 왼쪽 정렬
        System.out.printf("%-10s\t%s%n", "단어", "라인 번호");

        printInOrder(root);
    }

    // 중위순회로 출력
    private void printInOrder(Node node) {
        if (node != null) {
            printInOrder(node.left);

            // 라인 번호 출력
            StringBuilder pLineNumbers = new StringBuilder();
            for (int i = 0; i < node.lineNumbers.size(); i++) {
                pLineNumbers.append(node.lineNumbers.get(i));
                if (i < node.lineNumbers.size() - 1) {
                    pLineNumbers.append(", ");
                }
            }

            System.out.printf("%-10s\t%s%n", node.word, pLineNumbers.toString());

            printInOrder(node.right);
        }
    }
}

public class AVL {
    private static final HashSet<String> prepositions = new HashSet<>(); // 전치사를 저장하는 HashSet

    public static void main(String[] args) throws IOException {
        // 시작 시간 기록
        long startTime = System.nanoTime();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        AVLTree tree = new AVLTree(); // AVLTree 객체 생성

        String filePath = "src/textFile.txt"; // 텍스트 파일 경로
        String prepositionFile = "src/preposition.txt"; // 전치사 목록 파일 경로

        getPrepositionsFile(prepositionFile);
        getTextFile(tree, filePath);

        tree.printInOrder(); // 단어와 라인 번호 출력

        // 종료 시간 기록
        long endTime = System.nanoTime();

        // 시간 출력
        long totalTime = (endTime - startTime)/ 1_000_000;
        System.out.println("\n실행 시간: " + totalTime + " ms\n");

        // 단어 검색
        while (true) {
            System.out.print("검색할 단어를 입력하세요(종료: e): ");
            String word = br.readLine();

            if (word.equals("e")) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            tree.searchWord(word);
        }
    }

    private static void getTextFile(AVLTree tree, String textPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(textPath))) {
            String line;
            int lineNumber = 1; // 첫 라인의 라인 번호는 1

            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, " ,.?!"); // 구분자를 기준으로 분리

                while (st.hasMoreTokens()) { // 토큰이 존재할 때까지 반복
                    // 소문자로 변환. 소문자가 아닌 문자는 모두 빈 문자열로 대체
                    String word = st.nextToken().toLowerCase().replaceAll("[^a-z]", "");

                    // 단어가 빈 문자열이 아니거나, 전치사가 아니라면 AVL 트리에 삽입
                    if (!word.isEmpty() && !prepositions.contains(word)) {
                        tree.insert(word, lineNumber);
                    }
                }

                // 라인이 바뀌면 라인 번호 1 증가
                lineNumber++;
            }
        } catch (IOException e) {
            System.out.println("파일이 존재하지 않거나, 경로가 잘못되었습니다.");
            e.printStackTrace();
        }
    }

    // 전치사 파일 읽기
    private static void getPrepositionsFile(String prepositionPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(prepositionPath))) {
            String line;

            // preposition.txt 파일 읽기 -> 앞뒤 공백 제거 -> 소문자 변환 -> HashSet에 추가
            while ((line = br.readLine()) != null) {
                prepositions.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.out.println("파일이 존재하지 않거나, 경로가 잘못되었습니다.");
            e.printStackTrace();
        }
    }
}

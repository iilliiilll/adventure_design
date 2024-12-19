import java.io.*;
import java.util.*;

public class Huffman {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // 테스트 파일 만들기
        MakeTestFile.main(args);

        System.out.println("Huffman Test Start\n");
        HuffmanTest.main(args);
        HuffmanGUI.main(args);
    }
}

class Node implements Comparable<Node> {
    char ch;
    int freq;
    Node left, right;

    Node(char ch, int freq) {
        this.ch = ch;
        this.freq = freq;
    }

    // 출현 빈도 수를 오름차순으로 정렬
    @Override
    public int compareTo(Node next) {
        return this.freq - next.freq;
    }
}

class HuffmanTree {
    // 허프만 트리 생성
    public Node buildTree(Map<Character, Integer> freqMap) {
        PriorityQueue<Node> pq = new PriorityQueue<>(); // 우선순위 큐 생성

        // freqMap 순회하여 노드를 큐에 삽입
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        // 우선순위 큐에 노드가 하나 남을 때까지 허프만 트리 생성
        while (pq.size() > 1) {
            Node leftChild = pq.poll(); // 큐의 맨 앞의 노드 꺼내기
            Node rightChild = pq.poll();

            Node parent = new Node('\0', leftChild.freq + rightChild.freq); // 노드 합치기
            parent.left = leftChild;
            parent.right = rightChild;

            pq.add(parent);
        }

        // 허프만 트리의 루트 노드 반환
        return pq.poll();
    }

    // 허프만 코드 생성
    public Map<Character, String> getHuffmanCode(Node root) {
        Map<Character, String> huffmanCodeMap = new HashMap<>();
        makeHuffmanCode(root, "", huffmanCodeMap);

        return huffmanCodeMap;
    }

    // 허프만 코드 생성
    private void makeHuffmanCode(Node node, String code, Map<Character, String> huffmanCodeMap) {
        if (node == null) {
            return;
        }

        // 문자가 저장된 노드일 때, 코드 삽입
        if (node.left == null && node.right == null) {
            huffmanCodeMap.put(node.ch, code);
        }

        makeHuffmanCode(node.left, code + "0", huffmanCodeMap);
        makeHuffmanCode(node.right, code + "1", huffmanCodeMap);
    }
}

class fileHandler {
    // 문자 빈도 수 계산
    public Map<Character, Integer> calFreq(String filePath) throws IOException {
        Map<Character, Integer> freqMap = new HashMap<>();

        // 파일 읽기
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int read;

            // 파일의 끝까지
            while ((read = br.read()) != -1) {
                char ch = (char) read;

                // 해당 문자의 빈도 수에 1 더하기
                // 처음 나오는 문자는 0으로 초기화
                freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);
            }
        }

        return freqMap;
    }

    // 파일 압축
    public void compressFile(String inputFile, String outputFile, Map<Character, String> huffmanCodeMap) throws IOException {
        StringBuilder compressedCode = new StringBuilder();

        // 문자의 허프만 코드를 추가
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            int reader;
            while ((reader = br.read()) != -1) {
                char ch = (char) reader;
                compressedCode.append(huffmanCodeMap.get(ch));
            }
        }

        // 모든 비트가 0으로 초기화
        BitSet bitSet = new BitSet(compressedCode.length());

        // 허프만 코드가 1일 경우, bitset 을 1로 설정
        for (int i = 0; i < compressedCode.length(); i++) {
            bitSet.set(i, compressedCode.charAt(i) == '1');
        }

        // 이진 파일로 저장
        try (ObjectOutputStream outputBinary = new ObjectOutputStream(new FileOutputStream(outputFile))) {
            outputBinary.writeObject(huffmanCodeMap); // 허프만 코드표 저장
            outputBinary.writeObject(bitSet); // 압축된 데이터 저장
            outputBinary.writeInt(compressedCode.length()); // 비트 길이 저장
        }
    }

    // 파일 복원
    public void decompressFile(String inputFile, String outputFile, Node root) throws IOException, ClassNotFoundException {
        StringBuilder decompressedCode = new StringBuilder(); // 복원된 텍스트 저장
        BitSet bitSet;
        int compressedLen;

        // 1. 파일에서 허프만 코드표와 비트 데이터 읽기
        try (ObjectInputStream inputBinary = new ObjectInputStream(new FileInputStream(inputFile))) {
            @SuppressWarnings("unchecked")
            Map<Character, String> huffmanCodeMap = (Map<Character, String>) inputBinary.readObject(); // 허프만 코드표 읽기
            bitSet = (BitSet) inputBinary.readObject();
            compressedLen = inputBinary.readInt(); // 압축된 비트 길이 읽기
        }

        // 2. 비트 데이터를 허프만 트리로 복원
        Node current = root; // 탐색을 루트에서 시작
        for (int i = 0; i < compressedLen; i++) {
            current = bitSet.get(i) ? current.right : current.left; // 비트를 따라 트리 탐색

            if (current.left == null && current.right == null) { // 리프 노드 도달
                decompressedCode.append(current.ch); // 복원된 문자 추가
                current = root; // 루트로 되돌아감
            }
        }

        // 3. 복원된 텍스트를 파일로 저장
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            bw.write(decompressedCode.toString());
        }
    }

}
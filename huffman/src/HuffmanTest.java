import java.io.IOException;
import java.util.Map;

public class HuffmanTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        long startTime = System.currentTimeMillis();

        fileHandler fileHandler = new fileHandler();
        HuffmanTree huffmanTree = new HuffmanTree();

        Map<Character, Integer> freqMap = fileHandler.calFreq("resources/input.txt"); // 문자:빈도 Map 얻기
        Node root = huffmanTree.buildTree(freqMap); // 문자:빈도 Map 으로 허프만 트리 생성
        Map<Character, String> huffmanCodeMap = huffmanTree.getHuffmanCode(root); // 문자:코드 Map 얻기

        // 허프만 코드, 허프만 코드 맵
        printHuffmanCode(freqMap, huffmanCodeMap);

        // 파일 압축 및 해제 테스트
        printTime(fileHandler, root, huffmanCodeMap);

        // 압축률과 크기 테스트
        printCompression(freqMap, huffmanCodeMap);

        // 엔트로피 및 이론적 크기 계산
        printEntropy(freqMap);

        long endTime = System.currentTimeMillis();
        System.out.printf("\n총 수행 시간: %d ms%n", endTime - startTime);
    }

    private static void printHuffmanCode(Map<Character, Integer> freqMap, Map<Character, String> huffmanCodeMap) {
        System.out.println("----- TEST 1 -----");
        System.out.println("문자\t빈도\t허프만 코드");

        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            char ch = entry.getKey();
            int freq = entry.getValue();
            String code = huffmanCodeMap.get(ch);

            System.out.printf("%c\t%d\t%s%n", ch, freq, code);
        }

        System.out.println();
    }

    private static void printTime(fileHandler fileHandler, Node root, Map<Character, String> huffmanCodeMap) throws IOException, ClassNotFoundException {
        long compressStart = System.currentTimeMillis();
        fileHandler.compressFile("resources/input.txt", "resources/compressed.bin", huffmanCodeMap);
        long compressEnd = System.currentTimeMillis();

        long decompressStart = System.currentTimeMillis();
        fileHandler.decompressFile("resources/compressed.bin", "resources/output.txt", root);
        long decompressEnd = System.currentTimeMillis();

        System.out.println("----- TEST 2 -----");
        System.out.printf("압축 수행 시간: %d ms%n", compressEnd - compressStart);
        System.out.printf("압축 해제 수행 시간: %d ms%n", decompressEnd - decompressStart);
    }

    private static void printCompression(Map<Character, Integer> freqMap, Map<Character, String> huffmanCodeMap) {
        int beforeSize = 0;
        int afterSize = 0;

        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            char ch = entry.getKey();
            int freq = entry.getValue();

            beforeSize += freq * 8; // 원래 문자의 비트 수
            afterSize += freq * huffmanCodeMap.get(ch).length(); // 허프만 코드의 비트 수
        }

        double compressionRate = 1.0 - ((double) afterSize / beforeSize);

        System.out.println("\n----- TEST 3 -----");
        System.out.printf("압축 전 전체 크기 : %d bits%n", beforeSize);
        System.out.printf("압축 후 전체 크기 : %d bits%n", afterSize);
        System.out.printf("허프만 트리 압축률: %.2f%%%n", compressionRate * 100);
    }

    private static void printEntropy(Map<Character, Integer> freqMap) {
        int totalChars = freqMap.values().stream().mapToInt(Integer::intValue).sum();
        double entropy = 0.0;

        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            int freq = entry.getValue();
            double probability = (double) freq / totalChars; // Pz 계산
            entropy += -freq * (Math.log(probability) / Math.log(2)); // -Pz * log2(Pz)
        }

        System.out.println("\n----- TEST 4 -----");
        System.out.printf("총 문자 개수: %d%n", totalChars);
        System.out.printf("자료의 전체 엔트로피: %.6f bits%n", entropy);

        double beforeSize = totalChars * 8;
        double entropyCompressRate = 1.0 - (entropy / beforeSize);
        System.out.printf("엔트로피 압축률: %.2f%%%n", entropyCompressRate * 100);
    }
}
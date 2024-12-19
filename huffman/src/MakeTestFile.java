import java.io.*;

public class MakeTestFile {
    public static void main(String[] args) throws IOException {
        char[] charSet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        generateFile("resources/input.txt", 1, charSet);
    }

    public static void generateFile(String filePath, int sizeKB, char[] charSet) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            int totalChars = sizeKB * 1024;

            for (int i = 0; i < totalChars; i++) {
                for (int j = 0; j < charSet.length; j++) {
                    writer.write(charSet[0]);
                }
            }
        }
    }
}
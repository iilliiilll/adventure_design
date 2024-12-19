import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class HuffmanGUI extends JFrame {
    private final TreePanel treePanel;
    private PriorityQueue<TreeNode> pq;
    private Stack<TreeNode> previousStates;
    private Stack<List<TreeNode>> previousQueueStates;
    private TreeNode currentRoot;

    public HuffmanGUI() {
        setTitle("Huffman Tree");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 트리 패널 추가
        treePanel = new TreePanel();
        add(treePanel, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();

        JButton previousButton = new JButton("Previous Step");
        previousButton.addActionListener(e -> previousStep());
        buttonPanel.add(previousButton);

        JButton nextButton = new JButton("Next Step");
        nextButton.addActionListener(e -> nextStep());
        buttonPanel.add(nextButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 초기 데이터 준비
        prepareData();
    }

    private void prepareData() {
        try {
            Map<Character, Integer> freqMap = calculateFrequency("resources/input.txt");
            pq = new PriorityQueue<>((a, b) -> a.freq - b.freq);
            previousStates = new Stack<>();
            previousQueueStates = new Stack<>();

            for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
                pq.add(new TreeNode(entry.getKey(), entry.getValue()));
            }

            treePanel.setQueue(new ArrayList<>(pq));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<Character, Integer> calculateFrequency(String filePath) throws IOException {
        Map<Character, Integer> freqMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int read;
            while ((read = br.read()) != -1) {
                char ch = (char) read;
                freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);
            }
        }
        return freqMap;
    }

    private void nextStep() {
        if (pq.size() > 1) {
            // 현재 상태 저장
            previousStates.push(currentRoot);
            previousQueueStates.push(new ArrayList<>(pq));

            TreeNode left = pq.poll();
            TreeNode right = pq.poll();

            TreeNode parent = new TreeNode('\0', left.freq + right.freq);
            parent.left = left;
            parent.right = right;

            pq.add(parent);
            currentRoot = parent;

            treePanel.setQueue(new ArrayList<>(pq));
            treePanel.setRoot(currentRoot);
            treePanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Huffman Tree Complete!");
        }
    }

    private void previousStep() {
        if (!previousStates.isEmpty() && !previousQueueStates.isEmpty()) {
            currentRoot = previousStates.pop();
            List<TreeNode> previousQueue = previousQueueStates.pop();

            pq.clear();
            pq.addAll(previousQueue);

            treePanel.setQueue(new ArrayList<>(pq));
            treePanel.setRoot(currentRoot);
            treePanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "No more previous states!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HuffmanGUI frame = new HuffmanGUI();
            frame.setVisible(true);
        });
    }
}

class TreePanel extends JPanel {
    private List<TreeNode> queue;
    private TreeNode root;

    public TreePanel() {
        setBackground(Color.WHITE);
    }

    public void setQueue(List<TreeNode> queue) {
        this.queue = queue;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 큐 시각화
        if (queue != null) {
            int x = 50;
            int y = 20;

            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, getWidth() - 100, 80);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, getWidth() - 100, 80);

            int nodeX = x + 20;
            for (TreeNode node : queue) {
                g.setColor(Color.WHITE);
                g.fillRect(nodeX, y + 20, 60, 40);
                g.setColor(Color.BLACK);
                g.drawRect(nodeX, y + 20, 60, 40);
                String text = (node.ch == '\0') ? String.valueOf(node.freq) : node.ch + " (" + node.freq + ")";
                g.drawString(text, nodeX + 5, y + 45);
                nodeX += 70;
            }
        }

        // 트리 만들기
        if (root != null) {
            drawTree(g, root, getWidth() / 2, 150, getWidth() / 4);
        }
    }

    private void drawTree(Graphics g, TreeNode node, int x, int y, int xOffset) {
        if (node == null) return;

        g.setColor(Color.WHITE);
        g.fillOval(x - 15, y - 15, 30, 30);
        g.setColor(Color.BLACK);
        g.drawOval(x - 15, y - 15, 30, 30);
        String text = (node.ch == '\0') ? String.valueOf(node.freq) : node.ch + " (" + node.freq + ")";
        g.drawString(text, x - 10, y + 5);

        if (node.left != null) {
            g.drawLine(x, y, x - xOffset, y + 80);
            drawTree(g, node.left, x - xOffset, y + 80, xOffset / 2);
        }

        if (node.right != null) {
            g.drawLine(x, y, x + xOffset, y + 80);
            drawTree(g, node.right, x + xOffset, y + 80, xOffset / 2);
        }
    }
}

class TreeNode {
    char ch;
    int freq;
    TreeNode left, right;

    public TreeNode(char ch, int freq) {
        this.ch = ch;
        this.freq = freq;
    }
}

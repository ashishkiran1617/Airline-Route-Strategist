
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

public class AirlineRouteStrategist extends JFrame {

    private static final int MAX_LEVELS = 4;

    // Theme colors
    static final Color BG = new Color(10, 17, 32);
    static final Color PANEL = new Color(20, 31, 52);
    static final Color PANEL2 = new Color(27, 42, 67);
    static final Color CYAN = new Color(40, 210, 235);
    static final Color BLUE = new Color(65, 130, 255);
    static final Color GREEN = new Color(40, 210, 145);
    static final Color WHITE = new Color(235, 243, 255);
    static final Color MUTED = new Color(145, 163, 190);
    static final Color GOLD = new Color(255, 190, 75);

    // Binary Tree Node
    static class AirportNode {
        String name;
        AirportNode left, right;
        int level;

        AirportNode(String name, int level) {
            this.name = name;
            this.level = level;
        }
    }

    private AirportNode root;

    // BFS Queue
    private final Queue<AirportNode> expansionQueue =
            new LinkedList<>();

    private final Set<String> airportNames =
            new HashSet<>();

    private int score = 0;
    private int totalAirports = 0;
    private int completedHubs = 0;
    private int flightNumber = 1001;

    // Animation variables
    private AirportNode flyingParent;
    private int flightLeg = 0;
    private double flightProgress = 0;
    private boolean animating = false;
    private javax.swing.Timer flightTimer;

    // GUI components
    private JTextField rootInput;
    private JTextField destination1;
    private JTextField destination2;

    private JLabel airportValue;
    private JLabel hubValue;
    private JLabel levelValue;
    private JLabel scoreValue;

    private JLabel statusLabel;
    private JLabel nextHubLabel;
    private JLabel flightInfoLabel;

    private TreeCanvas treeCanvas;

    public AirlineRouteStrategist() {

        setTitle("Airline Route Strategist | Premium Edition");
        setSize(1300, 850);
        setMinimumSize(new Dimension(1100, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createDashboard(), BorderLayout.CENTER);

        setVisible(true);
    }

    // SIDEBAR

    private JPanel createSidebar() {

        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(210, 0));
        side.setBackground(new Color(14, 24, 43));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(25, 17, 20, 17));

        JLabel logo = new JLabel("✈ AIRLINE");
        logo.setFont(new Font("Arial", Font.BOLD, 23));
        logo.setForeground(CYAN);

        JLabel sub = new JLabel("ROUTE STRATEGIST");
        sub.setFont(new Font("Arial", Font.BOLD, 11));
        sub.setForeground(MUTED);

        side.add(logo);
        side.add(Box.createVerticalStrut(5));
        side.add(sub);
        side.add(Box.createVerticalStrut(45));

        side.add(sideItem("◈   Dashboard", true));
        side.add(Box.createVerticalStrut(17));
        side.add(sideItem("✈   Route Network", false));
        side.add(Box.createVerticalStrut(17));
        side.add(sideItem("◎   Airport Hubs", false));
        side.add(Box.createVerticalStrut(17));
        side.add(sideItem("▤   Tree Analysis", false));

        side.add(Box.createVerticalGlue());

        JLabel version = new JLabel("DSA PROJECT | JAVA");
        version.setForeground(MUTED);
        version.setFont(new Font("Arial", Font.BOLD, 10));
        side.add(version);

        return side;
    }

    private JLabel sideItem(String text, boolean selected) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(selected ? CYAN : MUTED);
        label.setBorder(new EmptyBorder(12, 8, 12, 5));

        if (selected) {
            label.setOpaque(true);
            label.setBackground(PANEL2);
        }

        return label;
    }

    // MAIN DASHBOARD

    private JPanel createDashboard() {

        JPanel main = new JPanel(new BorderLayout(18, 18));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Airline Command Center");
        title.setForeground(WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 27));

        JLabel description = new JLabel(
                "Design, expand and analyse your flight network."
        );
        description.setForeground(MUTED);

        heading.add(title);
        heading.add(Box.createVerticalStrut(7));
        heading.add(description);

        JLabel live = new JLabel("● SYSTEM ONLINE");
        live.setForeground(GREEN);
        live.setFont(new Font("Arial", Font.BOLD, 11));

        header.add(heading, BorderLayout.WEST);
        header.add(live, BorderLayout.EAST);

        // Statistics
        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 0));
        stats.setOpaque(false);

        airportValue = new JLabel("0");
        hubValue = new JLabel("0");
        levelValue = new JLabel("0");
        scoreValue = new JLabel("0");

        stats.add(statCard("TOTAL AIRPORTS", airportValue, CYAN));
        stats.add(statCard("COMPLETED HUBS", hubValue, GREEN));
        stats.add(statCard("NETWORK LEVELS", levelValue, BLUE));
        stats.add(statCard("GAME SCORE", scoreValue, GOLD));

        // Network visualization panel
        JPanel networkPanel = new JPanel(new BorderLayout(8, 8));
        networkPanel.setBackground(PANEL);
        networkPanel.setBorder(new EmptyBorder(17, 17, 17, 17));

        JLabel networkTitle = new JLabel(
                "FLIGHT NETWORK VISUALIZATION"
        );
        networkTitle.setForeground(WHITE);
        networkTitle.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel networkSubtitle = new JLabel(
                "Animated aircraft and live flight routes"
        );
        networkSubtitle.setForeground(MUTED);

        JPanel networkHeading = new JPanel();
        networkHeading.setOpaque(false);
        networkHeading.setLayout(new BoxLayout(
                networkHeading, BoxLayout.Y_AXIS
        ));

        networkHeading.add(networkTitle);
        networkHeading.add(Box.createVerticalStrut(5));
        networkHeading.add(networkSubtitle);

        treeCanvas = new TreeCanvas();
        treeCanvas.setPreferredSize(new Dimension(950, 580));

        JScrollPane scroll = new JScrollPane(treeCanvas);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(PANEL);

        networkPanel.add(networkHeading, BorderLayout.NORTH);
        networkPanel.add(scroll, BorderLayout.CENTER);

        // Game controls
        JPanel controls = new JPanel();
        controls.setPreferredSize(new Dimension(275, 0));
        controls.setBackground(PANEL);
        controls.setBorder(new EmptyBorder(18, 16, 18, 16));
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JLabel controlTitle = new JLabel("GAME CONTROLS");
        controlTitle.setForeground(CYAN);
        controlTitle.setFont(new Font("Arial", Font.BOLD, 15));

        controls.add(controlTitle);
        controls.add(Box.createVerticalStrut(22));

        controls.add(fieldLabel("MAIN AIRPORT"));
        rootInput = createField("Enter main airport");
        controls.add(rootInput);

        JButton start = createButton("START NEW NETWORK", BLUE);
        start.addActionListener(e -> startGame());

        controls.add(Box.createVerticalStrut(12));
        controls.add(start);

        controls.add(Box.createVerticalStrut(25));

        controls.add(fieldLabel("DESTINATION 01"));
        destination1 = createField("First destination");
        controls.add(destination1);

        controls.add(Box.createVerticalStrut(12));

        controls.add(fieldLabel("DESTINATION 02"));
        destination2 = createField("Second destination");
        controls.add(destination2);

        JButton expand = createButton(
                "EXPAND NEXT HUB  +",
                new Color(25, 155, 130)
        );

        expand.addActionListener(e -> expandNetwork());

        controls.add(Box.createVerticalStrut(17));
        controls.add(expand);

        controls.add(Box.createVerticalStrut(22));

        nextHubLabel = new JLabel("Next hub: Not started");
        nextHubLabel.setForeground(CYAN);
        nextHubLabel.setFont(new Font("Arial", Font.BOLD, 12));
        controls.add(nextHubLabel);

        controls.add(Box.createVerticalStrut(20));

        // LIVE FLIGHT INFORMATION
        JLabel flightTitle = new JLabel("LIVE FLIGHT INFORMATION");
        flightTitle.setForeground(GOLD);
        flightTitle.setFont(new Font("Arial", Font.BOLD, 11));

        flightInfoLabel = new JLabel(
                "<html>No active flights<br>Waiting for network...</html>"
        );
        flightInfoLabel.setForeground(WHITE);
        flightInfoLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        JPanel flightPanel = new JPanel();
        flightPanel.setBackground(PANEL2);
        flightPanel.setLayout(new BoxLayout(
                flightPanel, BoxLayout.Y_AXIS
        ));
        flightPanel.setBorder(new EmptyBorder(12, 10, 12, 10));

        flightPanel.add(flightTitle);
        flightPanel.add(Box.createVerticalStrut(12));
        flightPanel.add(flightInfoLabel);

        flightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controls.add(flightPanel);

        controls.add(Box.createVerticalStrut(18));

        JButton analysis = createButton(
                "ANALYSE NETWORK",
                new Color(105, 85, 180)
        );
        analysis.addActionListener(e -> showAnalysis());
        controls.add(analysis);

        controls.add(Box.createVerticalStrut(10));

        JButton reset = createButton(
                "RESET GAME",
                new Color(170, 65, 75)
        );
        reset.addActionListener(e -> resetGame());
        controls.add(reset);

        controls.add(Box.createVerticalGlue());

        JLabel rule = new JLabel(
                "<html><div style='width:225px'>" +
                        "RULE: Every expanded hub connects to exactly " +
                        "two destinations. Expansion follows BFS order " +
                        "and stops at four levels." +
                        "</div></html>"
        );

        rule.setForeground(MUTED);
        rule.setFont(new Font("Arial", Font.PLAIN, 10));
        controls.add(rule);

        JPanel content = new JPanel(new BorderLayout(15, 0));
        content.setOpaque(false);
        content.add(networkPanel, BorderLayout.CENTER);
        content.add(controls, BorderLayout.EAST);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(PANEL2);
        footer.setBorder(new EmptyBorder(12, 15, 12, 15));

        statusLabel = new JLabel("STATUS: Ready to build network");
        statusLabel.setForeground(GREEN);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 11));

        JLabel footerText = new JLabel("AIRLINE ROUTE STRATEGIST");
        footerText.setForeground(MUTED);
        footerText.setFont(new Font("Arial", Font.BOLD, 10));

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(footerText, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout(0, 17));
        center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH);
        center.add(content, BorderLayout.CENTER);

        main.add(header, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);
        main.add(footer, BorderLayout.SOUTH);

        return main;
    }

    // UI HELPERS

    private JPanel statCard(
            String name, JLabel value, Color accent
    ) {

        JPanel card = new JPanel();
        card.setBackground(PANEL);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 14, 15, 10));

        JLabel heading = new JLabel(name);
        heading.setForeground(MUTED);
        heading.setFont(new Font("Arial", Font.BOLD, 10));

        value.setForeground(accent);
        value.setFont(new Font("Arial", Font.BOLD, 27));

        card.add(heading);
        card.add(Box.createVerticalStrut(12));
        card.add(value);

        return card;
    }

    private JLabel fieldLabel(String text) {

        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("Arial", Font.BOLD, 10));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JTextField createField(String hint) {

        JTextField field = new JTextField();
        field.setToolTipText(hint);
        field.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 38
        ));

        field.setBackground(PANEL2);
        field.setForeground(WHITE);
        field.setCaretColor(CYAN);
        field.setFont(new Font("Arial", Font.PLAIN, 12));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(55, 75, 105)
                ),
                new EmptyBorder(8, 10, 8, 10)
        ));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        return field;
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 42
        ));

        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        return button;
    }

    // START NETWORK

    private void startGame() {

        String name = rootInput.getText().trim().toUpperCase();

        if (name.isEmpty()) {
            showMessage("Enter the main airport name.");
            return;
        }

        stopFlightAnimation();
        resetData();

        root = new AirportNode(name, 1);

        expansionQueue.add(root);
        airportNames.add(name);

        totalAirports = 1;

        flightInfoLabel.setText(
                "<html>Network started<br>Waiting for first flight...</html>"
        );

        updateDashboard();
        refreshTree();

        statusLabel.setText(
                "STATUS: Main airport " + name + " created"
        );
    }

    // EXPAND NETWORK USING BFS

    private void expandNetwork() {

        if (root == null) {
            showMessage("Start a new network first.");
            return;
        }

        if (animating) {
            showMessage("Please wait until the current flights finish.");
            return;
        }

        AirportNode parent = expansionQueue.peek();

        if (parent == null) {
            showMessage("Network completed!");
            return;
        }

        String first = destination1.getText().trim().toUpperCase();
        String second = destination2.getText().trim().toUpperCase();

        if (first.isEmpty() || second.isEmpty()) {
            showMessage("Enter both destinations.");
            return;
        }

        if (first.equals(second)) {
            showMessage("Destinations must be different.");
            return;
        }

        if (airportNames.contains(first) ||
                airportNames.contains(second)) {
            showMessage("Airport name already exists.");
            return;
        }

        if (parent.level >= MAX_LEVELS) {
            showMessage("Maximum network depth reached.");
            return;
        }

        int childLevel = parent.level + 1;

        // Create two child airports
        parent.left = new AirportNode(first, childLevel);
        parent.right = new AirportNode(second, childLevel);

        expansionQueue.remove();

        if (childLevel < MAX_LEVELS) {
            expansionQueue.add(parent.left);
            expansionQueue.add(parent.right);
        }

        airportNames.add(first);
        airportNames.add(second);

        totalAirports += 2;
        completedHubs++;
        score += 100;

        destination1.setText("");
        destination2.setText("");

        // Start aircraft animation
        startFlightAnimation(parent);

        updateDashboard();
        refreshTree();

        statusLabel.setText(
                "STATUS: Flights departing from " + parent.name
        );
    }

    // AIRCRAFT ANIMATION

    private void startFlightAnimation(AirportNode parent) {

        flyingParent = parent;
        flightLeg = 0;
        flightProgress = 0;
        animating = true;

        int currentFlight = flightNumber++;

        String first = parent.left.name;
        String second = parent.right.name;

        flightInfoLabel.setText(
                "<html><b>FLIGHT AI-" + currentFlight +
                        "</b><br>From: " + parent.name +
                        "<br>To: " + first +
                        "<br>Status: TAKEOFF</html>"
        );

        flightTimer = new javax.swing.Timer(30, e -> {

            flightProgress += 0.025;

            if (flightProgress >= 1.0) {

                flightProgress = 1.0;

                if (flightLeg == 0) {

                    flightLeg = 1;
                    flightProgress = 0;

                    flightInfoLabel.setText(
                            "<html><b>FLIGHT AI-" +
                                    currentFlight +
                                    "</b><br>From: " + parent.name +
                                    "<br>To: " + second +
                                    "<br>Status: IN FLIGHT</html>"
                    );

                } else {

                    flightTimer.stop();
                    animating = false;
                    flyingParent = null;

                    flightInfoLabel.setText(
                            "<html><b>FLIGHT AI-" +
                                    currentFlight +
                                    "</b><br>Route: " +
                                    parent.name + " to " +
                                    first + " and " + second +
                                    "<br>Status: LANDED SUCCESSFULLY</html>"
                    );

                    if (expansionQueue.isEmpty()) {

                        score += 500;
                        updateDashboard();

                        statusLabel.setText(
                                "STATUS: NETWORK COMPLETED - PERFECT TREE"
                        );

                        showMessage(
                                "NETWORK COMPLETED!\n\n" +
                                        "Total Airports: " + totalAirports +
                                        "\nCompleted Hubs: " + completedHubs +
                                        "\nNetwork Levels: " + getHeight(root) +
                                        "\nFinal Score: " + score
                        );

                    } else {

                        statusLabel.setText(
                                "STATUS: Flights landed successfully"
                        );
                    }
                }
            }

            treeCanvas.repaint();
        });

        flightTimer.start();
    }

    private void stopFlightAnimation() {

        if (flightTimer != null) {
            flightTimer.stop();
        }

        flyingParent = null;
        animating = false;
        flightProgress = 0;
        flightLeg = 0;
    }

    // RESET

    private void resetData() {

        root = null;
        expansionQueue.clear();
        airportNames.clear();

        score = 0;
        totalAirports = 0;
        completedHubs = 0;
        flightNumber = 1001;
    }

    private void resetGame() {

        stopFlightAnimation();
        resetData();

        rootInput.setText("");
        destination1.setText("");
        destination2.setText("");

        flightInfoLabel.setText(
                "<html>No active flights<br>Waiting for network...</html>"
        );

        statusLabel.setText("STATUS: Game reset successfully");

        updateDashboard();
        refreshTree();
    }

    // DASHBOARD UPDATE

    private void updateDashboard() {

        airportValue.setText(String.valueOf(totalAirports));
        hubValue.setText(String.valueOf(completedHubs));
        levelValue.setText(
                root == null ? "0" : String.valueOf(getHeight(root))
        );
        scoreValue.setText(String.valueOf(score));

        if (expansionQueue.isEmpty()) {
            nextHubLabel.setText("Next hub: None");
        } else {
            nextHubLabel.setText(
                    "Next hub: " + expansionQueue.peek().name
            );
        }
    }

    private void refreshTree() {
        treeCanvas.revalidate();
        treeCanvas.repaint();
    }

    private void showMessage(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Airline Route Strategist",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // TREE ANALYSIS

    private int getHeight(AirportNode node) {

        if (node == null) return 0;

        return 1 + Math.max(
                getHeight(node.left),
                getHeight(node.right)
        );
    }

    private boolean isFull(AirportNode node) {

        if (node == null) return true;

        if (node.left == null && node.right == null) {
            return true;
        }

        if (node.left != null && node.right != null) {
            return isFull(node.left) && isFull(node.right);
        }

        return false;
    }

    private boolean isComplete(AirportNode node) {

        if (node == null) return true;

        Queue<AirportNode> queue = new LinkedList<>();
        queue.add(node);

        boolean gapFound = false;

        while (!queue.isEmpty()) {

            AirportNode current = queue.remove();

            if (current.left != null) {

                if (gapFound) return false;

                queue.add(current.left);

            } else {
                gapFound = true;
            }

            if (current.right != null) {

                if (gapFound) return false;

                queue.add(current.right);

            } else {
                gapFound = true;
            }
        }

        return true;
    }

    private boolean checkPerfect(
            AirportNode node, int depth, int[] leafDepth
    ) {

        if (node == null) return true;

        if (node.left == null && node.right == null) {

            if (leafDepth[0] == -1) {
                leafDepth[0] = depth;
                return true;
            }

            return leafDepth[0] == depth;
        }

        if (node.left == null || node.right == null) {
            return false;
        }

        return checkPerfect(
                node.left, depth + 1, leafDepth
        ) && checkPerfect(
                node.right, depth + 1, leafDepth
        );
    }

    private int countLeaves(AirportNode node) {

        if (node == null) return 0;

        if (node.left == null && node.right == null) {
            return 1;
        }

        return countLeaves(node.left) +
                countLeaves(node.right);
    }

    private int countInternal(AirportNode node) {

        if (node == null ||
                (node.left == null && node.right == null)) {
            return 0;
        }

        return 1 + countInternal(node.left)
                + countInternal(node.right);
    }

    private void showAnalysis() {

        if (root == null) {
            showMessage("Create a network first.");
            return;
        }

        int[] leafDepth = {-1};

        boolean full = isFull(root);
        boolean complete = isComplete(root);
        boolean perfect = checkPerfect(root, 0, leafDepth);

        String result =
                "AIRLINE NETWORK ANALYSIS\n\n" +
                        "Total Airports: " + totalAirports + "\n" +
                        "Internal Hubs: " + countInternal(root) + "\n" +
                        "Leaf Airports: " + countLeaves(root) + "\n" +
                        "Tree Height: " + getHeight(root) + "\n" +
                        "Game Score: " + score + "\n\n" +
                        "FULL BINARY TREE: " + (full ? "YES" : "NO") +
                        "\nCOMPLETE BINARY TREE: " +
                        (complete ? "YES" : "NO") +
                        "\nPERFECT BINARY TREE: " +
                        (perfect ? "YES" : "NO") +
                        "\n\nPending Hubs: " + expansionQueue.size();

        showMessage(result);
    }

    // TREE VISUALIZATION AND AIRPLANE DRAWING

    class TreeCanvas extends JPanel {

        TreeCanvas() {
            setBackground(PANEL);
        }

        private int nodeX(int index, int level) {

            int width = 950;

            return (int) (
                    (index + 0.5) * width /
                            Math.pow(2, level)
            );
        }

        private int nodeY(int level) {
            return 65 + level * 125;
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (root == null) {

                g.setColor(MUTED);
                g.setFont(new Font("Arial", Font.BOLD, 16));

                g.drawString(
                        "START A NETWORK TO SEE FLIGHT ROUTES",
                        80, 130
                );

                g.dispose();
                return;
            }

            drawEdges(g, root, 0, 0);
            drawNodes(g, root, 0, 0);

            if (animating && flyingParent != null) {
                drawFlyingAircraft(g);
            }

            g.dispose();
        }

        // Draw routes
        private void drawEdges(
                Graphics2D g,
                AirportNode node,
                int level,
                int index
        ) {

            if (node == null) return;

            int x = nodeX(index, level);
            int y = nodeY(level);

            if (node.left != null) {

                int childX = nodeX(index * 2, level + 1);
                int childY = nodeY(level + 1);

                g.setColor(new Color(50, 120, 190));
                g.setStroke(new BasicStroke(2.5f));

                g.drawLine(x, y + 28, childX, childY - 28);

                drawEdges(
                        g, node.left,
                        level + 1, index * 2
                );
            }

            if (node.right != null) {

                int childX = nodeX(index * 2 + 1, level + 1);
                int childY = nodeY(level + 1);

                g.setColor(new Color(50, 120, 190));
                g.setStroke(new BasicStroke(2.5f));

                g.drawLine(x, y + 28, childX, childY - 28);

                drawEdges(
                        g, node.right,
                        level + 1, index * 2 + 1
                );
            }
        }

        // Draw airports
        private void drawNodes(
                Graphics2D g,
                AirportNode node,
                int level,
                int index
        ) {

            if (node == null) return;

            int x = nodeX(index, level);
            int y = nodeY(level);
            int radius = 28;

            boolean completed =
                    node.left != null && node.right != null;

            Color nodeColor = completed ? GREEN : BLUE;

            // Glow effect
            g.setColor(new Color(
                    nodeColor.getRed(),
                    nodeColor.getGreen(),
                    nodeColor.getBlue(),
                    40
            ));

            g.fillOval(
                    x - radius - 10,
                    y - radius - 10,
                    (radius + 10) * 2,
                    (radius + 10) * 2
            );

            // Airport circle
            g.setColor(nodeColor);
            g.fillOval(
                    x - radius, y - radius,
                    radius * 2, radius * 2
            );

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 10));

            FontMetrics fm = g.getFontMetrics();

            g.drawString(
                    node.name,
                    x - fm.stringWidth(node.name) / 2,
                    y + 4
            );

            g.setColor(MUTED);
            g.setFont(new Font("Arial", Font.PLAIN, 9));

            g.drawString(
                    completed ? "CONNECTED" : "AIRPORT",
                    x - 25, y + 43
            );

            drawNodes(
                    g, node.left,
                    level + 1, index * 2
            );

            drawNodes(
                    g, node.right,
                    level + 1, index * 2 + 1
            );
        }

        // Animated airplane travels from parent to child
        private void drawFlyingAircraft(Graphics2D g) {

            int parentX = nodeX(
                    findIndex(root, flyingParent, 0, 0),
                    flyingParent.level - 1
            );

            int parentY = nodeY(flyingParent.level - 1);

            AirportNode destination =
                    flightLeg == 0 ?
                            flyingParent.left : flyingParent.right;

            int childIndex = findIndex(
                    root, destination, 0, 0
            );

            int childX = nodeX(
                    childIndex, destination.level - 1
            );

            int childY = nodeY(destination.level - 1);

            double startX = parentX;
            double startY = parentY + 28;

            double endX = childX;
            double endY = childY - 28;

            double x = startX +
                    (endX - startX) * flightProgress;

            double y = startY +
                    (endY - startY) * flightProgress;

            // Highlight active route
            g.setColor(GOLD);
            g.setStroke(new BasicStroke(4f));

            g.drawLine(
                    parentX, parentY + 28,
                    childX, childY - 28
            );

            // Aircraft shadow
            g.setColor(new Color(255, 190, 75, 60));
            g.fillOval(
                    (int) x - 17,
                    (int) y - 17,
                    34, 34
            );

            // Aircraft icon
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 25));

            g.drawString(
                    "✈",
                    (int) x - 13,
                    (int) y + 9
            );

            // Arrival marker
            g.setColor(GOLD);
            g.fillOval(
                    childX - 5,
                    childY - 5,
                    10, 10
            );

            // Flight trail
            g.setColor(new Color(255, 190, 75, 150));
            g.setStroke(new BasicStroke(
                    2f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            int trailX = (int) (x - (endX - startX) * 0.12);
            int trailY = (int) (y - (endY - startY) * 0.12);

            g.drawLine(
                    trailX, trailY,
                    (int) x - 10, (int) y
            );
        }

        // Find a node's position in the tree
        private int findIndex(
                AirportNode current,
                AirportNode target,
                int index,
                int level
        ) {

            if (current == null) return -1;

            if (current == target) return index;

            int leftResult = findIndex(
                    current.left, target, index * 2, level + 1
            );

            if (leftResult != -1) return leftResult;

            return findIndex(
                    current.right, target,
                    index * 2 + 1, level + 1
            );
        }
    }

    // MAIN METHOD

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                AirlineRouteStrategist::new
        );
    }
}

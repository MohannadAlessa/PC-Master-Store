import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

public class StoreGUI extends JFrame {
    private ArrayList<Product> products;
    private Cart cart;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryBox;
    private float hue = 0.0f;
    private JPanel mainPanel;

    public StoreGUI() {
        initData();
        cart = new Cart();

        setTitle("PC Master Store - Dark Edition");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(18, 18, 18));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        JLabel headerLabel = new JLabel("PC Master Race Store", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(Color.CYAN);
        headerLabel.setPreferredSize(new Dimension(0, 60));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        String[] columns = {"ID", "اسم القطعة", "القسم", "السعر الأساسي"};
        tableModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(tableModel);
        productTable.setRowHeight(35);
        productTable.setBackground(new Color(30, 30, 30));
        productTable.setForeground(Color.WHITE);
        productTable.setFont(new Font("Arial", Font.PLAIN, 15));
        productTable.getTableHeader().setBackground(new Color(40, 40, 40));
        productTable.getTableHeader().setForeground(Color.CYAN);

        refreshTable("الكل");

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.getViewport().setBackground(new Color(18, 18, 18));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(7, 1, 10, 15));
        sidePanel.setBackground(new Color(18, 18, 18));
        sidePanel.setPreferredSize(new Dimension(200, 0));

        JButton btnAdd = createStyledButton("إضافة للسلة");

        JLabel lblCat = new JLabel("تصفح الأقسام:");
        lblCat.setForeground(Color.WHITE);
        lblCat.setHorizontalAlignment(SwingConstants.CENTER);

        String[] cats = {"الكل", "GPU", "CPU", "RAM", "SSD", "HDD", "M.2"};
        categoryBox = new JComboBox<>(cats);
        categoryBox.setBackground(new Color(40, 40, 40));
        categoryBox.setForeground(Color.WHITE);

        JButton btnDiscount = createStyledButton("إدخال كود الخصم");
        JButton btnViewCart = createStyledButton("عرض السلة");
        JButton btnExit = createStyledButton("خروج");

        sidePanel.add(btnAdd);
        sidePanel.add(lblCat);
        sidePanel.add(categoryBox);
        sidePanel.add(new JLabel(""));
        sidePanel.add(btnDiscount);
        sidePanel.add(btnViewCart);
        sidePanel.add(btnExit);

        mainPanel.add(sidePanel, BorderLayout.EAST);

        categoryBox.addActionListener(e -> {
            String selected = (String) categoryBox.getSelectedItem();
            refreshTable(selected);
        });

        btnAdd.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                for (Product p : products) {
                    if (p.getId() == id) {
                        cart.addProduct(p);
                        JOptionPane.showMessageDialog(this, "تمت إضافة " + p.getName() + " للسلة!", "نجاح", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "يرجى اختيار قطعة من الجدول أولاً.", "تنبيه", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDiscount.addActionListener(e -> {
            String code = JOptionPane.showInputDialog(this, "أدخل كود الخصم (جرب PC10):", "كود الخصم", JOptionPane.QUESTION_MESSAGE);
            if (code != null && code.equalsIgnoreCase("PC10")) {
                for (Product p : products) {
                    p.applyDiscount(10);
                }
                refreshTable((String) categoryBox.getSelectedItem());
                JOptionPane.showMessageDialog(this, "تم تفعيل الكود وخصم 10% من جميع القطع!", "مبروك", JOptionPane.INFORMATION_MESSAGE);
            } else if (code != null && !code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "كود الخصم غير صحيح.", "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnViewCart.addActionListener(e -> openCartViewer());

        btnExit.addActionListener(e -> System.exit(0));

        startRGBEffect();
        setVisible(true);
    }

    private void openCartViewer() {
        ArrayList<Product> cartItems = cart.getProducts();
        ArrayList<Integer> cartQuantities = cart.getQuantities();

        JDialog cartDialog = new JDialog(this, "تفاصيل سلة المشتريات", true);
        cartDialog.setSize(800, 550);
        cartDialog.setLocationRelativeTo(this);

        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
        dialogPanel.setBackground(new Color(25, 25, 25));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        cartDialog.setContentPane(dialogPanel);

        String[] cartColumns = {"ID", "اسم القطعة", "الفئة", "الكمية", "السعر الإجمالي"};
        DefaultTableModel cartTableModel = new DefaultTableModel(cartColumns, 0);
        for (int i = 0; i < cartItems.size(); i++) {
            Product p = cartItems.get(i);
            int qty = cartQuantities.get(i);
            cartTableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), qty, "$" + (p.getPrice() * qty)});
        }

        JTable cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(30);
        cartTable.setBackground(new Color(40, 40, 40));
        cartTable.setForeground(Color.WHITE);
        cartTable.getTableHeader().setBackground(new Color(50, 50, 50));
        cartTable.getTableHeader().setForeground(Color.CYAN);
        dialogPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(25, 25, 25));

        JLabel totalLabel = new JLabel("الإجمالي الحالي: $" + cart.calculateTotal(), JLabel.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(Color.GREEN);
        bottomPanel.add(totalLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(25, 25, 25));

        JButton btnRemove = createStyledButton("حذف القطعة");
        JButton btnCheck = createStyledButton("فحص التوافق");
        JButton btnExport = createStyledButton("طباعة الفاتورة");
        JButton btnClear = createStyledButton("تفريغ السلة");
        JButton btnClose = createStyledButton("إغلاق");

        buttonPanel.add(btnRemove);
        buttonPanel.add(btnCheck);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnClose);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        dialogPanel.add(bottomPanel, BorderLayout.SOUTH);

        btnRemove.addActionListener(re -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) cartTableModel.getValueAt(selectedRow, 0);
                for (int i = 0; i < cartItems.size(); i++) {
                    if (cartItems.get(i).getId() == id) {
                        cart.removeProduct(cartItems.get(i));
                        refreshCartTable(cartTableModel);
                        totalLabel.setText("الإجمالي الحالي: $" + cart.calculateTotal());
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(cartDialog, "يرجى اختيار قطعة لحذفها.", "تنبيه", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCheck.addActionListener(ce -> {
            String status = cart.checkCompatibility();
            if (status.contains("تحذير")) {
                JOptionPane.showMessageDialog(cartDialog, status, "فحص التوافق", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(cartDialog, "جميع القطع متوافقة وسليمة!", "فحص التوافق", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnExport.addActionListener(ee -> {
            if (cart.getProducts().isEmpty()) {
                JOptionPane.showMessageDialog(cartDialog, "السلة فارغة، لا يوجد شيء لطباعته.", "تنبيه", JOptionPane.WARNING_MESSAGE);
            } else {
                cart.exportInvoice();
                JOptionPane.showMessageDialog(cartDialog, "تم طباعة الفاتورة بنجاح في ملف invoice.txt بجانب المشروع!", "نجاح", JOptionPane.INFORMATION_MESSAGE);
                cart.clearCart();
                cartDialog.dispose();
            }
        });

        btnClear.addActionListener(ce -> {
            cart.clearCart();
            refreshCartTable(cartTableModel);
            totalLabel.setText("الإجمالي الحالي: $0.0");
            JOptionPane.showMessageDialog(cartDialog, "تم تفريغ السلة بنجاح.");
        });

        btnClose.addActionListener(ce -> cartDialog.dispose());

        cartDialog.setVisible(true);
    }

    private void refreshCartTable(DefaultTableModel model) {
        model.setRowCount(0);
        ArrayList<Product> items = cart.getProducts();
        ArrayList<Integer> qts = cart.getQuantities();
        for (int i = 0; i < items.size(); i++) {
            Product p = items.get(i);
            int qty = qts.get(i);
            model.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), qty, "$" + (p.getPrice() * qty)});
        }
    }

    private void refreshTable(String filter) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            if (filter.equals("الكل") || p.getCategory().equals(filter)) {
                Object[] row = {p.getId(), p.getName(), p.getCategory(), "$" + p.getPrice()};
                tableModel.addRow(row);
            }
        }
    }

    private JButton createStyledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(40, 40, 40));
        b.setFocusPainted(false);
        return b;
    }

    private void startRGBEffect() {
        Timer timer = new Timer(50, e -> {
            hue += 0.005f;
            if (hue > 1.0f) hue = 0.0f;
            Color rgb = Color.getHSBColor(hue, 1.0f, 1.0f);
            mainPanel.setBorder(BorderFactory.createLineBorder(rgb, 4));
        });
        timer.start();
    }

    private void initData() {
        products = new ArrayList<>();
        int id = 1;

        products.add(new CPU(id++, "Intel Core i9-14900K", 589));
        products.add(new CPU(id++, "AMD Ryzen 9 7950X3D", 699));
        products.add(new CPU(id++, "Intel Core i7-14700K", 399));
        products.add(new CPU(id++, "AMD Ryzen 7 7800X3D", 399));
        products.add(new CPU(id++, "Intel Core i5-14600K", 299));
        products.add(new CPU(id++, "AMD Ryzen 5 7600X", 229));
        products.add(new CPU(id++, "Intel Core i9-13900K", 549));
        products.add(new CPU(id++, "AMD Ryzen 9 5950X", 499));
        products.add(new CPU(id++, "Intel Core i7-13700K", 369));
        products.add(new CPU(id++, "AMD Ryzen 7 5800X3D", 329));
        products.add(new CPU(id++, "Intel Core i5-13400F", 199));
        products.add(new CPU(id++, "AMD Ryzen 5 5600X", 159));
        products.add(new CPU(id++, "Intel Core i3-14100F", 119));
        products.add(new CPU(id++, "AMD Ryzen 3 4100", 99));
        products.add(new CPU(id++, "Intel Core i9-12900K", 349));
        products.add(new CPU(id++, "AMD Ryzen 9 3900X", 299));
        products.add(new CPU(id++, "Intel Core i7-12700K", 249));
        products.add(new CPU(id++, "AMD Ryzen 7 3700X", 199));
        products.add(new CPU(id++, "Intel Core i5-12400F", 149));
        products.add(new CPU(id++, "AMD Ryzen 5 3600", 119));

        products.add(new GPU(id++, "NVIDIA GeForce RTX 4090", 1999));
        products.add(new GPU(id++, "AMD Radeon RX 7900 XTX", 999));
        products.add(new GPU(id++, "NVIDIA GeForce RTX 4080 Super", 999));
        products.add(new GPU(id++, "AMD Radeon RX 7900 XT", 899));
        products.add(new GPU(id++, "NVIDIA GeForce RTX 4070 Ti Super", 799));
        products.add(new GPU(id++, "AMD Radeon RX 7800 XT", 499));
        products.add(new GPU(id++, "NVIDIA GeForce RTX 4070 Super", 599));
        products.add(new GPU(id++, "AMD Radeon RX 7700 XT", 449));
        products.add(new GPU(id++, "NVIDIA GeForce RTX 4060 Ti", 399));
        products.add(new GPU(id++, "AMD Radeon RX 7600 XT", 329));
        products.add(new GPU(id++, "NVIDIA GeForce RTX 3090 Ti", 1099));
        products.add(new GPU(id++, "AMD Radeon RX 6950 XT", 649));
        products.add(new GPU(id++, "NVIDIA GeForce RTX 3080", 699));
        products.add(new GPU(id++, "AMD Radeon RX 6800 XT", 499));
        products.add(new GPU(id++, "NVIDIA GeForce RTX 3070", 499));
        products.add(new GPU(id++, "AMD Radeon RX 6700 XT", 349));
        products.add(new GPU(id++, "NVIDIA GeForce RTX 3060 Ti", 399));
        products.add(new GPU(id++, "AMD Radeon RX 6600", 199));
        products.add(new GPU(id++, "NVIDIA GeForce GTX 1660 Super", 169));
        products.add(new GPU(id++, "AMD Radeon RX 580", 129));

        products.add(new RAM(id++, "Corsair Vengeance RGB DDR5", 149));
        products.add(new RAM(id++, "G.Skill Trident Z5 RGB DDR5", 159));
        products.add(new RAM(id++, "Kingston FURY Beast DDR5", 129));
        products.add(new RAM(id++, "TeamGroup T-Force Delta RGB DDR5", 139));
        products.add(new RAM(id++, "Crucial Pro DDR5", 119));
        products.add(new RAM(id++, "Corsair Dominator Titanium DDR5", 199));
        products.add(new RAM(id++, "G.Skill Ripjaws S5 DDR5", 129));
        products.add(new RAM(id++, "Patriot Viper Venom DDR5", 119));
        products.add(new RAM(id++, "XPG Lancer RGB DDR5", 139));
        products.add(new RAM(id++, "PNY XLR8 Gaming DDR5", 129));
        products.add(new RAM(id++, "Corsair Vengeance LPX DDR4", 69));
        products.add(new RAM(id++, "G.Skill Trident Z Neo DDR4", 89));
        products.add(new RAM(id++, "Kingston FURY Renegade DDR4", 79));
        products.add(new RAM(id++, "TeamGroup T-Force Vulcan Z DDR4", 59));
        products.add(new RAM(id++, "Crucial Ballistix DDR4", 69));
        products.add(new RAM(id++, "G.Skill Ripjaws V DDR4", 59));
        products.add(new RAM(id++, "Patriot Viper Steel DDR4", 65));
        products.add(new RAM(id++, "XPG Spectrix D50 DDR4", 75));
        products.add(new RAM(id++, "HyperX Fury DDR4", 69));
        products.add(new RAM(id++, "Silicon Power Value Gaming DDR4", 49));

        products.add(new Storage(id++, "Samsung 990 Pro 2TB", 189, "M.2"));
        products.add(new Storage(id++, "WD Black SN850X 2TB", 179, "M.2"));
        products.add(new Storage(id++, "Crucial T700 2TB", 299, "M.2"));
        products.add(new Storage(id++, "Kingston KC3000 2TB", 169, "M.2"));
        products.add(new Storage(id++, "Seagate FireCuda 530 2TB", 179, "M.2"));
        products.add(new Storage(id++, "Corsair MP600 Pro 2TB", 189, "M.2"));
        products.add(new Storage(id++, "SK hynix Platinum P41 2TB", 169, "M.2"));
        products.add(new Storage(id++, "Sabrent Rocket 4 Plus 2TB", 199, "M.2"));
        products.add(new Storage(id++, "MSI Spatium M480 2TB", 159, "M.2"));
        products.add(new Storage(id++, "TeamGroup Cardea Zero 2TB", 149, "M.2"));
        products.add(new Storage(id++, "Samsung 870 EVO 1TB", 89, "SSD"));
        products.add(new Storage(id++, "Crucial MX500 1TB", 79, "SSD"));
        products.add(new Storage(id++, "Kingston A400 1TB", 69, "SSD"));
        products.add(new Storage(id++, "WD Blue SA510 1TB", 75, "SSD"));
        products.add(new Storage(id++, "SanDisk Ultra 3D 1TB", 79, "SSD"));
        products.add(new Storage(id++, "WD Black 4TB", 129, "HDD"));
        products.add(new Storage(id++, "Seagate Barracuda 4TB", 89, "HDD"));
        products.add(new Storage(id++, "Toshiba X300 4TB", 99, "HDD"));
        products.add(new Storage(id++, "WD Blue 4TB", 85, "HDD"));
        products.add(new Storage(id++, "Seagate IronWolf 4TB", 109, "HDD"));
    }
}
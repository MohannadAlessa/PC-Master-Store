import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Cart {
    private ArrayList<Product> products;
    private ArrayList<Integer> quantities;

    public Cart() {
        products = new ArrayList<>();
        quantities = new ArrayList<>();
    }

    public void addProduct(Product product) {
        int index = -1;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            quantities.set(index, quantities.get(index) + 1);
        } else {
            products.add(product);
            quantities.add(1);
        }
    }

    public void removeProduct(Product product) {
        int index = -1;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            int currentQty = quantities.get(index);
            if (currentQty > 1) {
                quantities.set(index, currentQty - 1);
            } else {
                products.remove(index);
                quantities.remove(index);
            }
        }
    }

    public void displayCart() {
        for (int i = 0; i < products.size(); i++) {
            System.out.println(products.get(i).getName() + " x" + quantities.get(i));
        }
    }

    public double calculateTotal() {
        double total = 0;
        for (int i = 0; i < products.size(); i++) {
            total += products.get(i).getPrice() * quantities.get(i);
        }
        return total;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public ArrayList<Integer> getQuantities() {
        return quantities;
    }

    public void clearCart() {
        products.clear();
        quantities.clear();
    }

    public String checkCompatibility() {
        boolean hasIntelCPU = false;
        boolean hasAmdCPU = false;
        boolean hasIntelMoboforAmd = false;
        boolean hasAmdMoboforIntel = false;

        for (Product p : products) {
            String name = p.getName().toLowerCase();
            if (p.getCategory().equals("CPU")) {
                if (name.contains("intel")) {
                    hasIntelCPU = true;
                } else if (name.contains("amd") || name.contains("ryzen")) {
                    hasAmdCPU = true;
                }
            }
        }

        if (hasIntelCPU && hasAmdCPU) {
            return "تحذير: لقد أضفت معالج Intel ومعالج AMD في نفس السلة!";
        }
        return "متوافق";
    }

    public void exportInvoice() {
        try {
            FileWriter writer = new FileWriter("invoice.txt");
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            writer.write("========================================\n");
            writer.write("           PC MASTER RACE STORE          \n");
            writer.write("========================================\n");
            writer.write("التاريخ والوقت: " + now.format(formatter) + "\n");
            writer.write("----------------------------------------\n");

            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                int qty = quantities.get(i);
                double itemTotal = p.getPrice() * qty;
                writer.write("- " + p.getName() + " (" + p.getCategory() + ")\n");
                writer.write("  الكمية: " + qty + " | سعر الحبة: $" + p.getPrice() + " | الإجمالي: $" + itemTotal + "\n");
            }

            writer.write("----------------------------------------\n");
            writer.write("الإجمالي النهائي للطلب: $" + calculateTotal() + "\n");
            writer.write("========================================\n");
            writer.write("       شكراً لتسوقك معنا! نراك قريباً.     \n");
            writer.write("========================================\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("حدث خطأ أثناء طباعة الفاتورة.");
        }
    }
}
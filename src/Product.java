interface Discountable {
    void applyDiscount(double percentage);
}

public class Product implements Discountable {
    private int id;
    private String name;
    private double price;
    private String category;

    public Product(int id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public void setPrice(double price) { this.price = price; }

    public void displayInfo() {
        System.out.println("ID: " + id + ", Product: " + name + ", Category: " + category + ", Price: $" + price);
    }

    @Override
    public void applyDiscount(double percentage) {
        this.price = this.price - (this.price * (percentage / 100));
    }
}

class GPU extends Product {
    public GPU(int id, String name, double price) { super(id, name, price, "GPU"); }
}

class CPU extends Product {
    public CPU(int id, String name, double price) { super(id, name, price, "CPU"); }
}

class RAM extends Product {
    public RAM(int id, String name, double price) { super(id, name, price, "RAM"); }
}

class Storage extends Product {
    public Storage(int id, String name, double price, String type) { super(id, name, price, type); }
}
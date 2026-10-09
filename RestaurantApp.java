package com.reva.restaurant;

import com.reva.restaurant.model.*;
import com.reva.restaurant.service.*;
import java.util.Scanner;

/**
 * REVA University - B.Sc. (BSTCs) Semester V - Java Programming
 * Mini Project: Restaurant Management System
 * Student: Rushda Fathima | SRN: R24SA032
 */
public class RestaurantApp {

    // final constant -> a class-level (static) value that never changes
    private static final double DEFAULT_TAX_PERCENT = 5.0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        MenuService menuService = new MenuService(20);
        OrderService orderService = new OrderService(50);

        // ---- Seed the menu (varied data types: int, String, double, enum) ----
        menuService.addItem(new MenuItem("Paneer Butter Masala", 220.0, FoodCategory.MAIN_COURSE));
        menuService.addItem(new MenuItem("Veg Biryani", 180.0, FoodCategory.MAIN_COURSE));
        menuService.addItem(new MenuItem("Spring Rolls", 120.0, FoodCategory.STARTER));
        menuService.addItem(new MenuItem("Chicken Tikka", 250.0, FoodCategory.STARTER));
        menuService.addItem(new MenuItem("Gulab Jamun", 60.0, FoodCategory.DESSERT));
        menuService.addItem(new MenuItem("Cold Coffee", 90.0, FoodCategory.BEVERAGE));

        // ---- Seed staff (array of Employee objects) ----
        Employee[] staff = new Employee[3];
        staff[0] = new Employee("Ravi Kumar", "9900000001", 15000.0, Designation.MANAGER);
        staff[1] = new Employee("Sunita Rao", "9900000002", 12000.0, Designation.CHEF);
        staff[2] = new Employee("Arjun Das", "9900000003", 9000.0, Designation.WAITER);

        // ---- Demonstrate method overriding with dynamic binding ----
        // A Person reference holding different child-class objects; getRole() resolves
        // at runtime to whichever subclass actually created the object.
        Person[] people = new Person[2];
        people[0] = staff[0];
        Person tempCustomerRef = new Customer("Guest", "0000000000", 0);
        people[1] = tempCustomerRef;

        System.out.println("=== Dynamic Binding Demo (Person reference -> child object) ===");
        for (Person p : people) {
            System.out.printf("%-12s -> Role: %s%n", p.getName(), p.getRole());
        }

        Customer currentCustomer = null;
        Order currentOrder = null;
        boolean running = true;

        while (running) { // main application loop (while)
            System.out.println("\n===== RESTAURANT MANAGEMENT SYSTEM =====");
            System.out.println("1. Register Customer");
            System.out.println("2. View Menu");
            System.out.println("3. Add Item to Current Order");
            System.out.println("4. View Current Order Bill");
            System.out.println("5. Finalize Order (Pay Bill)");
            System.out.println("6. View Staff Salary Details");
            System.out.println("7. View All Finalized Orders & Revenue");
            System.out.println("8. Add New Menu Item (Admin)");
            System.out.println("9. Sort Menu Alphabetically");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next();
                continue; // jump statement: skip rest of loop body
            }
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: {
                    System.out.print("Enter customer name: ");
                    String name = sc.nextLine().trim();

                    // do-while loop: keeps asking until a non-empty phone number is entered
                    String phone;
                    do {
                        System.out.print("Enter phone number: ");
                        phone = sc.nextLine().trim();
                    } while (phone.isEmpty());

                    currentCustomer = new Customer(name, phone, 60);
                    currentOrder = new Order(currentCustomer, (int) (Math.random() * 10) + 1);
                    System.out.println("Registered -> " + currentCustomer);
                    break;
                }
                case 2:
                    menuService.displayMenu();
                    break;
                case 3:
                    if (currentCustomer == null) {
                        System.out.println("Please register a customer first (Option 1).");
                        break;
                    }
                    System.out.print("Enter item name to search & add: ");
                    String itemQuery = sc.nextLine();
                    MenuItem found = menuService.searchByName(itemQuery);
                    if (found == null) {
                        System.out.println("Item not found.");
                    } else if (currentOrder.addItem(found)) {
                        System.out.println("Added: " + found.getItemName());
                    } else {
                        System.out.println("Order is full.");
                    }
                    break;
                case 4:
                    if (currentOrder == null) {
                        System.out.println("No active order.");
                        break;
                    }
                    System.out.println(currentOrder);
                    break;
                case 5:
                    if (currentOrder == null) {
                        System.out.println("No active order to finalize.");
                        break;
                    }
                    double finalAmount = currentOrder.calculatePayment(DEFAULT_TAX_PERCENT); // overloaded method
                    System.out.printf("Final Payable Amount (incl. %.1f%% tax): Rs.%.2f%n", DEFAULT_TAX_PERCENT, finalAmount);
                    orderService.placeOrder(currentOrder);
                    currentOrder = null;
                    break;
                case 6:
                    System.out.println("\n--- Staff Salary Details ---");
                    for (Employee e : staff) {
                        Payable p = e; // interface reference: Payable p pointing to an Employee
                        System.out.printf("%-12s %-10s Salary: Rs.%.2f%n", e.getName(), e.getRole(), p.calculatePayment());
                    }
                    break;
                case 7:
                    orderService.displayAllOrders();
                    System.out.printf("TOTAL REVENUE: Rs.%.2f%n", orderService.getTotalRevenue());
                    break;
                case 8: {
                    System.out.print("Enter as Name,Price,Category(STARTER/MAIN_COURSE/DESSERT/BEVERAGE): ");
                    String line = sc.nextLine();
                    String[] parts = line.split(","); // String.split()
                    if (parts.length != 3) {
                        System.out.println("Invalid format.");
                        break;
                    }
                    try {
                        String newName = parts[0].trim();
                        double newPrice = Double.parseDouble(parts[1].trim());
                        FoodCategory cat = FoodCategory.valueOf(parts[2].trim().toUpperCase());
                        menuService.addItem(new MenuItem(newName, newPrice, cat));
                        System.out.println("Menu item added successfully.");
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Invalid price or category entered.");
                    }
                    break;
                }
                case 9:
                    menuService.sortMenuAlphabetically();
                    System.out.println("Menu sorted alphabetically.");
                    menuService.displayMenu();
                    break;
                case 0:
                    running = false;
                    System.out.println("Total Persons ever created (static counter): " + Person.getTotalPersons());
                    System.out.println("Thank you for using the Restaurant Management System. Goodbye!");
                    sc.close();
                    return; // jump statement: exits main() immediately
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
}

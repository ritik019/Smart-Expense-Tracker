import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ExpenseTracker {
    private static final String FILE_NAME = "expenses.txt";
    private static final Map<String, String> categoryMap = new HashMap<>();
    private static List<Expense> expenses = new ArrayList<>();

    static {
        categoryMap.put("food", "Food");
        categoryMap.put("transport", "Transport");
        categoryMap.put("entertainment", "Entertainment");
        categoryMap.put("shopping", "Shopping");
        categoryMap.put("other", "Other");
    }

    public static void main(String[] args) {
        loadExpenses();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Add Expense\n2. View Expenses\n3. Monthly Summary\n4. Query by Date Range\n5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    addExpense(scanner);
                    break;
                case 2:
                    viewExpenses();
                    break;
                case 3:
                    viewMonthlySummary();
                    break;
                case 4:
                    queryByDateRange(scanner);
                    break;
                case 5:
                    saveExpenses();
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addExpense(Scanner scanner) {
        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter category (food, transport, entertainment, shopping, other): ");
        String category = scanner.nextLine().toLowerCase();

        LocalDate date = LocalDate.now();
        Expense expense = new Expense(description, amount, categoryMap.getOrDefault(category, "Other"), date);
        expenses.add(expense);
        System.out.println("Expense added successfully.");
    }

    private static void viewExpenses() {
        System.out.println("----- All Expenses -----");
        for (Expense expense : expenses) {
            System.out.println(expense);
        }
    }

    private static void viewMonthlySummary() {
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            categoryTotals.put(expense.getCategory(), categoryTotals.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
        }

        System.out.println("----- Monthly Expense Summary -----");
        for (String category : categoryMap.values()) {
            System.out.printf("%s: $%.2f%n", category, categoryTotals.getOrDefault(category, 0.0));
        }
    }

    private static void queryByDateRange(Scanner scanner) {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter end date (YYYY-MM-DD): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        System.out.println("----- Expenses from " + startDate + " to " + endDate + " -----");
        for (Expense expense : expenses) {
            if (!expense.getDate().isBefore(startDate) && !expense.getDate().isAfter(endDate)) {
                System.out.println(expense);
            }
        }
    }

    private static void loadExpenses() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String description = data[0];
                double amount = Double.parseDouble(data[1]);
                String category = data[2];
                LocalDate date = LocalDate.parse(data[3]);
                expenses.add(new Expense(description, amount, category, date));
            }
        } catch (IOException e) {
            System.out.println("No existing expenses found. Starting fresh.");
        }
    }

    private static void saveExpenses() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Expense expense : expenses) {
                writer.write(String.format("%s,%.2f,%s,%s%n", expense.getDescription(), expense.getAmount(), expense.getCategory(), expense.getDate()));
            }
            System.out.println("Expenses saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving expenses.");
        }
    }
}

package com.todoapp;
import java.sql.*;
import java.util.Scanner;

public class ToDoApp {
    // JDBC URL for SQLite database
    private static final String DB_URL = "jdbc:sqlite:todo.db";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Initialize database and create table if not exists
        initializeDatabase();
        
        boolean exit = false;
        while (!exit) {
            displayMenu();
            int choice = getMenuChoice();
            
            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    viewAllTasks();
                    break;
                case 3:
                    updateTask();
                    break;
                case 4:
                    deleteTask();
                    break;
                case 5:
                    exit = true;
                    System.out.println("Exiting application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
    
    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Create tasks table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS tasks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title VARCHAR(255) NOT NULL," +
                    "description TEXT," +
                    "status VARCHAR(20) DEFAULT 'Pending'" +
                    ")";
            stmt.execute(createTableSQL);
            
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void displayMenu() {
        System.out.println("\n===== TO-DO LIST APPLICATION =====");
        System.out.println("1. Add a new task");
        System.out.println("2. View all tasks");
        System.out.println("3. Update an existing task");
        System.out.println("4. Delete a task");
        System.out.println("5. Exit");
        System.out.print("Enter your choice (1-5): ");
    }
    
    private static int getMenuChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;  // Invalid choice
        }
    }
    
    private static void addTask() {
        System.out.println("\n----- Add a New Task -----");
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        
        String sql = "INSERT INTO tasks (title, description) VALUES (?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Task added successfully!");
            } else {
                System.out.println("Failed to add task.");
            }
            
        } catch (SQLException e) {
            System.out.println("Error adding task: " + e.getMessage());
        }
    }
    
    private static void viewAllTasks() {
        System.out.println("\n----- All Tasks -----");
        
        String sql = "SELECT id, title, description, status FROM tasks";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            boolean hasRecords = false;
            
            System.out.println("--------------------------------------------------------");
            System.out.printf("| %-4s | %-20s | %-20s | %-10s |\n", "ID", "TITLE", "DESCRIPTION", "STATUS");
            System.out.println("--------------------------------------------------------");
            
            while (rs.next()) {
                hasRecords = true;
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String status = rs.getString("status");
                
                // Truncate long text for better formatting
                if (title.length() > 20) {
                    title = title.substring(0, 17) + "...";
                }
                if (description.length() > 20) {
                    description = description.substring(0, 17) + "...";
                }
                
                System.out.printf("| %-4d | %-20s | %-20s | %-10s |\n", id, title, description, status);
            }
            
            System.out.println("--------------------------------------------------------");
            
            if (!hasRecords) {
                System.out.println("No tasks found.");
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving tasks: " + e.getMessage());
        }
    }
    
    private static void updateTask() {
        System.out.println("\n----- Update a Task -----");
        
        // First display all tasks so user can see the IDs
        viewAllTasks();
        
        System.out.print("Enter the task ID to update: ");
        int taskId;
        try {
            taskId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
            return;
        }
        
        // Check if task exists
        if (!taskExists(taskId)) {
            System.out.println("Task not found with ID: " + taskId);
            return;
        }
        
        System.out.println("What would you like to update?");
        System.out.println("1. Title");
        System.out.println("2. Description");
        System.out.println("3. Status");
        System.out.print("Enter your choice (1-3): ");
        
        int updateChoice;
        try {
            updateChoice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Update canceled.");
            return;
        }
        
        String columnToUpdate;
        String newValue;
        
        switch (updateChoice) {
            case 1:
                columnToUpdate = "title";
                System.out.print("Enter new title: ");
                newValue = scanner.nextLine();
                break;
            case 2:
                columnToUpdate = "description";
                System.out.print("Enter new description: ");
                newValue = scanner.nextLine();
                break;
            case 3:
                columnToUpdate = "status";
                System.out.println("Select new status:");
                System.out.println("1. Pending");
                System.out.println("2. In Progress");
                System.out.println("3. Completed");
                System.out.print("Enter your choice (1-3): ");
                try {
                    int statusChoice = Integer.parseInt(scanner.nextLine());
                    switch (statusChoice) {
                        case 1:
                            newValue = "Pending";
                            break;
                        case 2:
                            newValue = "In Progress";
                            break;
                        case 3:
                            newValue = "Completed";
                            break;
                        default:
                            System.out.println("Invalid choice. Using 'Pending' as default.");
                            newValue = "Pending";
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid choice. Using 'Pending' as default.");
                    newValue = "Pending";
                }
                break;
            default:
                System.out.println("Invalid choice. Update canceled.");
                return;
        }
        
        String sql = "UPDATE tasks SET " + columnToUpdate + " = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newValue);
            pstmt.setInt(2, taskId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Task updated successfully!");
            } else {
                System.out.println("Failed to update task.");
            }
            
        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
        }
    }
    
    private static void deleteTask() {
        System.out.println("\n----- Delete a Task -----");
        
        // First display all tasks so user can see the IDs
        viewAllTasks();
        
        System.out.print("Enter the task ID to delete: ");
        int taskId;
        try {
            taskId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
            return;
        }
        
        // Check if task exists
        if (!taskExists(taskId)) {
            System.out.println("Task not found with ID: " + taskId);
            return;
        }
        
        String sql = "DELETE FROM tasks WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, taskId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Task deleted successfully!");
            } else {
                System.out.println("Failed to delete task.");
            }
            
        } catch (SQLException e) {
            System.out.println("Error deleting task: " + e.getMessage());
        }
    }
    
    private static boolean taskExists(int taskId) {
        String sql = "SELECT COUNT(*) AS count FROM tasks WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, taskId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error checking task existence: " + e.getMessage());
        }
        
        return false;
    }
}
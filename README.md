A console-based To-Do List application with CRUD operations implemented in Java using JDBC for SQLite database connectivity.

Features:
Create: Add new tasks with title and description
Read: View all tasks in a formatted table
Update: Modify title, description, or status of existing tasks
Delete: Remove tasks from the database
User-friendly console interface: Simple menu-driven interaction

Technology Stack:
Java
JDBC (Java Database Connectivity)
SQLite Database
Console-based User Interface

Prerequisites:
Java Development Kit (JDK) 8 or higher
SQLite JDBC Driver

Compilation:
Compile the Java source code USING: javac -cp "lib/sqlite-jdbc-3.49.1.0.jar" -d bin src/com/todoapp/ToDoApp.java

Running the Application On Windows:
java -cp "bin;lib/sqlite-jdbc-3.40.1.0.jar" com.todoapp.ToDoApp

Project Structure:

todo-list-app/
├── src/
│   └─ com/
│       └── todoapp/
│           └── ToDoApp.java
├── lib/
│   └── sqlite-jdbc-3.49.1.0.jar
├── bin/
│   └── com/
│       └── todoapp/
│           └── ToDoApp.class
├── todo.db
└── README.md

# DeWallet


## Introduction

DeWallet is a frontend-friendly application designed to help you manage your finances effortlessly. With DeWallet, you can track your income and expenses conveniently, categorize transactions, and gain valuable insights into your spending habits.

### Key Features:
- **User Management**: Easily create and manage user accounts to track finances separately for each individual.
- **Category Management**: Organize your transactions into customizable categories for better financial analysis.
- **Transaction Tracking**: Record income and expenses with detailed descriptions and timestamps.
- **Balances Overview**: Get a comprehensive overview of your financial balances.
- **Insights and Reports**: Gain valuable insights into your spending patterns and financial trends through intuitive charts and reports.

Whether you're budgeting for personal expenses, managing finances for a small business, or simply tracking your daily expenditures, DeWallet is your go-to solution for efficient money management.

Start taking control of your finances today with DeWallet!

## Installation

Make sure you have Java 17 installed and follow these steps to install DeWallet on your local machine:

1. **Clone the Repository**: 
   ```bash
   git clone https://github.com/Scoo88/budget-devot
   ```
2. **Navigate to the Project Directory:**
    ```bash
    cd budget-devot
    ```
3. **Start Docker Containers:**
- Navigate to
  ```bash
  cd docker
  ```
- Start Docker Containers:
    - Make sure you have Docker installed on your system.
    - Run the following command to start PostgreSQL and PgAdmin containers
  ```bash
  docker compose up -d
  ```
- Access PostgreSQL Database:
   - PostgreSQL database is now running in a Docker container. You can access it using the following credentials:
      - Host: localhost
      - Port: 5432
      - Database Name: dewallet
      - Username: postgres
      - Password: postgres
- Access PgAdmin:
    - PgAdmin is also running as a Docker container. Open your web browser and go to `http://localhost:15432`. Log in to PgAdmin using the following credentials:
      - Email: admin@admin.hr
      - Password: admin
    - Database connection has been defined in servers.json file so you do not need to create a new server.
4. **Access DeWallet in Your Browser**:
   - Once you have started the application, open your web browser and go to `http://localhost:8226` to access the DeWallet application.
   - Swagger is available at `http://localhost:8226/docs`
   - To login go to `http://localhost:8226/login` and to logout go to `http://localhost:8226/logout`
4. Swagger is available at http://localhost:8226/docs

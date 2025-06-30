# 🏦 ATM Application in Python

This project is a **Command Line Interface (CLI) based ATM simulator**, developed in Python. It replicates basic banking operations like account creation, login, deposit, withdrawal, balance inquiry, and viewing transaction history — all using file-based JSON storage (no database required).

---

## 📌 Features

- 🔐 **User Registration & PIN Authentication**
- 💰 **Deposit / Withdraw Money**
- 📊 **Balance Inquiry**
- 📜 **Transaction History**
- 🔄 **Change PIN**
- 🗂️ **File-Based Data Storage using JSON**
- ❌ **No external database or dependencies required**

---

## 🛠️ Tech Stack

- **Language**: Python 3.x  
- **Storage**: JSON (file-based)  
- **UI**: Command Line Interface (CLI)

---

## 📂 Folder Structure

ATM-Application/
├── atm_app.py # Main application code
├── users/ # Folder to store user data (JSON files)
├── .gitignore # Optional - to exclude sensitive/user files
└── README.md # This documentation

yaml
Copy
Edit

---

## ▶️ How to Run

1. Clone the repo:
   ```bash
   git clone https://github.com/GundlapalliChaitanya/ATM-Application.git
   cd ATM-Application
Run the Python script:

bash
Copy
Edit
python atm_app.py
✅ Make sure Python 3 is installed and added to your PATH.

🧪 Sample Usage
yaml
Copy
Edit
=== 🏦 ATM Menu ===
1. Register
2. Login
3. Exit
Choose option: 1
Enter username: alice
Set 4-digit PIN: 1234
✅ Account created successfully.

...

💰 Available Balance: ₹5000.0
📈 Future Improvements
Add GUI with Tkinter or PyQt

Integrate with MySQL or SQLite

Add Admin panel for managing users

Add Account lockout & security enhancements

import json
import os
from datetime import datetime

DATA_DIR = 'users'

# Ensure user directory exists
if not os.path.exists(DATA_DIR):
    os.makedirs(DATA_DIR)

def load_user(username):
    path = os.path.join(DATA_DIR, f"user_{username}.json")
    if not os.path.exists(path):
        return None
    with open(path, 'r') as file:
        return json.load(file)

def save_user(username, data):
    path = os.path.join(DATA_DIR, f"user_{username}.json")
    with open(path, 'w') as file:
        json.dump(data, file, indent=4)

def register():
    username = input("Enter username: ")
    if load_user(username):
        print("User already exists.")
        return
    pin = input("Set 4-digit PIN: ")
    user_data = {
        "pin": pin,
        "balance": 0.0,
        "transactions": []
    }
    save_user(username, user_data)
    print("✅ Account created successfully.")

def authenticate():
    username = input("Enter username: ")
    user = load_user(username)
    if not user:
        print("❌ Account not found.")
        return None, None
    pin = input("Enter 4-digit PIN: ")
    if pin != user["pin"]:
        print("❌ Incorrect PIN.")
        return None, None
    return username, user

def deposit(user, username):
    try:
        amount = float(input("Enter amount to deposit: "))
        if amount <= 0:
            print("❌ Invalid amount.")
            return
        user["balance"] += amount
        user["transactions"].append({
            "type": "Deposit",
            "amount": amount,
            "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        })
        save_user(username, user)
        print(f"✅ Deposited ₹{amount}. New Balance: ₹{user['balance']}")
    except ValueError:
        print("❌ Invalid input.")

def withdraw(user, username):
    try:
        amount = float(input("Enter amount to withdraw: "))
        if amount > user["balance"]:
            print("❌ Insufficient balance.")
            return
        user["balance"] -= amount
        user["transactions"].append({
            "type": "Withdraw",
            "amount": amount,
            "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        })
        save_user(username, user)
        print(f"✅ Withdrawn ₹{amount}. Remaining Balance: ₹{user['balance']}")
    except ValueError:
        print("❌ Invalid input.")

def view_transactions(user):
    if not user["transactions"]:
        print("ℹ️ No transactions found.")
        return
    print("\n📜 Transaction History:")
    for txn in user["transactions"]:
        print(f"{txn['time']} | {txn['type']} | ₹{txn['amount']}")
    print()

def change_pin(user, username):
    old_pin = input("Enter current PIN: ")
    if old_pin != user["pin"]:
        print("❌ Incorrect current PIN.")
        return
    new_pin = input("Enter new 4-digit PIN: ")
    user["pin"] = new_pin
    save_user(username, user)
    print("✅ PIN changed successfully.")

def main():
    while True:
        print("\n=== 🏦 ATM Menu ===")
        print("1. Register")
        print("2. Login")
        print("3. Exit")
        choice = input("Choose option: ")
        if choice == '1':
            register()
        elif choice == '2':
            username, user = authenticate()
            if user:
                while True:
                    print("\n1. Balance Inquiry")
                    print("2. Deposit")
                    print("3. Withdraw")
                    print("4. Transaction History")
                    print("5. Change PIN")
                    print("6. Logout")
                    option = input("Choose option: ")
                    if option == '1':
                        print(f"💰 Available Balance: ₹{user['balance']}")
                    elif option == '2':
                        deposit(user, username)
                    elif option == '3':
                        withdraw(user, username)
                    elif option == '4':
                        view_transactions(user)
                    elif option == '5':
                        change_pin(user, username)
                    elif option == '6':
                        print("🚪 Logged out.")
                        break
                    else:
                        print("❌ Invalid option.")
        elif choice == '3':
            print("👋 Thank you for using the ATM!")
            break
        else:
            print("❌ Invalid input.")

if __name__ == '__main__':
    main()

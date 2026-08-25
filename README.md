# 💰 JERRIES EXPENSE

<p align="center">

### A Modern Offline-First Personal Finance & Expense Tracker for Android

Track expenses, manage budgets, monitor accounts, analyze spending, build savings goals, and understand your financial habits — all from one fast and privacy-focused Android application.

</p>

<p align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge\&logo=kotlin\&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge\&logo=android\&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge\&logo=jetpackcompose\&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-6750A4?style=for-the-badge\&logo=materialdesign\&logoColor=white)
![Room](https://img.shields.io/badge/Room-Database-FF6F00?style=for-the-badge)
![Hilt](https://img.shields.io/badge/Hilt-DI-4285F4?style=for-the-badge)

</p>

---

## 📌 Overview

**JERRIES EXPENSE** is a production-oriented Android personal finance application designed to provide complete control over personal income, expenses, accounts, budgets, savings goals, recurring transactions, and financial analytics.

The application follows an **offline-first architecture**, allowing users to manage their finances without requiring a constant internet connection.

The project focuses on:

* Clean architecture
* Maintainable Kotlin code
* Reactive UI
* Reliable financial calculations
* Local-first data storage
* Responsive Android UI
* Premium glassmorphic design
* Smooth and performance-conscious animations
* Privacy-oriented personal finance management

---

## ✨ Features

### 💳 Transaction Management

* Add expenses
* Add income
* Transfer money between accounts
* Edit transactions
* Delete transactions
* Transaction details
* Transaction search
* Date-range filtering
* Category filtering
* Account filtering
* Payment-method filtering
* Sort by date and amount
* Transaction notes
* Receipt attachment support

---

### 🏦 Account Management

Track multiple financial accounts and wallets:

* Cash
* Bank accounts
* UPI
* Debit cards
* Credit cards
* Savings accounts
* Other custom accounts

Transfers are treated separately from expenses so that moving money between accounts does **not incorrectly reduce total net worth**.

---

### 📊 Dashboard

The dashboard provides a real-time financial overview including:

* Total balance
* Monthly income
* Monthly expenses
* Monthly savings
* Recent transactions
* Budget usage
* Spending categories
* Spending trends
* Financial insights
* Quick transaction actions

---

### 📈 Financial Analytics

Analyze financial behavior through:

* Daily spending
* Weekly spending
* Monthly spending
* Yearly spending
* Income vs expenses
* Category breakdown
* Spending trends
* Average daily spending
* Highest spending category
* Highest-value transactions
* Month-over-month comparisons
* Savings rate

---

### 🎯 Budget Management

Create and monitor budgets for:

* Individual categories
* Overall spending
* Monthly periods

Track:

* Budget amount
* Amount spent
* Remaining amount
* Percentage used
* Approaching-limit warnings
* Overspending

---

### 🎯 Savings Goals

Create personal financial goals with:

* Target amount
* Current progress
* Deadline
* Contributions
* Withdrawals
* Completion tracking
* Visual progress indicators

---

### 🔄 Recurring Transactions

Manage recurring:

* Expenses
* Income
* Transfers
* Bills
* Subscriptions
* Salary
* Other scheduled transactions

Supported scheduling includes:

* Daily
* Weekly
* Monthly
* Yearly

Background processing is handled using Android's background-work architecture where appropriate.

---

### 🧠 Financial Insights

JERRIES EXPENSE includes a local rules-based financial insight system.

Examples include:

* Spending increases compared with previous periods
* Highest spending categories
* Budget risk warnings
* Daily spending trends
* Savings comparisons
* Budget projections

The insight architecture is designed so additional AI-powered analysis can be integrated in the future without making AI services mandatory for core functionality.

---

### 📄 Reports

Generate financial reports containing:

* Income
* Expenses
* Savings
* Categories
* Accounts
* Budgets
* Date ranges

Export support includes:

* CSV
* JSON backup
* PDF reports where supported

---

### 💾 Backup & Restore

Protect financial data with local backup and restore functionality.

Features include:

* JSON backup
* Data restoration
* Backup validation
* Versioned backup structure
* Corrupted backup detection
* Duplicate-import protection
* Android Storage Access Framework integration

---

### 🔐 Security

Optional application protection includes:

* PIN lock
* Biometric authentication
* Auto-lock
* Sensitive information protection

The application avoids storing authentication credentials as plain text.

---

### 🎨 Premium UI

The application uses a modern Android design system featuring:

* Jetpack Compose
* Material 3
* Light theme
* Dark theme
* Dynamic color support
* Glassmorphic surfaces
* Translucent cards
* Subtle borders
* Soft elevation
* Responsive layouts
* Smooth micro-interactions
* Accessible touch targets

The glass effects are intentionally controlled to maintain performance on mid-range Android devices.

---

## 🏗️ Architecture

JERRIES EXPENSE follows **Clean Architecture + MVVM + Repository Pattern**.

```text
┌─────────────────────────────┐
│        Jetpack Compose      │
│             UI              │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│          ViewModel          │
│       StateFlow / UI State  │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│           UseCases          │
│       Business Logic        │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│         Repository          │
│      Data Abstraction       │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│        Room Database        │
│        Local Storage        │
└─────────────────────────────┘
```

### Core principle

```text
UI
 ↓
ViewModel
 ↓
UseCase
 ↓
Repository
 ↓
Data Source
```

Business logic is kept outside Composables to improve maintainability and testability.

---

## 📁 Project Structure

```text
app/
└── src/main/
    ├── java/com/jerries/expense/
    │
    ├── core/
    │   ├── common/
    │   ├── designsystem/
    │   ├── navigation/
    │   ├── ui/
    │   ├── util/
    │   ├── security/
    │   └── backup/
    │
    ├── data/
    │   ├── local/
    │   │   ├── dao/
    │   │   ├── database/
    │   │   ├── entity/
    │   │   └── converters/
    │   ├── preferences/
    │   └── repository/
    │
    ├── domain/
    │   ├── model/
    │   ├── repository/
    │   └── usecase/
    │
    ├── feature/
    │   ├── dashboard/
    │   ├── transactions/
    │   ├── addtransaction/
    │   ├── budgets/
    │   ├── analytics/
    │   ├── accounts/
    │   ├── recurring/
    │   ├── categories/
    │   ├── goals/
    │   ├── insights/
    │   ├── reports/
    │   ├── backup/
    │   └── settings/
    │
    └── MainActivity.kt
```

---

## 🛠️ Tech Stack

| Technology             | Purpose                        |
| ---------------------- | ------------------------------ |
| **Kotlin**             | Primary programming language   |
| **Jetpack Compose**    | Declarative UI                 |
| **Material 3**         | Design system                  |
| **Room**               | Local database                 |
| **Hilt**               | Dependency injection           |
| **Coroutines**         | Asynchronous programming       |
| **Flow / StateFlow**   | Reactive data and UI state     |
| **Navigation Compose** | Application navigation         |
| **DataStore**          | User preferences               |
| **WorkManager**        | Reliable background work       |
| **AndroidX**           | Android application foundation |

---

## 🚀 Getting Started

### Prerequisites

Install:

* Android Studio
* Android SDK
* JDK compatible with the project's Gradle configuration
* Android emulator or physical Android device

### Clone

```bash
git clone https://github.com/YOUR_USERNAME/JERRIES-EXPENSE.git
```

```bash
cd JERRIES-EXPENSE
```

### Open

Open the project in **Android Studio**.

Allow Gradle to synchronize dependencies.

### Run

Connect an Android device or start an emulator.

Then run the `app` configuration from Android Studio.

Alternatively, on Windows:

```powershell
.\gradlew.bat assembleDebug
```

The generated debug APK can then be installed on a compatible Android device.

---

## 🧪 Testing

The project should be validated across the critical financial workflows:

```text
Income
  ↓
Expense
  ↓
Transfer
  ↓
Edit
  ↓
Delete
  ↓
Budget
  ↓
Analytics
  ↓
Backup
  ↓
Restore
```

Important financial scenarios include:

* Income calculation
* Expense calculation
* Account balance calculation
* Inter-account transfers
* Budget calculations
* Monthly boundaries
* Recurring transactions
* Savings progress
* Backup restoration

---

## 📱 Compatibility

Designed for modern Android devices with responsive layouts for:

* Small phones
* Standard phones
* Large phones
* Tablets
* Portrait orientation
* Landscape orientation

The UI is designed to adapt rather than depend on fixed screen dimensions.

---

## 🔒 Privacy

JERRIES EXPENSE is designed around local-first personal finance management.

Normal expense tracking does not require a mandatory online account or constant internet connection.

Users should review the application's backup and storage behavior before sharing exported financial data.

**Never commit private financial backups, API keys, passwords, signing keys, or other secrets to this repository.**

---

## 🗺️ Future Improvements

Potential future enhancements include:

* Optional cloud synchronization
* Multi-device synchronization
* Advanced AI financial assistant
* Natural-language transaction entry
* Receipt OCR
* Automatic expense categorization
* Bank statement import
* More advanced forecasting
* Custom dashboard widgets
* Multi-currency support
* Localization
* Automated financial recommendations

These are intentionally separated from the core offline-first architecture.

---

## 🎯 Project Goals

JERRIES EXPENSE was created to demonstrate practical Android engineering across:

* Kotlin
* Jetpack Compose
* Clean Architecture
* MVVM
* Room
* Dependency Injection
* Reactive programming
* Financial data modeling
* Responsive UI
* Local persistence
* Background processing
* Data backup and restoration
* Security-conscious development
* Performance optimization

---

## 👨‍💻 Developer

**JERRIES**

Computer Science & Engineering Student
Android Developer • Full-Stack Developer • AI/LLM Developer

### Areas of interest

* Android Development
* Kotlin
* Full-Stack Development
* AI & Local LLMs
* Mobile Applications
* Software Engineering

---

## ⭐ Support

If you find this project useful or interesting, consider giving the repository a ⭐.

---

## 📄 License

This project is currently intended as a personal portfolio project.

If you plan to use, modify, or redistribute the project, review and add an appropriate open-source license before doing so.

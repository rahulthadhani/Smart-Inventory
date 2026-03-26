# Smart Inventory 📦

A lightweight Android inventory management app built with Kotlin, designed to run efficiently on low-end Android devices. Each user has their own account with private inventory data stored locally on the device.
 
---

## Features

- **Account system** — register and log in with email and password. Each account has its own private inventory.
- **Dashboard** — see total item count, total inventory value, and low stock warnings at a glance.
- **Inventory list** — browse all your items with search functionality and color-coded low stock indicators.
- **Add / Edit / Delete items** — full CRUD support with fields for name, description, quantity, price, category, and low stock threshold.
- **Navigation drawer** — sidebar navigation with your username, email, and avatar initial displayed at the top.
- **Offline first** — all data is stored locally using SQLite via Room. No internet connection required.
- **Low-end device optimized** — built with memory efficiency in mind using RecyclerView with DiffUtil, LiveData, and a lightweight Room database.

---

## Screenshots

> _Add screenshots here once the app is running on your device._
 
---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Architecture | MVVM (Model-View-ViewModel) |
| Database | Room (SQLite) |
| UI | XML Layouts |
| Navigation | Navigation Component |
| Async | Kotlin Coroutines |
| Reactive data | LiveData |
| Password hashing | jBCrypt |
 
---

## Project Structure

```
app/src/main/java/com/example/smartinventory/
├── data/
│   ├── db/
│   │   ├── AppDatabase.kt          # Room database singleton
│   │   ├── UserDao.kt              # User queries
│   │   └── ItemDao.kt              # Inventory item queries
│   ├── model/
│   │   ├── User.kt                 # User entity
│   │   └── InventoryItem.kt        # Inventory item entity
│   └── repository/
│       └── InventoryRepository.kt  # Single source of truth
├── ui/
│   ├── auth/
│   │   ├── LoginFragment.kt
│   │   └── RegisterFragment.kt
│   ├── dashboard/
│   │   └── DashboardFragment.kt
│   └── items/
│       ├── ItemListFragment.kt
│       ├── ItemAdapter.kt
│       └── AddEditItemFragment.kt
├── util/
│   └── SessionManager.kt           # Manages login session via SharedPreferences
├── viewmodel/
│   ├── AuthViewModel.kt
│   ├── InventoryViewModel.kt
│   └── ViewModelFactory.kt
└── MainActivity.kt                 # Hosts navigation drawer and nav graph
```
 
---

## Database Schema

### users
| Column | Type | Notes |
|---|---|---|
| id | INTEGER | Primary key, auto-generated |
| username | TEXT | |
| email | TEXT | Unique index |
| password_hash | TEXT | Hashed with jBCrypt |

### inventory_items
| Column | Type | Notes |
|---|---|---|
| id | INTEGER | Primary key, auto-generated |
| user_id | INTEGER | Foreign key → users.id |
| name | TEXT | |
| description | TEXT | |
| quantity | INTEGER | |
| price | REAL | |
| category | TEXT | |
| low_stock_threshold | INTEGER | Default 5 |
| created_at | INTEGER | Unix timestamp |
| updated_at | INTEGER | Unix timestamp |
 
---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android device or emulator running API 21 (Android 5.0) or higher
- JDK 8 or higher

### Setup

1. Clone the repository:
```bash
git clone https://github.com/rahulthadhani/Smart-Inventory.git
```

2. Open the project in Android Studio.

3. Wait for Gradle to sync.

4. Run the app on a device or emulator.

### Building an APK

Go to **Build → Build Bundle(s) / APK(s) → Build APK(s)**. The APK will be output to:
```
app/build/outputs/apk/debug/app-debug.apk
```
 
---

## Security Notes

- Passwords are never stored in plain text. They are hashed using [jBCrypt](https://www.mindrot.org/projects/jBCrypt/) before being saved to the database.
- All inventory data is scoped to the logged-in user via a foreign key relationship. Users can only see and manage their own items.
- Session data is stored in SharedPreferences and cleared on logout.

---

## Minimum Requirements

| Requirement | Value |
|---|---|
| Minimum Android version | API 21 (Android 5.0 Lollipop) |
| Target Android version | API 34 (Android 14) |
| Storage | ~10MB for app + database |
| RAM | Works on devices with 1GB RAM or more |
 
---

## Future Improvements

- Export inventory to CSV or PDF
- Item images via camera or gallery
- Barcode scanning for quick item lookup
- Cloud sync across multiple devices
- Charts and analytics on the dashboard
- Multiple categories with custom colors

---

## License

This project is open source and available under the [MIT License](LICENSE).
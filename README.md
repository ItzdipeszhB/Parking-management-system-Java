# Parking Management System (Java + MySQL)

A simple console-based **Parking Management System** built with **Java** and **MySQL** using **JDBC**.  
It allows users to **park a vehicle**, **exit a vehicle**, automatically **calculate parking fees**, and **view parking records** stored in a database.

---

## Features

- **Park a vehicle**
  - Stores `vehicle_number`
  - Saves `entry_time` as current time (`NOW()`)
  - Keeps `exit_time` and `parking_fee` as `NULL` until vehicle exits

- **Exit a vehicle**
  - Finds the active record (same vehicle number with `exit_time IS NULL`)
  - Stores `exit_time`
  - Calculates fee based on duration + vehicle category
  - Updates record with `parking_fee`

- **View parking records**
  - Displays all records from the database in tabular form:
    - ID, Vehicle Number, Entry Time, Exit Time, Fee

---

## Fee Calculation Logic

Fee is computed based on the category’s hourly rate:

| Category | Rate per hour (Rs) |
|---------:|---------------------:|
| Car      | 50.0 |
| Bike     | 25.0 |
| Bus      | 100.0 |
| Default  | 25.0 |

Formula used:

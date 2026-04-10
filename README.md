# ⚖️ Ainoggo

A JavaFX-based law and appointment management system.

---

## 🚀 Features

* 👨‍⚖️ Lawyer profiles
* 📅 Appointment booking system
* 👤 User dashboard
* 🔐 Login & Registration
* 📸 Image upload support

---

## 🛠️ Technologies Used

* Java
* JavaFX
* MySQL
* CSS

---

## 📂 Project Structure

* src/ → Source code
* lib/ → Libraries
* uploaded_photos/ → Images

---

## 👥 Team Members

* Most:Moriom Sultana
* Abu Ubida Wadi

---

## 🎓 Supervisor

* Md. Emamul Haque Pranta(Lecturer,BUET)

---
## ⚙️ Installation Guide

Follow these steps to run the project locally:

### 🔹 1. Clone the Repository


git clone https://github.com/abuubidawadi/Ainoggo.git


### 🔹 2. Open the Project

* Open the project in **IntelliJ IDEA** or **VS Code**

### 🔹 3. Setup Database (MySQL)

1. Open **MySQL Workbench** 
2. Create a database named:


ainoggo


3. Create required tables:

* users
* lawyers
* appointments

### 🔹 4. Configure Database Connection

* Open your database connection file
* Update these values:


String url = "jdbc:mysql://crossover.proxy.rlwy.net:27408/railway?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

String username = "root";

String password = "IgcmcGlSPEswVxgRhrNKhMdsUguxcYUh";


### 🔹 5. Add Images Folder

* Make sure this folder exists:


uploaded_photos/


* Put all lawyer images inside this folder

### 🔹 6. Run the Project

* Run the main class (e.g., `App.java`)


---

## ✅ Requirements

* Java (JDK 17 or higher)
* MySQL
* IntelliJ IDEA / VS Code


▶️  How to Run

1.Clone the repository:
git clone https://github.com/abuubidawadi/Ainoggo.git

2.Open in IDE (IntelliJ / VS Code)

3.Run the project
## 📸  Some Screenshots

### Login Page
![Login](uploaded_photos/login.png)

### UserDashboard
![UserDashboard](uploaded_photos/userdashboard.png)

### Book Appointment
![BookAppointment](uploaded_photos/bookappointment.png)

### LawyerDashboard
![LawyerDashboard](uploaded_photos/lawyerdashboard.png)

## 🎥 Demo Video

[Watch Demo](https://youtu.be/3wfJSzklKco?si=kgcBmQGBS9f3HNfI)

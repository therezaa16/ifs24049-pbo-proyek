# Laundry Management System

Aplikasi manajemen laundry berbasis web dengan Spring Boot, menyediakan dashboard, statistik, dan upload foto bukti cucian.

## 🚀 Tech Stack

- Java 25 & Spring Boot 4.0
- Spring Security (Session & JWT)
- Thymeleaf, Bootstrap 5, Chart.js
- PostgreSQL/MySQL/H2
- Maven

## ✨ Fitur

- ✅ Login & Register dengan Spring Security
- ✅ CRUD Order Laundry (Tambah, Edit, Hapus, Detail)
- ✅ Dashboard dengan statistik & chart
- ✅ Upload foto bukti cucian (max 5MB)
- ✅ Search order berdasarkan nama/HP
- ✅ REST API dengan JWT authentication

## 📦 Instalasi & Menjalankan

### 1. Clone & Install Dependencies
```bash
git clone 
cd 
mvn clean install
```

### 2. Konfigurasi Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/db_laundry_app
spring.datasource.username=your_username
spring.datasource.password=your_password

app.upload.dir=./uploads
spring.servlet.multipart.max-file-size=5MB
```

### 3. Jalankan Aplikasi
```bash
mvn spring-boot:run
```
Akses: **http://localhost:8080**

## 🧪 Testing & Coverage

```bash
# Run tests
mvn test

# Generate coverage report
./mvnw test jacoco:report

# Open report (Windows)
mvn clean test; start target\site\jacoco\index.html

# Open report (Mac/Linux)
mvn clean test && open target/site/jacoco/index.html
```

## 📱 Endpoints

### Web UI
| Route | Deskripsi |
|-------|-----------|
| `/` | Dashboard |
| `/auth/login` | Login |
| `/auth/register` | Register |
| `/laundry` | List order |
| `/laundry/{id}` | Detail order |

### REST API (requires JWT)
| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| POST | `/api/auth/login` | Login & get token |
| GET | `/api/laundry` | Get all orders |
| POST | `/api/laundry` | Create order |
| PUT | `/api/laundry/{id}` | Update order |
| DELETE | `/api/laundry/{id}` | Delete order |

**Auth Header:** `Authorization: Bearer <token>`

## 📊 Dashboard Features

- Total order, pending, proses, revenue
- Chart statistik layanan (Bar Chart)
- Chart status pesanan (Doughnut)
- Search & filter

## 📸 Upload Foto

- Format: JPG, PNG, WEBP, GIF
- Max size: 5MB
- Preview sebelum upload
- Disimpan di folder `uploads/`

## 🔧 Troubleshooting

**Error 500 di `/laundry`:**
- Check file `templates/pages/laundry/home.html` exists
- Check console log untuk detail error

**Upload foto gagal:**
- Check permission folder `uploads/`
- Pastikan `app.upload.dir` configured correctly

## 📝 Changelog

### 11-12-2025
- ✅ Frontend laundry management completed
- ✅ Chart implementation with Chart.js
- ✅ WebAuthInterceptor for session handling
- ✅ Upload foto with preview
- ✅ Better error handling

### 20-11-2025
- Update dependencies

### 15-11-2025
- Initial project setup

## 📄 License

Proyek ini dibuat untuk tujuan **Pendidikan**.

---

**Happy Coding! 🚀**
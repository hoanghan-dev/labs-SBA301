# KỊCH BẢN THUYẾT TRÌNH: MONGODB WITH JAVA

---

## PHẦN A — TÓM TẮT NHANH (QUICK SUMMARY)

* **Mục tiêu bài thuyết trình:** Trình bày cơ chế tương tác giữa Java và MongoDB thông qua source code thực tế; phân tích kiến trúc tầng, vòng đời kết nối, kỹ thuật Object-Document Mapping thủ công và các thao tác CRUD cốt lõi với BSON Driver.
* **Các kiến thức MongoDB trình bày:**
  1. Mô hình kết nối Client – Database – Collection (`MongoClient`, Connection Pool).
  2. Sự khác biệt giữa Java Object (POJO) và MongoDB BSON Document.
  3. Cơ chế Object-Document Mapping (ODM) thủ công và so sánh với `MappingMongoConverter` trong Spring Data.
  4. Thao tác CRUD cấp thấp qua Driver API: `insertOne()`, `find()`, `updateOne()`, `deleteOne()`, sử dụng `Filters` và `Document("$set")`.
  5. Luồng xử lý dữ liệu nhiều tầng: Entry point (`Main`) $\rightarrow$ `Service` $\rightarrow$ `Repository` $\rightarrow$ `DAO` $\rightarrow$ MongoDB.
* **Công nghệ thực tế sử dụng:** Java 21, **MongoDB Java Sync Driver 5.1.1** (`org.mongodb:mongodb-driver-sync`), Logging SLF4J (`slf4j-jdk14:1.7.28`), mô hình phân tầng Enterprise (POJO - DAO - Repository - Service).
* **Những file cần mở trên IDE:**
  1. `pom.xml`
  2. `src/main/java/sba301/fe/MongoDBUtils.java`
  3. `src/main/java/sba301/fe/pojo/Student.java`
  4. `src/main/java/sba301/fe/dao/StudentDAO.java`
  5. `src/main/java/sba301/fe/service/StudentService.java`
  6. `src/main/java/sba301/fe/main/Main.java`
* **Thứ tự trình bày:** `pom.xml` $\rightarrow$ `MongoDBUtils.java` $\rightarrow$ `Student.java` $\rightarrow$ `StudentDAO.java` $\rightarrow$ `StudentService.java` $\rightarrow$ `Main.java` $\rightarrow$ Kết luận.
* **Tổng thời lượng dự kiến:** 10 phút (~1.000 từ thoại tiếng Việt, tốc độ 120–130 từ/phút kết hợp trỏ chuột và di chuyển code).

---

## PHẦN B — CHECKLIST CHUẨN BỊ (PRE-FLIGHT CHECKLIST)

Trước khi bắt đầu bài thuyết trình trước giảng viên và lớp, hãy dành 2 phút thực hiện các bước sau:

- [ ] **Mở đúng Project trên IDE:** Mở project `CRUDMongoDBDemo` tại thư mục gốc trong IntelliJ IDEA hoặc Eclipse.
- [ ] **Mở sẵn 6 tab mã nguồn quan trọng theo thứ tự:**
  1. Tab 1: `pom.xml`
  2. Tab 2: `MongoDBUtils.java`
  3. Tab 3: `Student.java`
  4. Tab 4: `StudentDAO.java`
  5. Tab 5: `StudentService.java`
  6. Tab 6: `Main.java`
- [ ] **BẢO MẬT TUYỆT ĐỐI:** **KHÔNG** mở hoặc để lộ file `atlas-credentials.env` trên màn hình trình chiếu nhằm bảo vệ URI, username và password thực tế của cơ sở dữ liệu.
- [ ] **Độ phóng đại màn hình (Font Size):** Nhấn `Ctrl + Chuột lăn` (hoặc `View > Appearance > Enter Presentation Mode`) để cỡ chữ trong code đạt tối thiểu 16–18pt, đảm bảo người ngồi cuối phòng vẫn đọc rõ.
- [ ] **Đóng các tool window không cần thiết:** Thu gọn Terminal, Database Tool, Git log để toàn màn hình tập trung vào vùng soạn thảo code.
- [ ] **Kiểm tra trạng thái MongoDB:**
  - Nếu demo trực tiếp: Đảm bảo MongoDB local đang chạy ở port `27017` hoặc biến môi trường `MONGODB_URI` đã trỏ đúng vào cluster Atlas.
  - Phương án dự phòng: Nếu mạng trường học chặn cổng hoặc MongoDB local chưa bật, bài thuyết trình được thiết kế theo dạng **Code Walkthrough tĩnh chuyên sâu**, phân tích trực tiếp cấu trúc logic mà không bắt buộc phải chạy demo runtime.

---

## PHẦN C — BẢN ĐỒ ĐIỀU HƯỚNG SOURCE CODE

| Thời gian | File cần mở | Vị trí cần focus (Dòng / Block) | Nội dung trọng tâm | Hành động trên IDE |
|---|---|---|---|---|
| **00:00 – 00:45** | `pom.xml` | Toàn cảnh Project | Mở đầu, giới thiệu bài toán & công nghệ nền tảng | Đứng tại tab `pom.xml`, chào giảng viên & lớp |
| **00:45 – 02:00** | `pom.xml` | Dòng 13–25 | Dependency `mongodb-driver-sync` 5.1.1 & SLF4J; Phân định 3 tầng abstraction | Bôi đen dòng 13–17, phân tích Driver cấp thấp |
| **02:00 – 03:30** | `MongoDBUtils.java` | Dòng 10–39 & 53–58 | Singleton Pattern, Vòng đời `MongoClient`, Connection Pool & Quản lý URI | Mở Tab 2; trỏ dòng 14, dòng 33–39, dòng 53–58 |
| **03:30 – 05:00** | `Student.java` & `StudentDAO.java` | `Student.java` (Dòng 3–10) & `StudentDAO.java` (Dòng 71–86) | Mô hình thực thể POJO & Cơ chế ánh xạ BSON sang Object (Manual ODM) | Chuyển Tab 3 xem thuộc tính; chuyển Tab 4 dòng 71 xem `mapDocumentToStudent` |
| **05:00 – 07:15** | `StudentDAO.java` | Dòng 20–31 (Save), 41–47 (Find), 49–58 (Update), 60–69 (Delete) | Phân tích chi tiết 4 thao tác CRUD trực tiếp với BSON Document | Cuộn lần lượt qua từng method `save`, `findByEmail`, `update`, `delete` |
| **07:15 – 08:45** | `StudentService.java` & `Main.java` | `StudentService.java` (Dòng 36–42) & `Main.java` (Dòng 13–45) | Luồng điều phối tầng Service, nghiệp vụ validate và Console Menu điều hướng | Chuyển Tab 5 dòng 36; chuyển Tab 6 phân tích `switch-case` và log filter |
| **08:45 – 09:30** | `Main.java` | Toàn màn hình IDE | Tổng kết giá trị kỹ thuật, liên hệ Spring Data MongoDB | Dừng ở `Main.java`, nhìn về phía người nghe |
| **09:30 – 10:00** | — | — | Cảm ơn & Mời đặt câu hỏi Q&A | Lắng nghe phản hồi từ giảng viên |

---

## PHẦN D — KỊCH BẢN ĐỌC CHI TIẾT (WORD-FOR-WORD SCRIPT)

---

### Phần 1 — Mở đầu & Giới thiệu Mục tiêu (00:00 – 00:45)

* **File cần mở:** `pom.xml`
* **Vị trí cần hiển thị:** Toàn cảnh file `pom.xml`.
* **Hành động trên IDE:** Giữ chuột ở phần thông tin `groupId` và `artifactId` (`CRUDMongoDBDemo`).

**Lời thoại:**
> "Kính thưa thầy/cô và các bạn, hôm nay em xin đại diện nhóm trình bày về chủ đề: **Tương tác giữa Java và Cơ sở dữ liệu NoSQL MongoDB thông qua MongoDB Java Driver**.
>
> Trong chương trình học của môn SBA301, chúng ta đã được làm quen với khái niệm NoSQL Document Model ở Slot 32 và bức tranh tổng thể về Spring Data MongoDB ở Slot 33. Mục tiêu lớn nhất của bài thuyết trình hôm nay là đi sâu vào bản chất nền tảng nhất: Thay vì dùng các framework tự động hóa hoàn toàn, chúng ta sẽ mở mã nguồn dự án để xem một Java Application kết nối, quản lý connection pool và biến đổi các Java Object thành BSON Document ở tầng Driver cấp thấp như thế nào.
>
> Toàn bộ bài trình bày sẽ kéo dài trong 10 phút, đi trực tiếp trên source code thực tế của dự án."

* **Điểm cần nhấn mạnh:** Nhấn mạnh rằng bài thuyết trình không nói lý thuyết suông mà đi từ gốc rễ kỹ thuật thông qua code thực tế.
* **Chuyển tiếp:** *"Trước tiên, em xin mời thầy cô và các bạn nhìn vào file cấu hình `pom.xml` để xác định công nghệ thực tế đang được sử dụng."*

---

### Phần 2 — Khảo sát Công nghệ & Kiến trúc Phân tầng (00:45 – 02:00)

* **File cần mở:** `pom.xml` (file:///d:/Documents/FPT_Education/FA26/SBA301/code-demo/slot32-sba/slot32-sba/pom.xml#L11-L26)
* **Vị trí cần hiển thị:** Dòng 11 đến 25.
* **Hành động trên IDE:** Bôi đen đoạn khai báo `<dependency>` của `mongodb-driver-sync` tại dòng 13–17.

**Lời thoại:**
> "Nhìn vào block dependency từ dòng 13 đến dòng 17, chúng ta thấy dự án sử dụng thư viện `org.mongodb:mongodb-driver-sync` phiên bản `5.1.1`. Cùng với đó, ở dòng 20 đến 24 là `slf4j-jdk14` phục vụ việc ghi log hệ thống cho Driver.
>
> Điều thú vị đầu tiên cần lưu ý: Dự án của chúng em là một ứng dụng thuần Java Core chạy trên Java 21, **không sử dụng Spring Boot Starter hay Spring Data MongoDB**. 
>
> Theo lý thuyết bài học Slot 33, hệ sinh thái MongoDB cho Java có 3 mức trừu tượng: Mức cao nhất là `MongoRepository`, mức trung bình là `MongoTemplate`, và mức cơ sở bên dưới chính là **MongoDB Java Sync Driver**. Việc dự án lựa chọn trực tiếp tầng Sync Driver 5.1.1 mang lại giá trị học tập rất lớn: Nó giúp chúng ta hiểu chính xác những gì diễn ra bên dưới các framework, từ việc bắt tay socket, quản lý pool, đến cấu trúc BSON Document nguyên bản.
>
> Đồng thời, nhìn vào cây thư mục bên tay trái, dự án vẫn tuân thủ chặt chẽ kiến trúc phân tầng chuẩn Enterprise: Tầng POJO định nghĩa dữ liệu, tầng DAO thực thi truy vấn vật lý, tầng Repository làm cầu nối dữ liệu, tầng Service điều phối nghiệp vụ, và tầng Main là điểm khởi chạy."

* **Điểm cần nhấn mạnh:** Làm rõ sự khác biệt giữa việc dùng Driver trực tiếp và Spring Data; khẳng định giá trị của việc học tầng Driver để hiểu bản chất hoạt động bên dưới.
* **Chuyển tiếp:** *"Để bắt đầu giao tiếp với MongoDB, thành phần đầu tiên cần được khởi tạo là kết nối. Em xin chuyển sang file `MongoDBUtils.java`."*

---

### Phần 3 — Quản lý Kết nối & Connection Pool (02:00 – 03:30)

* **File cần mở:** `src/main/java/sba301/fe/MongoDBUtils.java` (file:///d:/Documents/FPT_Education/FA26/SBA301/code-demo/slot32-sba/slot32-sba/src/main/java/sba301/fe/MongoDBUtils.java#L10-L58)
* **Vị trí cần hiển thị:** Dòng 10–39 và dòng 53–58.
* **Hành động trên IDE:** 
  1. Trỏ vào biến `mongoClient` ở dòng 14.
  2. Cuộn qua method `resolveConnectionString()` từ dòng 16 đến 31.
  3. Bôi đen method `getMongoClient()` ở dòng 33–39.
  4. Trỏ vào method `close()` ở dòng 53–57.

**Lời thoại:**
> "Đây là class `MongoDBUtils`, giữ vai trò quản trị kết nối tập trung cho toàn bộ ứng dụng.
>
> Ở dòng 14, chúng em khai báo một biến tĩnh `private static MongoClient mongoClient = null;`. Trong MongoDB Java Driver, `MongoClient` là một đối tượng thread-safe và đại diện cho một **Connection Pool** chứa nhiều kết nối vật lý, chứ không phải một socket đơn lẻ.
>
> Hãy quan sát cách lấy connection string tại method `resolveConnectionString` từ dòng 16 đến 31: Mã nguồn có tư duy production rất tốt khi không ép cứng thông tin xác thực. Thứ tự ưu tiên là: Đọc từ System Property `-Dmongodb.uri`, nếu không có thì đọc từ biến môi trường `MONGODB_URI`, và cuối cùng mới fallback về `mongodb://localhost:27017` cho môi trường local. Điều này giúp ngăn chặn nguy cơ rò rỉ credential lên Git.
>
> Tiếp theo, tại dòng 33, method `getMongoClient()` áp dụng mẫu thiết kế **Singleton** với từ khóa `synchronized`. Cơ chế này đảm bảo trong suốt vòng đời của ứng dụng, chỉ có đúng một instance `MongoClient` được tạo ra. Nếu lập trình viên mắc phản mẫu tạo một `MongoClient` mới cho mỗi câu lệnh CRUD, ứng dụng sẽ nhanh chóng cạn kiệt tài nguyên cổng mạng và quá tải server MongoDB.
>
> Từ dòng 41 đến 51, các method `getDatabase()` và `getStudentCollection()` hoạt động như các handle logic trỏ vào Database `SBA301DB` và Collection `students` mà không tốn chi phí mở lại kết nối vật lý. Cuối cùng, dòng 53 là method `close()` để giải phóng toàn bộ Connection Pool khi hệ thống dừng hoạt động."

* **Điểm cần nhấn mạnh:** Giải thích bản chất `MongoClient` là Connection Pool (Thread-safe), quy tắc bắt buộc phải reuse Singleton, và cách cấu hình URI linh hoạt qua biến môi trường.
* **Chuyển tiếp:** *"Sau khi đã có handle trỏ tới Collection `students`, câu hỏi đặt ra là: Dữ liệu sinh viên trong Java được biểu diễn thế nào và làm sao để chuyển đổi vào MongoDB? Em xin chuyển qua `Student.java` và tầng DAO."*

---

### Phần 4 — Mô hình Dữ liệu & Kỹ thuật Object-Document Mapping (03:30 – 05:00)

* **File cần mở:** `src/main/java/sba301/fe/pojo/Student.java` và `src/main/java/sba301/fe/dao/StudentDAO.java`
* **Vị trí cần hiển thị:** 
  - `Student.java` dòng 3–10 (Khai báo thuộc tính).
  - `StudentDAO.java` dòng 71–86 (`mapDocumentToStudent`).
* **Hành động trên IDE:** 
  1. Mở tab `Student.java`, chỉ vào các trường `id`, `email`, `firstName`, `lastName`, `marks`.
  2. Chuyển ngay sang tab `StudentDAO.java`, cuộn xuống dòng 71 bôi đen method `mapDocumentToStudent`.

**Lời thoại:**
> "Tại file `Student.java`, đây là một POJO Java thuần túy gồm các trường: `id` kiểu số nguyên, `email`, `password`, `firstName`, `lastName` và `marks`. Vì chúng ta dùng Driver thuần nên class này hoàn toàn không có các annotation của Spring Data như `@Document` hay `@Id`.
>
> Vậy làm thế nào Java hiểu được một Document BSON trả về từ MongoDB?
>
> Xin mời thầy cô nhìn sang `StudentDAO.java` tại dòng 71, method `mapDocumentToStudent`. Đây chính là hiện thân thực tế của khái niệm **ODM — Object-Document Mapping**, nhưng được viết thủ công bằng code Java.
>
> Trong Spring Data, công việc này do thành phần `MappingMongoConverter` âm thầm xử lý tự động qua Reflection. Nhưng ở tầng Driver, chúng ta phải tự thực hiện việc bóc tách:
> - Dòng 73 đến 76: Lấy trường `_id` từ BSON Document. Vì MongoDB có thể lưu số dưới dạng `Integer` hoặc `Long`, code ép kiểu an toàn thông qua lớp cha `java.lang.Number` rồi chuyển thành `intValue()`.
> - Dòng 77 đến 80: Đọc các trường kiểu chuỗi bằng `doc.getString()`.
> - Dòng 81 đến 84: Tiếp tục bóc tách điểm số `marks` dưới dạng `Number`.
>
> Đoạn code này minh họa rất rõ một nguyên lý cốt lõi trong tài liệu: MongoDB có schema linh hoạt, nhưng khi dữ liệu ánh xạ vào hệ thống kiểu tĩnh của Java, lập trình viên phải chủ động kiểm soát kiểu dữ liệu để tránh lỗi `ClassCastException` hoặc sai lệch dữ liệu."

* **Điểm cần nhấn mạnh:** Giải thích cơ chế ánh xạ giữa BSON Document và Java Object; so sánh việc code thủ công `mapDocumentToStudent` với cơ chế ngầm `MappingMongoConverter` của Spring Data.
* **Chuyển tiếp:** *"Bây giờ, chúng ta sẽ đi vào phần trọng tâm nhất của bài thuyết trình: Các thao tác CRUD vật lý được thực thi trong `StudentDAO`."*

---

### Phần 5 — Phân tích Thao tác CRUD cốt lõi với BSON Driver (05:00 – 07:15)

* **File cần mở:** `src/main/java/sba301/fe/dao/StudentDAO.java` (file:///d:/Documents/FPT_Education/FA26/SBA301/code-demo/slot32-sba/slot32-sba/src/main/java/sba301/fe/dao/StudentDAO.java#L20-L69)
* **Vị trí cần hiển thị:** Các method `save`, `getStudents`, `findByEmail`, `update`, `delete`.
* **Hành động trên IDE:** Cuộn lần lượt qua từng method và dùng chuột chỉ vào các câu lệnh gọi Driver.

**Lời thoại:**
> "Tại `StudentDAO`, toàn bộ các thao tác được thực hiện trực tiếp thông qua đối tượng `collection` kiểu `MongoCollection<Document>` khởi tạo ở dòng 17.
>
> **1. Thao tác CREATE — method `save()` (Dòng 20–31):**
> Code khởi tạo một đối tượng `org.bson.Document`. Nếu `student.getId() > 0`, nó sẽ gán trực tiếp vào trường khóa chính `_id` của MongoDB, sau đó dùng phương thức chaining `.append()` để nhồi các cặp key-value và gọi `collection.insertOne(doc)`.
> *Một điểm lưu ý kỹ thuật:* Dự án đang tự quản lý `_id` dạng số nguyên do lập trình viên truyền vào, thay vì để MongoDB tự sinh `ObjectId` 12-byte ngẫu nhiên.
>
> **2. Thao tác READ ALL & READ FILTER — method `getStudents()` và `findByEmail()` (Dòng 33–47):**
> Ở dòng 35, `collection.find()` trả về một `FindIterable<Document>`. Vòng lặp `for-each` duyệt qua từng Document và gọi hàm map dữ liệu. Driver sẽ tự động mở và quản lý cursor đọc dữ liệu từ server.
> Đến dòng 42 trong `findByEmail`, thay vì ghép chuỗi JSON thô dễ gây lỗi cú pháp, code sử dụng **Filter Builder** của Driver: `Filters.eq("email", email)`. Hàm `.first()` sẽ trả về Document khớp đầu tiên hoặc `null` nếu không tìm thấy.
>
> **3. Thao tác UPDATE — method `update()` (Dòng 49–58):**
> Ở dòng 51 đến 56, code tạo ra một Document mới chứa các trường cần sửa, rồi bọc vào toán tử `$set` của MongoDB: `new Document("$set", updateFields)`. Lệnh `collection.updateOne(filter, ...)` thực hiện cập nhật cục bộ (partial update) chỉ các field được chỉ định mà không ghi đè mất các field khác. Sau đó nó gọi lại `findByEmail` để trả về trạng thái mới nhất của đối tượng.
>
> **4. Thao tác DELETE — method `delete()` (Dòng 60–69):**
> Method này có logic kiểm tra rất linh hoạt: Ưu tiên xóa theo `email` qua `Filters.eq("email", ...)`; nếu không có email thì fallback về xóa theo khóa chính `_id`. Lệnh `collection.deleteOne()` sẽ tìm và xóa chính xác một document khớp điều kiện."

* **Điểm cần nhấn mạnh:** Chỉ rõ các method native của Driver (`insertOne`, `find`, `updateOne`, `deleteOne`), cách dùng `Filters.eq()`, và kỹ thuật partial update bằng toán tử `$set`.
* **Chuyển tiếp:** *"Các thao tác truy cập dữ liệu đã hoàn tất ở tầng DAO. Vậy dữ liệu này được đưa lên ứng dụng và kiểm soát luồng ra sao? Em xin mở `StudentService.java` và `Main.java`."*

---

### Phần 6 — Luồng Điều phối & Thực thi End-to-End (07:15 – 08:45)

* **File cần mở:** `src/main/java/sba301/fe/service/StudentService.java` và `src/main/java/sba301/fe/main/Main.java`
* **Vị trí cần hiển thị:**
  - `StudentService.java` dòng 36–42 (`update`).
  - `Main.java` dòng 13–45 (`main` & `switch-case`).
* **Hành động trên IDE:**
  1. Trỏ vào `StudentService.java` dòng 37–40.
  2. Chuyển sang `Main.java`, chỉ vào dòng 13 cấu hình Logger và khối `switch (key)` từ dòng 26 đến 44.

**Lời thoại:**
> "Quan sát tại `StudentService.java` dòng 36 đến 42, chúng ta thấy ý nghĩa của việc tách tầng: Tầng Repository chỉ đơn thuần làm nhiệm vụ CRUD, trong khi tầng Service chịu trách nhiệm về logic nghiệp vụ. Cụ thể ở hàm `update`, Service kiểm tra xem sinh viên có tồn tại trong cơ sở dữ liệu qua `findByEmail` hay không rồi mới quyết định gọi cập nhật, tránh các thao tác thừa đối với database.
>
> Cuối cùng, chúng ta đến với điểm khởi chạy ứng dụng tại `Main.java`:
> - Ở dòng 13: Lệnh `Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);` là một kinh nghiệm thực tế rất hay. Mặc định MongoDB Driver ghi log kết nối rất nhiều; câu lệnh này lọc bớt các thông tin INFO, giúp màn hình console của chúng ta sạch sẽ, chỉ hiện cảnh báo quan trọng.
> - Dòng 14: Khởi tạo một đối tượng `Student` mẫu mang ID là 2, email `lammn15@gmail.com`.
> - Từ dòng 18 đến 45: Ứng dụng in Menu điều khiển bằng dòng lệnh Console qua 4 phím bấm: Phím 1 lấy toàn bộ danh sách, phím 2 lưu sinh viên mới, phím 3 xóa sinh viên, và phím 4 cập nhật thông tin.
>
> Toàn bộ luồng đi dữ liệu được vận hành khép kín: Người dùng chọn chức năng $\rightarrow$ `Main` gọi `StudentService` $\rightarrow$ `StudentRepository` $\rightarrow$ `StudentDAO` $\rightarrow$ chuyển đổi POJO sang BSON $\rightarrow$ gửi lệnh qua `MongoClient` tới MongoDB server $\rightarrow$ kết quả trả về được map ngược thành POJO để in ra màn hình."

* **Điểm cần nhấn mạnh:** Tính trật tự của kiến trúc 3 lớp (Service - Repository - DAO), kỹ thuật lọc log Driver, và chu trình khép kín từ UI console xuống cơ sở dữ liệu.
* **Chuyển tiếp:** *"Từ việc khảo sát toàn bộ mã nguồn trên, em xin rút ra một số kết luận kỹ thuật tổng kết cho bài trình bày."*

---

### Phần 7 — Tổng kết Kỹ thuật & Liên hệ Bài học (08:45 – 09:30)

* **File cần mở:** Giữ màn hình tại `Main.java` hoặc sơ đồ phân tầng.
* **Vị trí cần hiển thị:** Toàn cảnh màn hình code.
* **Hành động trên IDE:** Đứng thẳng, hướng mắt về phía giảng viên và người nghe.

**Lời thoại:**
> "Kính thưa thầy/cô, qua việc phân tích trực tiếp mã nguồn của dự án, chúng em xin đúc kết 3 bài học quan trọng:
>
> 1. **Hiểu rõ tầng Driver là chìa khóa:** Mặc dù Spring Data MongoDB cung cấp `MongoRepository` rất tiện lợi, nhưng việc hiểu rõ `MongoClient`, connection pool, `Filters` builder và cách map BSON sang POJO thủ công như trong project này giúp chúng ta làm chủ hoàn toàn quá trình truy cập dữ liệu và debug hiệu quả khi gặp lỗi.
> 2. **Vòng đời tài nguyên:** Luôn ghi nhớ `MongoClient` là tài nguyên đắt giá, cần áp dụng Singleton Pattern và đóng kết nối đúng lúc.
> 3. **Kiến trúc phân tầng sạch:** Việc phân tách POJO, DAO, Repository và Service giúp code dễ bảo trì, dễ viết Unit Test và có thể dễ dàng nâng cấp sang Spring Boot hoặc Spring Data sau này mà không làm xáo trộn luồng nghiệp vụ.
>
> Đây chính là nền tảng vững chắc để chúng em tự tin tiếp tục tích hợp MongoDB vào các dự án lớn hơn ở các môn học tiếp theo."

* **Điểm cần nhấn mạnh:** Khẳng định giá trị thực tiễn của kiến thức nền tảng và tư duy kiến trúc sạch.
* **Chuyển tiếp:** *"Đó là toàn bộ nội dung thuyết trình của nhóm em."*

---

### Phần 8 — Lời cảm ơn & Chuyển sang Q&A (09:30 – 10:00)

* **Hành động trên IDE:** Giữ nguyên màn hình code rõ ràng, sẵn sàng mở bất kỳ file nào giảng viên yêu cầu xem lại.

**Lời thoại:**
> "Em xin chân thành cảm ơn thầy/cô và các bạn đã chú ý lắng nghe! Sau đây, nhóm em rất mong nhận được những nhận xét, góp ý và sẵn sàng trả lời các câu hỏi từ thầy/cô ạ!"

---

## PHẦN E — TỔNG KẾT ĐỂ KẾT THÚC BÀI (CLOSING STATEMENT)

*(Đoạn văn tóm lược 30–45 giây dành cho người trình bày đọc liền mạch nếu thời gian sắp hết)*

> "Tóm lại, dự án `CRUDMongoDBDemo` đã minh họa trọn vẹn cách một ứng dụng Java tương tác với MongoDB ở cấp độ nền tảng nhất: Sử dụng official **MongoDB Java Sync Driver 5.1.1** để quản lý Connection Pool thông qua Singleton `MongoClient`, tự xây dựng cơ chế Object-Document Mapping giữa POJO `Student` và BSON `Document`, đồng thời thực hiện đầy đủ chu trình CRUD bằng các câu lệnh `insertOne`, `find`, `updateOne` và `deleteOne` kết hợp với `Filters` builder. Việc nắm vững tầng Driver này chính là chìa khóa cốt lõi giúp chúng ta hiểu sâu sắc cơ chế chuyển đổi dữ liệu mà các framework như Spring Data MongoDB tự động hóa bên dưới. Em xin cảm ơn thầy cô và các bạn!"

---

## PHẦN F — CÂU HỎI GIẢNG VIÊN CÓ THỂ HỎI & CÁCH TRẢ LỜI (Q&A DEFENSE)

Dưới đây là 10 câu hỏi giảng viên rất hay hỏi khi chấm bài thuyết trình code MongoDB với Java, kèm câu trả lời ngắn gọn, câu trả lời chuyên sâu và vị trí file code cần mở để bảo vệ quan điểm.

---

### Câu 1: Tại sao trong `pom.xml` dự án dùng `mongodb-driver-sync` mà không dùng Spring Data MongoDB như tiêu đề bài học Slot 33?

* **Trả lời ngắn:** Dạ thưa thầy/cô, dự án này được thiết kế để học và minh họa **tầng cơ sở nền tảng (low-level)** của tương tác Java – MongoDB. Spring Data MongoDB thực chất là tầng trừu tượng được xây dựng đè lên trên chính Java Sync Driver này.
* **Trả lời sâu:** Trong Section 3.2 và 4.58 của tài liệu Slot 33 có phân tích 3 mức trừu tượng: High (`MongoRepository`), Medium (`MongoTemplate`), và Low (`Java Sync Driver`). Hiểu được tầng Sync Driver giúp chúng em nắm được bản chất: Connection pool hoạt động ra sao, đối tượng `Document` BSON được tạo và bóc tách thế nào. Sau khi đã hiểu bản chất này, việc nâng cấp lên Spring Data chỉ là chuyển đổi sang dùng `MappingMongoConverter` tự động mà thôi.
* **Mở code minh họa:** Mở `pom.xml` dòng 13–17 và `StudentDAO.java` dòng 71.

---

### Câu 2: Biến `MongoClient` trong `MongoDBUtils` có vai trò gì? Tại sao phải dùng Singleton và `synchronized`?

* **Trả lời ngắn:** `MongoClient` là điểm truy cập trung tâm kết nối tới MongoDB, bên trong nó quản lý một **Connection Pool** chứa nhiều kết nối vật lý. Nó là thread-safe nên phải dùng Singleton để tái sử dụng, tránh tạo mới gây cạn kiệt tài nguyên.
* **Trả lời sâu:** Theo tài liệu Slot 33 (mục 4.43 & 4.44), tạo `MongoClient` là một thao tác rất đắt đỏ vì nó phải thiết lập kết nối socket, bắt tay TLS, xác thực và cấu hình pool. Nếu mỗi request hay mỗi lần gọi DAO lại gọi `MongoClients.create()` thì cổng mạng sẽ bị bão hòa (exhaustion) và MongoDB server sẽ bị quá tải kết nối. Do đó, hàm `getMongoClient()` dùng `synchronized` để đảm bảo cơ chế thread-safe, chỉ khởi tạo đúng một instance duy nhất trong toàn bộ vòng đời ứng dụng.
* **Mở code minh họa:** Mở `MongoDBUtils.java` dòng 14–15 và dòng 33–39.

---

### Câu 3: Khác biệt giữa Document trong MongoDB và Class POJO trong Java là gì? Quá trình chuyển đổi diễn ra ở đâu?

* **Trả lời ngắn:** POJO `Student` là class định kiểu tĩnh trong bộ nhớ Java. BSON `Document` là cấu trúc nhị phân dạng key-value linh hoạt của MongoDB. Quá trình chuyển đổi trong code diễn ra thủ công tại method `save()` (chuyển POJO $\rightarrow$ Document) và `mapDocumentToStudent()` (chuyển Document $\rightarrow$ POJO) trong `StudentDAO`.
* **Trả lời sâu:** MongoDB lưu trữ dữ liệu dạng BSON. Java là ngôn ngữ strictly-typed. Trong dự án này, do không dùng `MappingMongoConverter` của Spring Data, chúng em thực hiện cơ chế ODM thủ công: Ở chiều ghi (Write), hàm `save` bóc các getter của `Student` đưa vào `Document.append()`. Ở chiều đọc (Read), hàm `mapDocumentToStudent` đọc từng field BSON, kiểm tra instance kiểu `Number` để tránh crash rồi set ngược vào thuộc tính của `Student`.
* **Mở code minh họa:** Mở `StudentDAO.java` dòng 21–29 và dòng 71–86.

---

### Câu 4: Trường `_id` trong code đang được xử lý như thế nào? Có gì khác so với mặc định của MongoDB?

* **Trả lời ngắn:** Code đang dùng trực tiếp thuộc tính `student.getId()` kiểu số nguyên (`int`) để gán vào `_id`. Điều này khác với mặc định của MongoDB là tự sinh ra chuỗi mã hóa 12-byte `ObjectId`.
* **Trả lời sâu:** Ở dòng 22–24 trong `StudentDAO`, code kiểm tra `if (student.getId() > 0)` thì mới append vào `_id`. Điều này có nghĩa ứng dụng đang nắm quyền kiểm soát khóa chính (Application-assigned identity). Ưu điểm là khóa chính dễ nhớ, dễ quản lý theo ID dạng số truyền thống. Nhược điểm là lập trình viên phải tự đảm bảo tính duy nhất, nếu trùng ID sẽ bị ném lỗi `DuplicateKeyException` từ Driver, chứ không tận dụng được cơ chế phân tán tự sinh không đụng độ của `ObjectId`.
* **Mở code minh họa:** Mở `StudentDAO.java` dòng 22–24 và `Student.java` dòng 4.

---

### Câu 5: Method `update` trong `StudentDAO` đang áp dụng kỹ thuật gì? Nó có ghi đè toàn bộ document không?

* **Trả lời ngắn:** Method `update` sử dụng toán tử `$set` của MongoDB (`new Document("$set", updateFields)`), đây là kỹ thuật **Partial Update** (cập nhật từng phần), không ghi đè toàn bộ Document.
* **Trả lời sâu:** Theo lý thuyết Slot 33 mục 4.32, nếu chúng ta dùng cơ chế `save()` của Spring Data trên một đối tượng có sẵn ID, nó sẽ thực hiện replace toàn bộ document, nếu có trường null sẽ bị mất dữ liệu. Nhưng ở đây trong Driver, code dùng `collection.updateOne(filter, new Document("$set", updateFields));`. MongoDB chỉ cập nhật các trường được liệt kê trong `updateFields` (password, firstName, lastName, marks), giữ nguyên các trường khác nếu có, đảm bảo an toàn dữ liệu.
* **Mở code minh họa:** Mở `StudentDAO.java` dòng 49–58.

---

### Câu 6: Tại sao trong `findByEmail` và `delete` lại dùng `Filters.eq()` thay vì truyền chuỗi JSON thô?

* **Trả lời ngắn:** Dùng `Filters.eq()` là sử dụng **Builder API** an toàn của MongoDB Driver, giúp tránh lỗi cú pháp chuỗi, kiểm soát kiểu dữ liệu tốt hơn và code dễ đọc hơn.
* **Trả lời sâu:** Theo mục 4.50 của tài liệu Slot 33, việc tự nối chuỗi JSON như `"{ 'email': '" + email + "' }"` rất dễ dẫn tới lỗi cú pháp khi chuỗi chứa ký tự đặc biệt, đồng thời tiềm ẩn nguy cơ bảo mật như injection. `Filters.eq("email", email)` trả về một đối tượng Bson có cấu trúc, được kiểm soát chặt chẽ và tương thích hoàn toàn với wire protocol của Driver.
* **Mở code minh họa:** Mở `StudentDAO.java` dòng 42 và dòng 65.

---

### Câu 7: Code xử lý ra sao trong tình huống không tìm thấy dữ liệu khi tìm kiếm hoặc cập nhật?

* **Trả lời ngắn:** Khi tìm kiếm không thấy, Driver trả về `null`. Khi cập nhật, tầng Service kiểm tra `existingStudentOpt != null`, nếu không thấy sẽ trả về `null` thay vì tiếp tục gọi DAO.
* **Trả lời sâu:** Trong `StudentDAO.findByEmail`, phương thức `.first()` của `FindIterable` sẽ trả về `null` nếu cursor rỗng. Ở tầng `StudentService` dòng 37–41, code đã triển khai nghiệp vụ kiểm tra trước: `Student existingStudentOpt = iStudentRepo.findByEmail(email);`. Nếu tìm thấy thì mới thực hiện `update`, nếu không sẽ trả về `null`. Đây là cơ chế phòng vệ tốt ở tầng nghiệp vụ.
* **Mở code minh họa:** Mở `StudentDAO.java` dòng 42–46 và `StudentService.java` dòng 37–42.

---

### Câu 8: Điểm hạn chế hiện tại trong `StudentDAO.update()` và `delete()` là gì? Làm sao để tối ưu theo chuẩn của Driver?

* **Trả lời ngắn:** Code hiện tại chưa hứng và kiểm tra kết quả trả về từ `UpdateResult` và `DeleteResult` để biết chính xác số lượng bản ghi đã được match hay modify.
* **Trả lời sâu:** Theo khuyến nghị ở Step 16 và mục 4.56 trong tài liệu, các phương thức `collection.updateOne()` và `collection.deleteOne()` trả về đối tượng `UpdateResult` và `DeleteResult`. Trong đó có các phương thức rất quan trọng như `getMatchedCount()`, `getModifiedCount()` và `getDeletedCount()`. Việc kiểm tra các count này giúp ứng dụng khẳng định chính xác thao tác vật lý có thành công hay không, thay vì chỉ giả định thành công khi không có exception văng ra.
* **Mở code minh họa:** Mở `StudentDAO.java` dòng 56 và dòng 65.

---

### Câu 9: Trong `Main.java`, dòng lệnh `Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);` có ý nghĩa gì?

* **Trả lời ngắn:** Dòng lệnh này dùng để cấu hình mức độ log của Driver, chỉ cho phép hiển thị các thông báo từ mức Cảnh báo (`WARNING`) trở lên, ẩn các log thông tin (`INFO`) rườm rà khi kết nối.
* **Trả lời sâu:** MongoDB Sync Driver khi khởi tạo Connection Pool sẽ ghi ra rất nhiều log debug và info (thông tin cluster, handshake, server description, heartbeat). Nếu không set level sang `WARNING`, màn hình Console của menu ứng dụng sẽ bị tràn ngập các dòng log kỹ thuật, gây khó khăn cho người dùng khi theo dõi kết quả CRUD.
* **Mở code minh họa:** Mở `Main.java` dòng 13.

---

### Câu 10: Nếu muốn nâng cấp toàn bộ project này sang Spring Boot và Spring Data MongoDB, chúng ta cần thay đổi những gì?

* **Trả lời ngắn:** Đổi dependency trong `pom.xml` sang `spring-boot-starter-data-mongodb`, gắn annotation `@Document` và `@Id` vào `Student.java`, cho `IStudentRepository` kế thừa `MongoRepository<Student, Integer>`, và xóa bỏ class `MongoDBUtils` cũng như `StudentDAO` vì Spring Boot sẽ tự cấu hình.
* **Trả lời sâu:** Cụ thể theo lộ trình trong tài liệu Slot 33:
  1. Thay thế dependency Sync Driver bằng Starter của Spring Boot.
  2. Bổ sung cấu hình URI vào `application.properties` với tiền tố `spring.data.mongodb.uri`.
  3. Biến `Student` thành Domain Entity với `@Document(collection = "students")` và `@Id`.
  4. Thay vì viết class `StudentRepository` thủ công, chúng ta chỉ cần tạo một interface kế thừa `MongoRepository<Student, Integer>`, Spring Data sẽ tự động sinh proxy implementation lúc runtime.
  5. Khi đó, toàn bộ code mở kết nối trong `MongoDBUtils` và code map dữ liệu trong `StudentDAO` đều được framework tự động hóa hoàn toàn.
* **Mở code minh họa:** Mở `pom.xml` và `IStudentRepository.java`.

---

## PHẦN G — PHIẾU GHI NHỚ 60 GIÂY (SPEAKER CHEAT SHEET)

In hoặc dán phiếu này lên một góc màn hình máy tính trước khi bắt đầu nói:

```text
================================================================================
                    SPEAKER 60-SECOND QUICK CHEAT SHEET
================================================================================
1. THỨ TỰ CLICK TAB MÃ NGUỒN:
   pom.xml  -->  MongoDBUtils.java  -->  Student.java  -->  StudentDAO.java  -->  StudentService.java  -->  Main.java

2. 3 TỪ KHÓA BẮT BUỘC PHẢI NÓI:
   - "MongoDB Java Sync Driver 5.1.1" (tầng cơ sở, không phải Spring Data)
   - "MongoClient là Connection Pool Singleton" (Thread-safe, tuyệt đối không tạo lặp)
   - "Object-Document Mapping (ODM) thủ công" (tự bóc Document BSON sang POJO Student)

3. VỊ TRÍ CODE TRỌNG TÂM CẦN CHỈ CHUỘT:
   - pom.xml: dòng 13 (dependency mongodb-driver-sync)
   - MongoDBUtils.java: dòng 14 (mongoClient static), dòng 33 (getMongoClient synchronized)
   - StudentDAO.java: dòng 21 (insertOne), dòng 42 (Filters.eq), dòng 56 (updateOne $set), dòng 71 (mapDocumentToStudent)
   - Main.java: dòng 13 (Logger filter level WARNING)

4. CÂU NÓI NỐI TUYỆT VỜI NHẤT:
   "Dự án không dùng Spring Data tự động mà dùng Driver thuần để chúng ta nhìn thấy rõ
    cơ chế chuyển đổi dữ liệu và quản lý kết nối gốc rễ bên dưới framework."

5. ĐIỀU TUYỆT ĐỐI TRÁNH:
   - KHÔNG mở file 'atlas-credentials.env' (tránh lộ mật khẩu DB trên màn hình).
   - KHÔNG nói "đây là cơ sở dữ liệu quan hệ" hay "bảng Student" (phải dùng Collection & Document).
   - KHÔNG nhận vơ là dự án có Spring Boot khi trong pom.xml chỉ có Java thuần.
================================================================================
```


System zapisów do lekarza

Aplikacja webowa do obsługi zapisów pacjentów do lekarzy. Projekt został wykonany w technologii Spring Boot z bazą danych MySQL, Dockerem oraz prostym frontendem HTML/CSS/JavaScript.

System umożliwia logowanie użytkowników z różnymi rolami: administrator, lekarz oraz pacjent. Każda rola posiada osobny panel i dostęp do innych funkcji.

Technologie
Java
Spring Boot
Spring Web
Spring Data JPA
Spring Security
JWT
BCrypt
MySQL
Docker
HTML
CSS
JavaScript
Funkcje aplikacji
Pacjent

Pacjent może:

zalogować się do systemu,
przeglądać dostępnych lekarzy,
sprawdzać wolne terminy wizyt,
rezerwować wizyty,
anulować swoje wizyty,
przeglądać swoje zaplanowane wizyty.
Lekarz

Lekarz może:

zalogować się do systemu,
dodawać wolne terminy wizyt,
usuwać swoje wolne terminy,
przeglądać dzisiejsze wizyty,
sprawdzać historię wizyt pacjenta.
Administrator

Administrator może:

przeglądać użytkowników,
usuwać użytkowników bez historii wizyt,
przeglądać wszystkie wizyty,
filtrować i wyszukiwać wizyty,
usuwać wizyty,
sprawdzać statystyki systemu,
akceptować lub odrzucać rejestracje lekarzy.
Uruchomienie projektu

Projekt można uruchomić za pomocą Dockera.

W katalogu głównym projektu należy wykonać:

docker compose up --build

Jeżeli baza danych była już wcześniej uruchamiana i trzeba ją zresetować, należy użyć:

docker compose down -v
docker compose up --build

Po uruchomieniu aplikacja będzie dostępna pod adresem:

http://localhost:8080

Strona logowania:

http://localhost:8080/login.html
Baza danych

Aplikacja korzysta z bazy danych MySQL uruchamianej w Dockerze.

Domyślna baza:

clinic_db

Dane do połączenia z bazą przez MySQL Workbench:

Host: 127.0.0.1
Port: 3307
User: root
Password: password
Database: clinic_db

Dane startowe są ładowane z pliku:

src/main/resources/data.sql

Po uruchomieniu projektu na czystej bazie zostaną automatycznie dodani przykładowi użytkownicy, lekarze, pacjenci, wolne terminy oraz przykładowe wizyty.

Dane logowania
Administrator
Email: admin@example.com
Hasło: 1234
Lekarz 1
Email: doctor@example.com
Hasło: 1234
Lekarz 2
Email: doctor2@example.com
Hasło: 1234
Pacjent 1
Email: anna@example.com
Hasło: 1234
Pacjent 2
Email: marek@example.com
Hasło: 1234
Najważniejsze endpointy
Autoryzacja
POST /auth/login
Lekarze
GET /doctors
GET /doctors/me
Pacjenci
GET /patients/me
Wizyty
GET /appointments/me
POST /appointments/book
DELETE /appointments/{appointmentId}
GET /appointments/available?doctorId={doctorId}
GET /appointments/available/all
GET /appointments/doctor/today
GET /appointments/doctor/patients/{patientId}/history
Sloty lekarza
GET /slots/me
POST /slots
DELETE /slots/{slotId}
Administrator
GET /admin/me
GET /admin/users
DELETE /admin/users/{userId}
GET /admin/appointments
DELETE /admin/appointments/{appointmentId}
GET /admin/stats
GET /admin/doctors/pending
PUT /admin/doctors/{userId}/approve
PUT /admin/doctors/{userId}/reject
Opis działania

Użytkownik loguje się do systemu za pomocą adresu e-mail i hasła. Po poprawnym logowaniu otrzymuje token JWT, który jest wykorzystywany do autoryzacji kolejnych zapytań.

Po zalogowaniu użytkownik zostaje przekierowany do odpowiedniego panelu zależnie od roli.

Pacjent może wybrać lekarza oraz wolny termin wizyty. Po rezerwacji termin zostaje oznaczony jako zajęty, a w systemie tworzona jest wizyta.

Lekarz może zarządzać swoimi wolnymi terminami oraz przeglądać wizyty pacjentów.

Administrator zarządza użytkownikami, wizytami oraz rejestracjami lekarzy.

Autor

Projekt wykonany jako aplikacja webowa w technologii Spring Boot, MySQL, Docker oraz HTML/CSS/JavaScript.

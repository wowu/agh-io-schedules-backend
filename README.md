# agh-io-schedules-backend

<h3>Instrukcja</h3>

```shell
Wymagania :
  - docker

1. Uruchomienie aplikacji wewnątrz kontenera docker.
  - docker-compose build
  - docker-compose up
  
2. Połączenie się z bazą danych.
  - psql postgresql://docker:docker@localhost:5820/io_schedules
  
3. Korzystanie z API.
  - aplikacja jest uruchomiona na porcie 8080
  - dokumentacja API znajduje się na stronie :
    https://documenter.getpostman.com/view/15316035/TzRPkA3h#b6c0b159-f8f8-4da5-bf63-a3b519d66d5a

4. Użytkownicy.
  - po uruchominiu aplikacji jest utworzone tylko konto administratora
    - login : admin@io.pl 
    - hasło : mlk72bx
    
  - w celu stworzenia nowego konta należy skorzystać bezpośrednio z API
    lub panelu internetowego 

5. Gotowa aplikacja.
  - wdrożona aplikacja jest dostepna pod adresem:
    https://agh-schedules-backend.herokuapp.com
```



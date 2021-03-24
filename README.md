# agh-io-schedules-backend

<h3>Run Spring Boot App Locally</h3>

```shell
sudo docker build -f db.Dockerfile -t io_postgres .
sudo docker run -p 5432:5432 io_postgres
gradle bootRun
```

<h3>Run Spring Boot App inside Docker Container</h3>

```shell
sudo docker build -f db.Dockerfile -t io_postgres .
sudo docker build -f app.Dockerfile -t io_backend .
sudo docker-compose up
```

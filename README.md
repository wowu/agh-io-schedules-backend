# agh-io-schedules-backend

<h3>Run Spring Boot App Locally</h3>

```shell
sudo docker build -f db.Dockerfile -t io_postgres .
sudo docker run -p 5432:5432 io_postgres
gradle bootRun
```

<h3>Run Spring Boot App inside Docker Container</h3>

```shell
sudo docker-compose up
psql postgresql://docker:docker@localhost/postgres
```

<h3>Authentication and Authorization</h3>

```
Admin credentials:
- username : admin@io.pl
- password : mlk72bx

Public key : zvxcTUTglkOP67x

1.Authentication

HTTP Method : POST
HTTP endpoint : /api/token/create

RequestBody : {
 "username" : ...
 "password" : ...
}

ResponseBody: {
 "token" : ...
 "refreshToken" : ...
}

Access token expiration time : 10 minutes
Refresh token expiration time : 45 minutes

2.Authorization

Authorization : Bearer <token>
If server return HTTP 401, it means that access token is expired.

3. Refresh token.

HTTP Method : POST
HTTP endpoint : /api/token/refresh
Authorization : Bearer <refreshToken>

ResponseBody: {
 "token" : ...
 "refreshToken" : ...
}

If server return HTTP 401, it means that refresh token is expired.
Client need to be authenticated again.

```

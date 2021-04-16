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

<h3>Schedule uploading/downloading</h3>

```shell
1. Uploading schedule

HTTP Method : POST
HTTP endpoint : /api/schedule/upload

RequestBody : {
 "files": ...
}

(All files accepted)
ResponseBody: {
 "message": "Files uploaded successfully."
}

(Partial/None files accepted)
ResponseBody: {
 "schedules": [
    {
        "schedule": ...
        "conflicts": [
            {
              "schedule": ...
              "conflict meetings": [
                  {
                    "reason": ...
                    "meeting": {
                        "date start": ...
                        ...
                        ...
                    }
                  },
                  ...
              ]
            },
            ...
        ]
    }, 
    ...
 ]
}

2. Downloading schedule

HTTP Method : GET
HTTP endpoint : /api/schedule/download/{fileId}

RequestBody : {
}

(Correct Id)
ResponseBody: {
  <data to be downloaded>
}

(Wrong Id)
ResponseBody: {
 "ERROR": "Wrong excel file id."
}

3. List of schedules (names of Excels)

HTTP Method : GET
HTTP endpoint : /api/schedule/getFiles

RequestBody : {
}

ResponseBody: {
  [List of files]
}
```


<h3>Subscriptions</h3>

```shell
1. Add subscriptions by admin.

HTTP Method : POST
HTTP endpoint : /api/subscription/add

RequestBody : {
 "scheduleId" : ...
 "emailList" : [
   firstEmail,
   secondEmail,
   ...
 ]
}

ResponseBody: {
 true
}

If emails list contains incorrect email, then you receive HTTP 404 with error message and none of subscription has been created.
If schedule does not exist, you also receive HTTP 404 with error message.

2. Add subscription using public link.

HTTP Method : POST
HTTP endpoint : /api/subscription/addByLink

RequestParams:
"publicLink" : ...
"email" : ...

ResponseBody: {
 true
}

If emails list contains incorrect email, then you receive HTTP 404 with error message.
If schedule doesn't exist, you also receive HTTP 404 with error message.

```

<h3>Schedule management</h3>

```shell
1. Getting specific schedule

HTTP Method : GET
HTTP endpoint : /api/schedule/get

RequestParams:
"scheduleId" : ...

(Successful)
ResponseBody: {
    "schedule": ...
    "meetings": [
        {
            "date start": ...
            "date end": ...
            ...
        },
        ...
    ]
}

(Unsuccesful)
ResponseBody: {
 "ERROR": "Wrong schedule id. Schedule doesn't exist."
}

2. Getting all schedules

HTTP Method : GET
HTTP endpoint : /api/schedule/getSchedules

(Successful)
ResponseBody: {
  "schedules": [
      {
          "schedule": ...
          "meetings": [
              {
                  "date start": ...
                  "date end": ...
                  ...
              },
              ...
          ]
      },
      ...
  ]
}

(Unsuccesful)
ResponseBody: {
 "ERROR": "Something did go wrong. Sorry for that!"
}

```
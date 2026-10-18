# ATS API Document

---

##### CommonResponse:

| Name    |   Type |       Note       |
|---------|-------:|:----------------:|
| code    |    int |  response code   |
| message | string | response message |
| success |   bool | boolean success  |
| result  |    obj |  response data   |

---

## User

### Get User Info:
> URL: /api/user/getUserInfo  
> Method: GET  

- #### Response:

| Name          |   Type |                Note                |
|---------------|-------:|:----------------------------------:|
| name          | string |             user name              |
| email         | string |             user email             |
| phone         | string |         user phone number          |
| type          |    int |       ESP User or Candidate        |
| status        |    int |            user status             |
| join_date     |    str |             join date              |
| roles         |  array |         ["role1", "role2"]         |
| permissions   |  array |          ["pem1", "pem2"]          |
| notifications |  array | json array: see table notification |
| tasks         |  array | json array: see table notification |

- **table notification** 

| Name           |   Type |              Note               |
|----------------|-------:|:-------------------------------:|
| id             |    int |         notification id         |
| user_id        |    int |             user id             |
| message        | string |      notification message       |
| trigger_obj_id |    int |        trigger object id        |
| type           |    int |        notification type        |
| type_name      |    str |     notification type name      |
| extend_param   |   json |        extend parameters        |
| create_time    |    str |    notification created time    |
| notify_time    |    str |   notification notified time    |
| task           |   bool | true: notification; false: task |


```json
{
  "code": 1000,
  "message": "success",
  "success": true,
  "result": {
    "name": "Test User",
    "email": "test@espressif.com",
    "phone": null,
    "type": 0,
    "status": 0,
    "join_date": "2023-09-04 14:26:07",
    "roles": [
      "sa"
    ],
    "permissions": [
      "rpe",
      "rps",
      "jpc",
      "jpe",
      "jps",
      "tpc",
      "tpe",
      "tps",
      "crc",
      "cre",
      "crd",
      "crs"
    ],
    "notifications": [
      {
        "id": 2,
        "user_id": 20,
        "message": "test notification",
        "trigger_obj_id": 2,
        "type": 0,
        "type_name": "notification type",
        "extend_param": {
          "k1": "v1"
        },
        "create_time": "2023-09-07 10:53:17",
        "notify_time": "2023-09-07 10:53:20",
        "task": false
      }
    ],
    "tasks": [
      {
        "id": 3,
        "user_id": 20,
        "message": "test task",
        "trigger_obj_id": 3,
        "type": 1,
        "type_name": "task type",
        "extend_param": {
          "k1": "v1"
        },
        "create_time": "2023-09-07 10:53:17",
        "notify_time": "2023-09-07 10:53:20",
        "task": true
      }
    ]
  }
}
```

### Remove All User's Notifications
> URL: /api/user/removeAllNotifications  
> Method: POST  

- #### Request:
- #### Response:
```json
{
  "code": 1000,
  "message": "success",
  "success": true
}
```

### Remove Specific Notification
> URL: /api/user/removeNotification  
> Method: POST  
> ContentType: JSON  

- #### Request

| Name           |   Type |              Note               |
|----------------|-------:|:-------------------------------:|
| id             |    int |         notification id         |

```json
{
  "id": 1
}
```

- #### Response:
```json
{
  "code": 1000,
  "message": "success",
  "success": true
}
```

## Recruitment Module

### Create Project Api:

> URL: /api/recruitment/createProject<br>
> Method : POST<br>
> ContentType:JSON <br>

- #### Request:

| Name   |   Type | Mandatory |        Note         |
|--------|-------:|:---------:|:-------------------:|
| name   | string |   true    |    project name     |
| admins |  array |   true    | [userId1,userId2..] |

```json
{
  "name": "test_project",
  "admins": [
    1,
    2
  ]
}
```

- #### Response:

| Name       | Type |      Note      |
|------------|-----:|:--------------:|
| project_id |  int | new project id |

```json
{
  "code": 1000,
  "message": "success",
  "success": true,
  "result": {
    "project_id": 1
  }
}
```

### Get Accessible Project Summary Api:

> URL: /api/recruitment/listProjectSummary
>
> Method : GET
>
> ContentType:JSON

- #### Request:

| Name   | Type | Mandatory | Note                             |
|--------|------|-----------|----------------------------------|
| status | int  | false     | status code 0: active 1:inactive |

- #### Response:
- ##### Array

| Name         | Type   | Note                |
|--------------|--------|---------------------|
| project_id   | int    | project id          |
| project_name | string | project name        |
| status_code  | int    | project status code |
| status_name  | string | project status name |

```json
[
  {
    "project_id": 1,
    "project_name": "test_project",
    "status_code": 0,
    "status_name": "ACTIVE"
  },
  {
    "project_id": 3,
    "project_name": "test_project1",
    "status_code": 0,
    "status_name": "ACTIVE"
  },
  {
    "project_id": 9,
    "project_name": "test_project2",
    "status_code": 0,
    "status_name": "ACTIVE"
  }
]  
```

### List Project:

> URL: /recruitment/listProject<br>
> Method: GET<br>
> ContentType: JSON <br>

- #### Request:

| Name   | Type | Mandatory | Note                             |
|--------|------|-----------|----------------------------------|
| status | int  | false     | status code 0: active 1:inactive |

```

- #### Response:

| Name              | Type   | Note |
|-------------------|--------|------|
| project_id        | int    |      |
| project_name      | string |      |
| status_type_value | int    |      |
| status_type_name  | string |      |
| created_by_id     | int    |      |
| created_by_name   | string |      |
| created_at        | string |      |
| admins            | array  |      |

```json


{
  "code": 1000,
  "message": "success",
  "success": true,
  "result": [
    {
      "project_id": 9,
      "project_name": "test_project2",
      "status_type_value": 0,
      "status_type_name": "ACTIVE",
      "created_by_id": 2,
      "created_by_name": "other",
      "created_at": "2023-08-14 08:18:59",
      "admins": [
        {
          "id": 1,
          "name": "xiongrui"
        }
      ]
    },
    {
      "project_id": 11,
      "project_name": "project3",
      "status_value": 0,
      "status_name": "ACTIVE",
      "created_by_id": 1,
      "created_by_name": "xiongrui",
      "created_at": "2023-08-16 07:14:34",
      "admins": [
        {
          "id": 1,
          "name": "xiongrui"
        }
      ]
    }
  ]
}


```

### Create Job Position Api:

> URL: /api/recruitment/createJobPosition<br>
> Method : POST<br>
> ContentType:JSON <br>

- #### Request:

| Name             |   Type | Mandatory |        Note         |
|------------------|-------:|:---------:|:-------------------:|
| title            | string |   true    | Job Position Title  |
| recruitment_type |    int |   true    |                     |
| resume_inbox     | string |   true    |                     |
| hr_recruiter     |    int |   true    |     hr user id      |
| job_req          | string |   true    |                     |
| job_desc         | string |   true    |                     |
| work_location    |  array |   true    | e.g:    [1,2,3...]  |
| job_type         |    int |   true    |                     |
| employment_type  |    int |   true    |                     |
| project          |    int |   true    | Related Project id  |
| publish_channel  |    int |   true    |                     |
| hr_coordinators  |  array |   False   | [userId1,userId2..] |

```json
{
  "title": "test_project",
  "recruitment_type": 0,
  "resume_inbox": "campus.in@espressif.com",
  "hr_recruiter": 1,
  "job_req": "job requierment",
  "job_desc": "job describtion",
  "work_location": [
    0,
    1,
    2
  ],
  "job_type": 0,
  "employment_type": 0,
  "hr_coordinators": [
    1,
    2
  ],
  "project": 1,
  "publish_channel": 1
}
```

- #### Response:

| Name            | Type |        Note         |
|-----------------|-----:|:-------------------:|
| job_position_id |  int | new job_position_id |

```json
{
  "code": 1000,
  "message": "success",
  "success": true,
  "result": {
    "job_position_id": 1
  }
}
```

### List User By Role:

> URL: /user/listUserByRole<br>
> Method: GET<br>
> ContentType: JSON <br>

- #### Request:

| Name | Type   | Mandatory | Note                                                                                                                                                                     |
|------|--------|-----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| role | string | true      | System_Admin = sa;HR_Director = hrd;Project_Admin = proa; HR_Recruiters = hrr; HR_Recruiter_Coordinator = hrrc; Hiring_Manager = hm; Interviewer = itv; Candidate = cdt; |


- #### Response:

| Name | Type   | Note      |
|------|--------|-----------|
| id   | int    | user id   |
| name | string | user name |

```json
{
  "code": 1000,
  "message": "success",
  "success": true,
  "result": [
    {
      "id": 1,
      "name": "xiongrui"
    },
    {
      "id": 2,
      "name": "other"
    }
  ]
}
```

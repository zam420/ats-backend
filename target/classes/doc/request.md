# Http Request

项目中对 request 的改造

## 示例

### 通用方法

```python
def common_request(request):
    # 获取 ats.web.user.models.user_model.User
    user = request.user
```

### Get 请求

```python
class QueryParams:
    def __init__(self, id: int, name: str):
        self.id = id
        self.name = name


@get_mapping
def get_request(request):
    # 将请求参数转换成指定对象
    query_param: QueryParams = request.get_query(QueryParams)
    # 获取指定类型的参数
    name = request.get_param("name", str)
```

### Post 请求

```python
class RequestBody:
    def __init__(self, id: int, name: str):
        self.id = id,
        self.name = name

        
@post_mapping        
def post_request(request):
    # 将请求体转换成指定对象
    request_body: RequestBody = request.get_body(RequestBody)
```

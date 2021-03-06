## 1 功能分析用例图

![studywell 用户用例图](https://raw.githubusercontent.com/mele-y/MDpic/main/studywell_usercase.png)

## 2 系统架构

![系统架构](https://raw.githubusercontent.com/mele-y/MDpic/main/%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84.png)

## 3 数据库设计
user表

| 字段           | 类型    | 样例                                           | 说明                     |
| -------------- | ------- | ---------------------------------------------- | ------------------------ |
| user_id        | integer | 1                                              | 用户标示符，主键，不可空 |
| username       | text    | mele                                           | 不可空                   |
| password       | text    | showmethemoney                                 | 不可空                   |
| photo_location | text    | /www/wwwroot/test/static/user_photo/1_mele.jpg | 用于人脸比对的照片，     |

book表

| 字段             | 类型 | 样例                                                   | 说明                 |
| ---------------- | ---- | ------------------------------------------------------ | -------------------- |
| book_id          | int  | 12                                                     | 主键，不可空         |
| book_name        | text | 白夜行                                                 | 不可空               |
| author           | text | 东野圭吾                                               | 可空                 |
| publication      | text | 人民邮电出版社                                         | 可空                 |
| publish_date     | text | 2017-10-26                                             | 可空                 |
| book_description | text | 四大名著之一                                           | 图书附加说明，可空   |
| book_location    | text | /www/wwwroot/bookfile/白夜行.pdf                       | 图书存储位置，不可空 |
| upload_date      | text | 2020-11-29 11:24                                       | 上传时间，不可空     |
| book_cover_url   | text | http://121.196.150.190/static/book_cover/12_白夜行.jpg | 可空                 |

## 4 安卓程序activity说明

![android activity](https://raw.githubusercontent.com/mele-y/MDpic/main/android_activity.png)

## 5 页面功能说明

### 5.1 登录页面设计
#### 功能1用户验证：
		  输入：用户名、密码
	   	  输出：1重定向图书列表页面
				2 提示登录错误类型
	      说明：提示错误类型用户不存在，密码错误		

#### 功能2 跳转人脸登录页面
#### 功能3 跳转注册页面

### 5.2 人脸登录页面设计
	输入:用户照片,jpg格式
	输出：1 重定向图书列表页面
		 2 错误类型
### 5.3 注册页面设计 
    输入：用户名，密码，照片
    输出：1注册成功重定向登录页面
          2 注册失败提示错误类型
### 5.4 图书列表页面
#### 功能1：登录成功初始化:
    输入：显示图书数目
    输出：第一页图书信息
    说明：
#### 功能2：下滑更新
    输入：下拉动作
    输出：更新图书列表
    说明：
#### 功能3：下载图书
    说明：点击图书可选择下载至本地
#### 功能4：搜索
    说明：输入图书信息，查询相关图书，更新图书列表
#### 功能5：重定向至上传页面

### 5.5 上传图书页面
    输入:书名，作者，出版商，出版时间，书籍说明，本地文件
    输出：上传成功或失败
    说明：书名和本地文件不可空，其他可空

## 6 接口设计
### 6.1 登录接口

- 请求地址：http://121.196.150.190/login/

- 请求方式：post

- 请求body参数

  | 参数     | 类型   | 说明 | 是否必填 |
  | -------- | ------ | ---- | -------- |
  | usesname | string |      | Y        |
  | password | string |      | Y        |

- 返回body参数说明

  格式统一为json

  | 参数   | 说明                                            |
  | ------ | ----------------------------------------------- |
  | status | 1说明登录成功，2说明用户名不存在，3说明密码错误 |
  | msg    |                                                 |
  | data   | 第一页的书籍信息                                |

  

- 返回参数示例

  ```
  {
     "status":"1",
     "msg":"login success",
     "data":[{"book_id":"1",
       "book_name":"1984",
       "author":"George Orwel",
       "publication":"",
       "book_description":"famous book",
       "publish_date":"2010-04-15"
       "upload_date":"2020-11-30 11:29"
     },{
     "book_id":"2",
     ......
     }
     ]
  }
  {
     "status":"2",
     "msg":"user does not exist",
     "data":[]
  }
  {
  "status":"3",
  "msg":"password error",
  "data":[]
  }
  ```

### 6.2 注册接口

- 请求地址 http://120.196.150.190/register

- 请求方式 POST

- 请求body参数

  | 参数名     | 类型   | 说明 | 是否必填 |
  | ---------- | ------ | ---- | -------- |
  | username   | string |      | Y        |
  | password   | string |      | Y        |
  | user_photo | file   |      | Y        |
- 返回body参数
    |参数|说明|
    | --- | --- |
    | code|0表示username已存在,1表示注册成功，2表示注册失败  |
    |msg||


### 6.3 人脸登录接口

### 6.4 分页显示接口
### 6.5 查询接口
- 请求地址 http://121.196.150.190/query

- 请求方式 GET

- 请求body参数

| 参数名      | 类型   | 说明 | 是否必填 |
| ----------- | ----- | ---- | -------- |
|  info       |string |      | Y        |
|  page       |  int  |      | Y        |
|  category   |string |      | Y        |
info和category不能为空 可以为""
- 返回参数说明

  格式统一为json

  | 参数   | 说明                                            |
  | ------ | ----------------------------------------------- |
  | code   | 1说明查询成功，0说明未查询到书籍                   |
  | msg    | 1"query success" 2"cant find any books"          |
  | page   | 当前页数                                         |
  | pages  | 查询到的书籍有多少页                              |
  | data   | 当前页数的书籍信息                                |

  
- 返回参数示例

  ```
  {
     "code":1,
     "msg":"query success",
     "page":1,
     "pages":1,
     "data":[{"book_id":"1",
       "book_name":"1984",
       "author":"George Orwel",
       "publication":"",
       "book_description":"famous book",
       "publish_date":"2010-04-15"
       "upload_date":"2020-11-30 11:29"
     },{
     "book_id":"2",
     ......
     }
     ]
  }
  {
     "code":0,
     "msg":"cant find any books",
     "page":1,
     "pages":0,
     "data":[]
  }
  ```
  
### 6.6 下载接口

- 请求地址:http://121.196.150.190/download_book/

- 请求方式：GET

- URL请求参数:

  | 参数名  | 类型 | 说明 | 是否必填 |
  | ------- | ---- | ---- | -------- |
  | book_id | int  |      | Y        |

  

### 6.7 上传接口
- 请求地址:http://121.196.150.190/upload_book

- 请求方式: POST

- 请求body参数

  | 参数名       | 类型   | 说明 | 是否必填 |
  | ------------ | ------ | ---- | -------- |
  | book_name    | string |      | Y        |
  | author       | string |      | N        |
  | publication  | string |      | N        |
  | description  | string |      | N        |
  | book_file    | file   |      | Y        |
  | publish_date | string |      | N        |
  | book_cover   | file   |      | N        |

- 返回body参数

  | 参数名 | 类型   | 说明                         |
  | ------ | ------ | ---------------------------- |
  | code   | int    | 1表示上传成功，0表示上传失败 |
  | msg    | string |                              |

  

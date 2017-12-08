# 阿里云OSS上传DEMO使用说明

## 1. 在resources目录新建一个文件 aliyun-oss.properties ，参考内容：

```properties
oss-aliyun.accessKey=accesskey
oss-aliyun.secretKey=secretkey
oss-aliyun.bucket=bucketname
oss-aliyun.endPoint=oss-cn-shenzhen.aliyuncs.com
```

## 2. 启动 AliyunOssDemoApplication 

访问 [http://localhost:8080](http://localhost:8080)

## Notes

### 前端页面在 resources/static/index.html

### 接口实现在 FileController.java

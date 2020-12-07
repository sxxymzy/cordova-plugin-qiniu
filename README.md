## 支持平台

* Android

## 插件使用

```javascript

//上传参数
const filePath = "/storage/emulated/0/Android/data/io.hello.cordova.cache/2013424231.jpg";	// local file path
const remoteKey = "prefix/io.hello.cordova.cache/2013424231.jpg"; // remote file key
const token = "aTibxa7eU0wyp0ZYzDfxHksTvk9wor0I1DKgEwp1:mN-DVcJD9vZRD-yrAZ4FX6Fm9L8=:eyJzY29wZSI6InRlc3="; // up token
const callbacks = {
	successCbk: () => {
		// upload complete ...
	},
	progressCbk: () =>{
		// upload progress handle...
	},
	errorCbk: () => {
		// error...
	}
};

const options = {
	params: {}, // Map<String, String> 自定义变量，key 必须以 x: 开始
	mimeType: "", // String 指定文件的 mimeType
	checkCrc: true,
    isCancelled: () => false  //取消上传，当 isCancelled() 返回 true 时，不再执行更多上传
}

//上传文件
navigator.qiniu.upload(filePath, remoteKey, token, callbacks, options);

```

关于文件完整路径，代表的是一个Anrdoid资源路径
。
cordova 部分插件返回的 FILE_URL 是

```
file:///storage/emulated/0/Android/data/io.hello.cordova.cache/2013424231.jpg
```

这种情况下，插件内部已经进行处理过 依然能够正常上传

当文件上传成功之后会返回文件的完整名称。

## 演示 [点击](http://oct8d1mqf.bkt.clouddn.com/qiniuDemo.mp4)

七牛空间:
![image](http://oct8d1mqf.bkt.clouddn.com/2016-09-01-14%3A10%3A10.jpg)

七牛没有提供下载的 SDK，因为文件下载是一个标准的 HTTP GET 过程。开发者只需理解资源 URI 的组成格式即可非常方便的构建资源 URI，并在必要的时候加上[下载凭证](http://developer.qiniu.com/article/developer/security/download-token.html)，即可使用 HTTP GET 请求获取相应资源。

## 演示项目下载

http://o7k7yxkn2.bkt.clouddn.com/qiniu.zip

需要自己配置 UploadToken
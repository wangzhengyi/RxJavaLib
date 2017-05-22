# Android Retrofit源码解析

Retrofit是Square公司推出的HTTP框架，主要用于Android和Java。

# 基本用法

Retrofit将HTTP API转换为Java接口.
```java
public interface GitHubService {
    @GET("user/{user}/repos")
    Call<List<Repo>> listRepos(@Path("user") String user);
}
```

使用方式:

```java
Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
GitHubService service = retrofit.create(GitHubService.class);
```

使用GitHubService获取一个Call对象，用来发送同步或异步的HTTP请求.
```java
Call<List<Repo>> repos = service.listRepos("octocat");
```

# 源码分析


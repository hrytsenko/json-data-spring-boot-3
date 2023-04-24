[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=hrytsenko_json-data-spring-boot&metric=alert_status)](https://sonarcloud.io/dashboard?id=hrytsenko_json-data-spring-boot-3)
[![](https://jitpack.io/v/hrytsenko/json-data-spring-boot.svg)](https://jitpack.io/#hrytsenko/json-data-spring-boot-3)

# JSON Data for Spring Boot 3

This library enables [json-data] for [Spring Boot] including serialization, validation, transformation and error handling.

```java
@EnableFeignClients
@SpringBootApplication
@FieldDefaults(level = AccessLevel.PRIVATE)
class Demo {

  public static void main(String[] args) {
    SpringApplication.run(Demo.class, args);
  }

  @RestController
  @AllArgsConstructor
  static class GithubController {

    static final JsonMapper<Response> TO_RESPONSE = JsonMapper.create(Response.PROJECTION, Response::new);

    GithubClient githubClient;

    @PostMapping(
        value = "/list-repositories",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ValidatePayload(Request.SCHEMA)
    @PropagateFailure("CANNOT_LIST_REPOSITORIES")
    Response listRepositories(@RequestBody Request request) {
      return TO_RESPONSE.map(githubClient.listRepositories(request.getOwner()));
    }

    @FeignClient(name = "github-client", url = "https://api.github.com/")
    interface GithubClient {

      @GetMapping(
          value = "/users/{owner}/repos",
          produces = MediaType.APPLICATION_JSON_VALUE)
      List<JsonBean> listRepositories(@PathVariable("owner") String owner);

    }

  }

  @Bean
  CorrelationSource braveSource(Tracer tracer) {
    return () -> tracer.currentSpan().context().traceId();
  }

  static class Request extends JsonEntity<Request> {

    static final String SCHEMA = """
        {
          "required": ["owner"]
        }
        """;

    String getOwner() {
      return getString("owner");
    }

  }

  static class Response extends JsonEntity<Response> {

    static final String PROJECTION = """
        [
          {
            "operation": "shift",
            "spec": {
              "*": {
                "name": "repositories[].name"
              }
            }
          }
        ]
        """;

  }

}
```

Use `ValidatePayload` to validate requests and messages.
Use `ServiceException` and `PropagateFailure` to produce error responses.
Use `CorrelationSource` to enable correlations for error responses.

[json-data]: https://github.com/hrytsenko/json-data
[Spring Boot]: https://spring.io/projects/spring-boot

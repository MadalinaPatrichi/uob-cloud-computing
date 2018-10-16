# app server

This app serves a basic "ToDo" application which allows a user to manage a list of tasks and tick them off one by one.

**Tech Stack**:

- Spring Boot (Java)
- NodeJS
- Vue.js
- Gradle to stick it all together

The app is not intended to be a brilliant example of either Spring Boot or Vue.js but is meant to simply be the example application that is deployed and managed in this tutorial content.

## Development

**Note**: make sure you have the JDK installed on your system! (at least version 8)

**Note**: if your network requires a proxy, see the proxy section later in this document

Ensure your gradle environment is set up:

```
$ cd uob-cloud-computing/api/
$ ./gradlew
```

Run the tests

```
$ ./gradlew test
```

Build the jar

```
$ ./gradlew build
```

Run the server

```
$ java -jar build/libs/uob-todo-app-0.1.0.jar
```

You should not be able to navigate to `localhost:8080` and see the landing page or `localhost:8080/api/todos` and see the API responding.

### Proxy

If you're on a network that requires a network proxy, do the following:

```bash
$ mkdir -p ~/.gradle/
$ cat << EOF > ~/.gradle/gradle.properties
systemProp.http.proxyHost=hostname-or-ip
systemProp.http.proxyPort=port
systemProp.https.proxyHost=hostname-or-ip
systemProp.https.proxyPort=port
EOF
```

and

```bash
$ cat << EOF > ~/.npmrc
proxy=http://proxy-host:port
https-proxy=http://proxy-host:port
EOF
```

You also need to tell Docker about your proxy if you intend to use Docker

If using OAuth security and the server is running behind a proxy, you should also configure the following properties at runtime:

```
-Dsecurity.oauth2.httpProxy.host=proxy-host
-Dsecurity.oauth2.httpProxy.port=port
```

These allow the server to check the OAuth2 tokens.

### Development Running

```
$ ./gradlew bootRun
```

If you want to hot-reload the webpack content, execute the following in another terminal:

```
$ ./gradlew -t webpack
```

This will hot recompile the webpacked javascript from `src/main/js`.

### Development Database

By default this will use an in memory database that will be dropped immediately when the application closes. If you wish to use a Mysql database, you can do the following:

```
$ docker run --rm -d \
    -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD=secret \
    -e MYSQL_DATABASE=uob \
    -v mysqldata:/var/lib/mysql \
    --name mysql \
    mysql:8
```

And then launch the application Jar as follows:

```
$ java \
    -Dspring.datasource.url=jdbc:mysql://localhost:3306/uob \
    -Dspring.datasource.username=root \
    -Dspring.datasource.password=secret \
    -jar build/libs/uob-todo-app-0.1.0.jar
```

To access the mysql database you can do the following:

```
$ docker exec -ti mysql mysql -hlocalhost -p3306 -uroot -psecret
```

### OAuth2 Security

To secure the app and its API from nefarious use you should enable and configure the Oauth2 security:

```
spring.profiles.active=secured
security.oauth2.client.clientId=[client id]
security.oauth2.client.clientSecret=[client secret]
```

And override the following to use anything other than github:

```
security.oauth2.client.accessTokenUri=https://github.com/login/oauth/access_token
security.oauth2.client.userAuthorizationUri=https://github.com/login/oauth/authorize
security.oauth2.client.clientAuthenticationScheme=form
security.oauth2.resource.user-info-uri=https://api.github.com/user
security.oauth2.resource.prefer-token-info=true
```

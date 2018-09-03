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
systemProp.http.proxyPort=80
systemProp.https.proxyHost=hostname-or-ip
systemProp.https.proxyPort=80
EOF
```

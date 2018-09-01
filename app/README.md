# api server

### Development

**Note**: make sure you have the JDK installed on your system! (at least version 8)

**Note**: if your network requires a proxy, see the proxy section later in this document

Ensure your gradle environment is set up:

```
$ cd uob-cloud-computing/api/
$ ./gradlew
```

Build the jar

```
$ ./gradlew build
```

Run the server

```
$ java -jar build/libs/uob-todo-app-0.1.0.jar
```

You should now be able to curl `localhost:8080/todo` and see the API responding.

#### Proxy

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

FROM openjdk:8 

WORKDIR /app
RUN apt-get update; apt-get install zip
# installing sdk and gradle
RUN curl -s "https://get.sdkman.io" | bash
RUN /bin/bash -c "source /root/.sdkman/bin/sdkman-init.sh && sdk install gradle 4.10 "

COPY ./ /app/
RUN /bin/bash -c "/root/.sdkman/candidates/gradle/current/bin/gradle wrapper"
RUN ./gradlew build

ENTRYPOINT ["java", "-jar", "build/libs/uob-todo-app-0.1.0.jar"]



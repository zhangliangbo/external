# external
external call wrapper, like git,scoop,docker,kube,ffmpeg,...

1. use `commons-exec` to run command
2. use `graalvm` to generate native image

# build step
```
./mvnw clean package -DskipTests
```
```
java -agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image -jar target/external-1.0-SNAPSHOT-jar-with-dependencies.jar -i xxx.xxx[xxx,xxx]
```
```
./mvnw clean native:compile -Pnative -DskipTests
```
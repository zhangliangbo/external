.\mvnw clean native:compile -Pnative -DskipTests
Move-Item -Path .\target\external.exe -Destination D:\scoop\shims -Force
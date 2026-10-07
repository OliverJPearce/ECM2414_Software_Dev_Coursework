## Compiling and Running The Tests

JUnit 4.13.2

To run tests first run:
```bash
javac -cp ".;lib/junit-platform-console-standalone-6.1.3.jar" *.java
```

and then run:
```bash
java -jar lib/junit-platform-console-standalone-6.1.3.jar execute --class-path . --scan-class-path
```
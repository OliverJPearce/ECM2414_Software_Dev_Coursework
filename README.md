## Compiling and Running The Tests

The tests are done using JUnit 5 (Version 6.1.3)

To compile the tests first run:
```bash
javac -cp ".;lib/junit-platform-console-standalone-6.1.3.jar" *.java
```

Then to run the compiled tests use:
```bash
java -jar lib/junit-platform-console-standalone-6.1.3.jar execute --class-path . --scan-class-path
```
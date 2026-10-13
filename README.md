## Compiling and Running The Tests

The tests are done using JUnit version 4.13.2

To compile the tests first run:
```bash
javac -cp ".;lib/junit-platform-console-standalone-6.1.3.jar" *.java
```

Then to perform the compiled tests run:
```bash
java -jar lib/junit-platform-console-standalone-6.1.3.jar execute --class-path . --scan-class-path
```
## Compiling and Running The Tests

The tests use JUnit 5 (Version 6.1.3) with JDK 25

To compile the tests, first run the command below in the same directory as CardGameTest.java:
```bash
javac -cp ".;lib/junit-platform-console-standalone-6.1.3.jar" *.java
```

Then to run the compiled tests use the command below in the same directory as the previous command:
```bash
java -jar lib/junit-platform-console-standalone-6.1.3.jar execute --class-path . --scan-class-path
```
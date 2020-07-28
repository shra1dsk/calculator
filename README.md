# calculator
Command line calculator

This project evaluates expressions in a very simple integer expression language. The program takes an input on the command line, computes the result, and prints it to the console.  
For example:

Eg1: java -jar target/calculator-1.0-SNAPSHOT-jar-with-dependencies.jar "let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))"
40

Eg2: java  -jar target/calculator-1.0-SNAPSHOT-jar-with-dependencies.jar "add(1, 2)"
3


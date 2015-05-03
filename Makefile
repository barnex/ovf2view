all:
	astyle --style=java --indent=tab *.java
	javac *.java
	gcj -o Main --main=Main *.java

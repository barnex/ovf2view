all:
	javac *.java
	gcj -o Main --main=Main *.java

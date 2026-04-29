all: jaylib-5.5.0-2-shadow.jar lombok-1.18.46.jar snakeyaml-2.6-shadow.jar

CENTRAL = https://repo1.maven.org/maven2

jaylib-5.5.0-2-shadow.jar:
	curl -o $@ ${CENTRAL}/uk/co/electronstudio/jaylib/jaylib/5.5.0-2/jaylib-5.5.0-2.jar
snakeyaml-2.6-shadow.jar:
	curl -o $@ ${CENTRAL}/org/yaml/snakeyaml/2.6/snakeyaml-2.6.jar
lombok-1.18.46.jar:
	curl -o $@ ${CENTRAL}/org/projectlombok/lombok/1.18.46/lombok-1.18.46.jar

clean:
	rm -f *.jar

.PHONY: all clean

# Usage:
# make all - build dev jar
# make run - build and run dev jar
# make package - create production build (zip, tar.gz, tar.xz, 7z) (out/game-version.type)
# make fatjar - create production jar (one fat jar with all the resources) (out/game-version-fat.jar)
# make clean - removes the out dir

VERSION ?= 0.0.6

# all ys for package
# all ns for fatjar

# y - resources are in ./ (resources for cwd)
# n - bundled resources
LOCAL_RESOURCES ?= y
# y - libs are next to jar (../libs for jar)
# n - bundled libs
# unused in runtime
LOCAL_LIBS ?= y
LIBS_RUNTIME ?= ../
# y - data is always on ./
# n - search for runtime directory
LOCAL_DATA ?= y

JAVA ?= java
JAVAC ?= javac
JAR ?= jar

MAIN_CLASS = Entrypoint

JAVAC_ARGS ?=
JAVA_ARGS ?=

SRC := $(shell find src -type f -name '*.java')

OUT ?= out
CLASSES ?= ${OUT}/classes
OUTJAR ?= ${OUT}/game-${VERSION}.jar
MANIFEST ?= ${OUT}/manifest.mf

JAVAC_ARGS += -proc:full
JAVAC_ARGS += -cp "$(shell echo $(wildcard libs/*.jar) | tr ' ' ':')"
SHADOW_LIBS = $(wildcard libs/*-shadow.jar)

LOCAL_RUNTIME_FILE = .game-runtime

ASSETS = LICENSE
RESOURCES = resources/*

ifeq ($(LOCAL_RESOURCES),n)
ASSETS += resources/*
endif

PWD := $(shell pwd)

all: $(LOCAL_RUNTIME_FILE) $(OUTJAR)
run: all
	$(JAVA) $(JAVA_ARGS) -jar $(OUTJAR)

$(MANIFEST):
	echo 'Manifest-Version: 1.0' > $@
	echo 'Main-Class: $(MAIN_CLASS)' >> $@
ifeq ($(LOCAL_LIBS),y)
	echo 'Class-Path: $(addprefix $(LIBS_RUNTIME), $(SHADOW_LIBS))' >> $@
endif
	echo 'Foo: Bar' >> $@
	echo 'Jjjjjjjjjjjjj: jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj' >> $@
	echo 'License: GLWTSPL (Good Luck With That Shit Public License)' >> $@
	echo 'Source-Code: https://github.com/actualsuck/my-game' >> $@
	echo 'Jjjjjjjjjjjjjjjj: jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj' >> $@
	echo 'Game-Version: ${VERSION}' >> $@
	echo 'Local-Resources: ${LOCAL_RESOURCES}' >> $@
	echo 'Local-Libraries: ${LOCAL_LIBS}' >> $@
	echo 'Local-Data: ${LOCAL_DATA}' >> $@
	echo 'Java-Version: 21' >> $@

libs:
	$(MAKE) -C $@

$(OUTJAR): libs $(CLASSES) $(MANIFEST)
	mkdir -p $(dir $@)
	cp -r $(ASSETS) $(CLASSES)/
	$(JAR) --create --manifest "$(MANIFEST)" --file "$@" -C $(CLASSES) .

$(CLASSES): $(SRC)
	mkdir -p $@
	$(JAVAC) $(JAVAC_ARGS) -d $@ $^
ifeq ($(LOCAL_LIBS),n)
	cd $@ && \
	for i in $(addprefix $(PWD)/, $(SHADOW_LIBS)); do \
    	$(JAR) -xf $$i; \
	done
	rm -r $@/META-INF
endif

RUNTIME_FILE = $(OUT)/runtime-file

$(RUNTIME_FILE):
	mkdir -p $(dir $@)
	echo "local-resources=$(LOCAL_RESOURCES)" > $@
	echo "local-libs=$(LOCAL_LIBS)" >> $@
	echo "local-data=$(LOCAL_DATA)" >> $@

$(LOCAL_RUNTIME_FILE): $(RUNTIME_FILE)
	cp $^ $@

OUTPACKAGE ?= $(OUT)/package

$(OUTPACKAGE): clean
	mkdir -p $(dir $@)
	@make LOCAL_DATA=y LOCAL_RESOURCES=y LOCAL_LIBS=y LIBS_RUNTIME=./ $(OUTJAR) $(RUNTIME_FILE)
	mkdir -p $@/data $@/libs
	cp -r LICENSE $(OUTJAR) resources $@
	cp libs/*.jar $@/libs
	cp $(RUNTIME_FILE) $@/.game-runtime

$(OUT)/game-${VERSION}.zip: $(OUTPACKAGE)
	cd $<; zip -r9 $(PWD)/$@ .
$(OUT)/game-${VERSION}.tar.xz: $(OUTPACKAGE)
	cd $<; tar -cf $(PWD)/$@ -I 'xz -9e' .
$(OUT)/game-${VERSION}.tar.gz: $(OUTPACKAGE)
	cd $<; tar -cf $(PWD)/$@ -I 'gzip -9' .
$(OUT)/game-${VERSION}.7z: $(OUTPACKAGE)
	cd $<; 7z a -t7z -mx=9 $(PWD)/$@ .

package: $(OUT)/game-${VERSION}.zip \
	$(OUT)/game-${VERSION}.7z \
	$(OUT)/game-${VERSION}.tar.gz \
	$(OUT)/game-${VERSION}.tar.xz
fatjar: clean
	$(MAKE) LOCAL_DATA=n LOCAL_RESOURCES=n LOCAL_LIBS=n LIBS_RUNTIME=../ $(OUTJAR)
	mv $(OUTJAR) $(OUT)/game-${VERSION}-fat.jar

clean:
	rm -rf $(OUT)/
	$(MAKE) -C libs clean

.PHONY: clean all run package fatjar libs

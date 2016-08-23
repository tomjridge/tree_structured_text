SHELL:=bash

all:
	sbt compile run

clean:
	sbt clean

unsafe_clean:
	rm -rf project target

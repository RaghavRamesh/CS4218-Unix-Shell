# CS4218 Shell #

CS4218 Shell is a command interpreter that provides a set of tools (applications): *cd*, *pwd*, *ls*, *cat*, *echo*, *head*, *tail*, *grep*, *sed*, *find* and *wc*. Apart from that, CS4218 Shell is a language for calling and combining these application. The language supports *quoting* of input data, *semicolon operator* for calling sequences of applications, *command substitution* and *piping* for connecting applications' inputs and outputs, *IO-redirection* to load and save data processed by applications from/to files. More details can be found in "Project Description.pdf" in IVLE.

## Prerequisites ##

CS4218 Shell requires the following versions of software:

1. JDK 7
2. Eclipse 4.3
3. JUnit 4

Compiler compliance level must be <= 1.7

## Implementation ##

CS4218 Shell implementation has the following structure:

`sg.edu.nus.comp.cs4218` is the main package. It provides basic interfaces (`Application`, `Command`, `Shell`) and `Environment` class that is used to manage current working directory. A `Shell` implementation works the following way: it parses and expands user's command line producing an instance of `Command` class. For example, parsing `cat * | grep foo` can yield the following structure: `Pipe(Call(Cat(), ["a.txt", "b.txt"]), Call(Grep(), ["foo"]))`. After that, shell calls `Command.evaluate` method. An implementation of `Command` interface is responsible for calling applications in separate threads and supplying proper input/output streams. Implementations of `Application` interface provide application-specific functionality.

`sg.edu.nus.comp.cs4218.exception` contains definitions of the exceptions that can be thrown by shell and applications. `ShellException` is thrown when an error occurred during the execution of shell's functionality such as command line parsing. For each application, there is a separate exception that is inherited from `AbstractApplicationException` class.

Package `sg.edu.nus.comp.cs4218.impl` contains implementation classes. Implementation of applications is in `sg.edu.nus.comp.cs4218.impl.app` package, inplementation if shell commands is in `sg.edu.nus.comp.cs4218.impl.cmd`.

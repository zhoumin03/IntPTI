# IntPTI
Integer error fixing by proper-type inference

Updates
-------
* 2017-6-30 Added updated experimental results on `IntPTI/data/ase17tool/`.
* 2017-6-29 Added [wiki pages](https://github.com/45258E9F/IntPTI/wiki) for IntPTI including the configuration guide and quick help for web interface.
* 2017-6-29 Interactive mode is now available and users can review and manage generated fixes via a user-friendly web interface. To use this feature users should insert `phase.repair.interactive = true` in the phase configuration if it is missing.
* 2017-6-29 We have uploaded [a demonstration video](https://youtu.be/9Tgd4A_FgZM) to showcase the usage and capabilities of IntPTI. Welcome to view our demonstration and leave your comments.

Introduction
------------
IntPTI is a tool for automatic integer error (e.g. overflow, sign error) fixing. For now it only supports C programs. The input of IntPTI is a preprocessed C source file or a project after build capture (which will be introduced later), and the output is the modified source files applied with fixes. IntPTI is based on [CPAchecker](https://github.com/sosy-lab/cpachecker) and we have implemented a configurable phase system to schedule multiple static analysis tasks. 

IntPTI is also the prototype from the paper "Fixing Integer Errors by Proper-Type Inference". Generally, for each variable or expression, IntPTI tries to infer a type that can represent all its possible values. Values are derived by static value analysis, while types are derived by solving constraints generated in type inference.

Prerequisite
------------
[Z3](https://github.com/Z3Prover/z3): >= 4.5.0, because IntPTI relies on the specific format of model for partial weighted MaxSMT problem.

JDK: >= 7

[Apache Ant](http://ant.apache.org/): this is the build system used by IntPTI

Building
--------
Enter the project directory and execute the following commands:

    ant
    ant jar
    
The first command builds the whole project and automatically downloads dependencies. The second command builds three `*.jar` files. `TsmartBuild.jar` is used for build capture and `TsmartAnalyze.jar` is the entry of our tool.

Configuration
-------------
The configuration files used by IntPTI are located in `IntPTI/config/fix_top/`. The entry configuration is [`top.properties`](https://github.com/45258E9F/IntPTI/blob/master/config/fix_top/top.properties), and the first line is as follows.

    phase.manager.config = ...
    
User should fill the absolute path of phase configuration. For benchmark evaluation, we should choose [`top.config.190`](https://github.com/45258E9F/IntPTI/blob/master/config/fix_top/top.config.190), [`top.config.197`](https://github.com/45258E9F/IntPTI/blob/master/config/fix_top/top.config.197) or [`top.config.680`](https://github.com/45258E9F/IntPTI/blob/master/config/fix_top/top.config.680) (Here, `*.197` and `*.680` are used for CWE 197 and 680, respectively. `*.190` is used for other benchmark cases), and change the name of [`rangeAnalysis.properties.bench`](https://github.com/45258E9F/IntPTI/blob/master/config/fix_top/rangeAnalysis.properties.bench) to `rangeAnalysis.properties`. For realistic applications, we choose [`top.config.real`](https://github.com/45258E9F/IntPTI/blob/master/config/fix_top/top.config.real) or [`top.config.real.nomain`](https://github.com/45258E9F/IntPTI/blob/master/config/fix_top/top.config.real.nomain) for projects with no main function, then change the name of [`rangeAnalysis.properties.real`](https://github.com/45258E9F/IntPTI/blob/master/config/fix_top/rangeAnalysis.properties.real) to `rangeAnalysis.properties`.

To specify the call depth of each call graph component for multi-entry analysis, we need to modify the following option in `rangeAnalysis.properties`.

    cpa.boundary.callDepth = 0
    
 `cpa.boundary.callDepth = N` if maximum depth of each call graph component is N.
 
 Usage
 -----
 
 **1.** __Build capture__
 
 This step is applicable for analyzing a project. Given a project, we first invoke the following command line to generate the Makefile.
 
     ./configure
        
 Then, we use `TsmartBuild.jar` to perform build capture.
 
     java -jar TsmartBuild.jar -shell=/bin/bash make
      
 Build capture tool captures the command lines in Makefile by their execution sequence, then we add `-E` to make `gcc` stop after preprocessing and replace the output file `*.o` to `*.i`. Files that are finally compiled to an executable or a library are organized as a task, which is the direct input of `TsmartAnalyze.jar`. The output of build capture tool is a `.process_makefile` folder in the  Makefile project directory with preprocessed files organized as tasks.
 
 **2.** __Run integer error fixing__
 
 (1) To fix one or more source files, we invoke the following command.
 
     java -jar TsmartAnalyze.jar -root=[root dir] -manual [source files]
     
 `root dir` is the directory of IntPTI project. We can append one or more file names after `-manual` and two consecutive file names are split by a space.
 
 (2) To fix a task generated by build capture, we invoke the following command.
 
     java -jar TsmartAnalyze.jar -root=[root dir] -captured=[*/.process_makefile] [-task=N]
 
 The optional argument `-task=N` tells IntPTI to analyze the task N only. Without this argument, all tasks are analyzed.
 
 (3) To fix a bunch of preprocessed source files possibly without the main function, we invoke the following command to make IntPTI work under assembled mode.
 
     java -jar TsmartAnalyze.jar -root=[root dir] -assembled=[folder of source files]
 
 **3.** __Use sanity check library__
 
 Enter [`IntPTI/src/org/sosy_lab/cpachecker/core/phase/fix/lib/`](https://github.com/45258E9F/IntPTI/tree/master/src/org/sosy_lab/cpachecker/core/phase/fix/lib) and install the sanity check library `libTsmartIntFix` by invoking the following commands.
 
     make
     make install
     
 Invoking `make uninstall` will uninstall the library.
 
 Benchmarks
 ----------
 The benchmarks we used are all available on the Internet.
 
 **1.** The SAMATE benchmark: https://samate.nist.gov/SRD/testsuites/juliet/Juliet_Test_Suite_v1.2_for_C_Cpp.zip
 
 **2.** Real-world applications: we choose 7 applications and their source code is publicly available.
 
| Project    | Version |
|------------|---------|
| gzip       | 1.2.4   |
| Vim        | 7.4     |
| grep       | 2.10    |
| PostgreSQL | 9.0.15  |
| JasPer     | 1.900.5 |
| libarchive | 3.1.2   |
| OpenSSL    | 1.0.1r  |

Data Availability
-----------------
All data is available in `IntPTI/data/ase17/`.
* `cve/` contains case study results on integer bugs from CVE database.
* `cwe/` contains evaluation results on the SAMATE benchmark. `cwe/0x/` contains results produced by IntPTI under single-entry mode, while `cwe/1x/` and `cwe/2x/` contain results under multi-entry mode with 1-depth and 2-depth heuristics, respectively.
* `real.xlsx` contains the result on real-world applications.
 

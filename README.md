# SSCV

This repository is a collection of representative examples of Software Side Channel Vulnerabilities. Side channel vulnerabilities lead to a differential consumption of resource (space or time) of a software which leads to sensitive information leak.

The set was curated as part of research done for the STAC program at Iowa State University (More information on STAC - http://www.darpa.mil/program/space-time-analysis-for-cybersecurity). This research involves defining patterns in the existing side channel vulnerabilities. So far we have defined following patterns,

Taxonomy:

1) Differential Branch Point : A branch condition in the code segment which leads to two paths with different consumption of resources. Differential branch is the core cause of side channel vulnerabilities.
2) Branch Governing Loop : A branch condition in the code which decides whether a loop executes or not.
3) Library API : Any API which is not part of the code provided and the source code for it resides in a dependency (external Library).

1) Pattern 1 : Branch Governing Loop.
In this pattern the loop is supposed to cause significantly excessive consumption of resource.

2) Pattern 2 : Branch condition leading to expensive library API.
The library API involved is deemed to consume excessive resources and that makes the branch a differential branch.

3) Pattern 3 : Exception acting as a differential branch point
Code throws an exception and the path led by exception is expensive.

4) Pattern 4 : Loop termination Condition acts as a differential branch point

5) Pattern 5 : Weak side channel amplified by loop
The side channel involved is a weak i.e. the difference in resource consumption between different paths does not differ greatly/reliably. But because it is contained in a loop, the code executes repeatedly and it amplifies the difference.

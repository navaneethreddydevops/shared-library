/* Load the default configuration*/
```
@Library('shared-library')
```
/* Using a version specifier, such as branch, tag, etc */
```
@Library('shared-library@1.0')
```
/* Accessing multiple libraries with one statement */
```
@Library(['shared-library', 'jenkins-shared-library']) _
```
Dynamic retrieval
```
library identifier: 'custom-lib@master', retriever: modernSCM(
  [$class: 'GitSCMSource',
   remote: 'git@git.mycorp.com:my-jenkins-utils.git',
   credentialsId: 'my-private-key'])
```
#!/usr/bin/groovy
today = new Date()
println 'today'

// This method will read a file and iterate through it and prints line by line and line numbers
//import java.io.File --JJava package to perform operations
def number = 0
new File('learning.groovy').eachLine { line ->
number += 1
println "$number: $line"
}
//////////////////////////////////////
// Working with Lists and Maps in java requires these imports
// import java.util.List
// import java.util.Map
///////////////////////////////////////
def classes = [String, List ,File]
for (clazz in classes) {
    println clazz.'package'.name
}
// Simple way of writing
println ([String, List, File].'package'.name)

//Working on xml files with groovy
def customers = new XmlSlurper().parse(new File('customers.xml'))
for (customer in customers) {
    println "${customer.@name} works for ${customer.@company}"
}

//Kind of ls command in shell script(groovyConsole)
//groovy -e "new File('.').eachFileRecurse { println it }"
//Java way of writing same code
// public class ListFiles{
//     public static void main(String[] args) {
//         new java.io.File(.).eachFileRecurse {
//             new FileListener(){
//                 public void onFile(File file){
//                     System.out.println(file.toString());
//                 }
//             }
//         };
//     }
// }
// Fibonacci.groovy
def current = 1
def next = 1
10.times {
    print current + ' '
    newCurrent = next
    next = next + current
    current = newCurrent
}
println ''

// Compiling Groovy code this will generate the classfiles in classes directory
groovyc -d classes learning.groovy

// Groovy always import these packages by default
import groovy.lang.*,
import groovy.util.*,
import java.lang.*,
import java.util.*,
import java.net.*,
import java.io.*

//Asserions These are used for conditions checks or logic check
assert(true)
assert 1 == 1
def x= 1
assert x == 1
def y = 1 ; assert y == 1


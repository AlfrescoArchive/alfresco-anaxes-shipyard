# Welcome

This folder contains test cases related to sample hello world application

There are two type of test

* Backend rest api test
* UI test

# Test prerequisite

In order to set up selenium grid we can use the selenium standalone Firefox docker container

Following is the docker command to get the container

docker run -d --name <name of the container> -p 4444:4444 selenium/standalone-firefox-debug

# Test run

## To run the test in minikube

     mvn test -U -Dcluster.type=minikube  -Dcluster.namespace=<namespace of cluster>  -DsuiteXml=app_suite_testng.xml

## To run the test in AWS

    mvn test -U -Dcluster.type=aws  -Dcluster.namespace=<namespace of cluster>  -DsuiteXml=app_suite_testng.xml

*** app_suite_testng.xml - contains both the test related to backend and UI

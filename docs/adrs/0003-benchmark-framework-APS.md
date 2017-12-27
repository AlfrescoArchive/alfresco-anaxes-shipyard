# 3. Benchmark Framework APS

Date: 2017-09-29

## Status

Accepted

## Context

### Why benchmark test?
In order to get a production ready deployment - [![Jira](https://img.shields.io/badge/Jira-PRIVATE-red.svg)](https://issues.alfresco.com/jira/browse/PRODDEL-208) - we need to prove that system will be up and running for set of requirement defined in REQ project. Benchmark test are only to load the system based on REQ project and validate the same.

There are two-benchmark test frameworks available
*	APS Benchmark using Jmeter – provided by SE [![APS Benchmark](https://img.shields.io/badge/APS%20Benchmark-PRIVATE-red.svg)]( https://ts.alfresco.com/share/page/site/apsbenchmark/dashboard)
*	Write a benchmark test using the Alfresco community benchmark framework.
	https://github.com/AlfrescoBenchmark/alfresco-benchmark

### Current APS Testing

The current APS regression testing uses java script for REST API testing. They mainly make use of the REST requests to create data for protractor based UI testing. Based on my discussion we understand that there is no separate RESTAPI test project in APS.

### Alfresco community benchmark framework

 The current benchmark frame has the following component  

*	Management Server for creating tests, scheduling tests, and downloading test results.
*	Driver Server(s) for executing tests (doing the actual work and calling the Alfresco servers)
*	A framework for writing load/stress tests - This can be TAS where we have built the RESTAPI request.  

### Steps for Creating and executing benchmark test

1) Register Test Suite Definition to the management server.
2) Create a test and run for the test suite.
3) Schedule a run time for running the test- if not mentioned it will run immediately.
4) Driver server will execute the test in alfresco server.
5) Record the execution time and the results in the mongo database.
6) View the results in the management server.

### Benchmark Test Driver

*	The test driver should contain details about the test scenario we want to execute in the test-context.xml
*	It should implement the event processor and load processor for executing the scenario.
*	The event processor will load the scenarios and process the steps mentioned in scenario and load the results in the mongo db.
*	The actual test scenarios can we written using TAS framework.
There are sample benchmark projects like Benchmark-claims where we can clone and create the driver for our needs.  

### TAS framework for RESTAPI

TAS is a test automation framework written in JAVA, with a built in utility to help tester to write automation testing for RESTAPI. The tests are written using simple DSL language, where in for every REST request models are created and asserted for full JSON response.  
These models can be reused for other testing such as  
*	Data preparation for UI
*	Integration testing with other apps
*	Benchmark testing

## Decision

We will use the existing Jmeter benchmark test created by SE to prove that APS is production ready .
As part of platfrom services team we are not planning to implementing new benchmark test for the deployment value bundle as it is not in scope.
In the section below we have documented what is required to implement a new benchmark test for APS based on ACS.

## Consequences

As a result of this analysis we suggest APS team to add an item in their backlog to develop
new benchmark test using the Alfresco benchmark framework.

### Suggestion / Things to do  

As existing benchmark framework is extensive used in ACS and it can easily extend to APS we would suggest to re use the framework for doing any benchmark testing. As platform of ACS and APS is using JAVA we would recommend to use TAS framework to build the API models and re use the same for performing benchmark testing.  

#### One off Task

As the current TAS is written for ACS, it would be good idea to extract common libraries so that it can be extended to APS. (Kris is creating a epic in TAS backlog).  Doing this will help for teams to use TAS for all the RESTAPI testing.  This is very much crucial for TAS adoption. As part of API guideline standard we can also emphasis on adoption TAS as standard framework for all he RESTAPI testing through out Alfresco. This will also help in unified testing framework for both APS and ACS.

#### Task for APS benchmark

There are two types of benchmark we can perform
*	UI scenario benchmark flow
*	RESTAPI benchmark

#### RESTAPI benchmark Driver

Create a new TAS based RESTAPI project for APS.
Create the following benchmark driver for APS.
*	Create a new benchmark driver for user creation / sign up like the one we have in 	ACS. It is good practice to separate user creation in a separate driver so that other 	scenarios can re used going forward.
*	Create a new benchmark driver for RESTAPI scenario identified in deploy-87 for APS.

#### UI benchmark Driver
The APS protractor UI test should be modified to contain render login to make sure the page are loaded correctly before used in for benchmark flow.
As benchmark driver is just a war file, need to write a new benchmark driver, which can use Nashorn framework to run the protractor java script page objects in java.

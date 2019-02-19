dropwizard-stress
===

How to start the stress-server application
---

1. Run `mvn verify` to build both the server and client jars
1. Switch to the server module directory: `cd stress-server`
1. Start application with `java -jar target/stress-server-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080/category`

How to test the stress-client application
---

1. Prerequisite: launch the stress-server, and keep it running (see above).
1. In a new console, switch to the client module directory: `cd stress-client`
1. Start the application with `java -jar target/stress-client-1.0-SNAPSHOT.jar check.yml`
1. Wait until the test is complete.

How to run the stress test
---

Follow the instructions for "How to test the stress-client application", but instead of using
`check.yml`, use `stress.yml`. The test will take much longer, exposing bugs that manifest under
load.

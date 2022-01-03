# trading
### Setup for local testing
1. install a local apache client like laragon or xampp
2. create a local mysql database for the project
3. configure data in application.properties or set system environment variables

### Tests
Most things in this service have tests.
The tests ending with "IntegrationTest" will fail locally.
These should only be able to run correctly with the other services deployed.

### Rest Api:
https://the-microservice-dungeon.github.io/docs/openapi/trading
### Event Api:
https://the-microservice-dungeon.github.io/docs/asyncapi/trading

# ocpp-middleware
Multi-protocol Middleware for Charging Point Infrastructure


This project is an open source ready-to-use application developed by NHP Srl.
The purpose of this application is to behave as a communication middleware between Charging Points and a third-party back-end platform.

Version 1.0 integrates OCPP 1.6 protocol in both JSON and SOAP format, but more protocols as OCPP 1.5 and OCPP 2.0 could be easily integrated thanks to application multi-interface architecture.

DEPENDENCIES
ocpp-middleware is a Java 11 maven project built on Spring 2.1.1 framework
The main external dependency is:
Java OCA OCPP V1.6 developed by ChargeTimeEU and open source on GitHub (https://github.com/ChargeTimeEU/Java-OCA-OCPP)

CONFIGURATION
In order to start the application application.yml file (in src/main/resources) has to be completed with user settings as follows:

- DB Settings: needed for datasource connection
- Server Port settings: needed to configure multiple listener (one for each protocol) on different ports
- Client URL Settings: not necessary, needed for the connection with user back-end (push messages for Authorization, Start/Stop of transaction)
- FTP Settings: not necessary, needed for default FTP file transfer with stations (diagnostics, firmware)

After configuration phase, application will be ready to start!

START UP
At startup phase, db is populated via JSON files in resources with the following:
- OCPP 1.6 Configuration keys
- One test Station Model ready to use (2 units: each unit has one Type2 Connector 22kW and one Schuko Connector)

Afterwards, new station brand and models can be created via API.

REST API
Once application is running, user can communicate with it through REST API.

All the Java objects used for communication can be found in package it.besmart.ocppLib and could be easily imported in user backend.
REST controllers used for communication are grouped as follows:

- Configuration Controllers (it.besmart.ocpp.controllers.config) for creation/update of station Brands and models
  - Brand Controller (Create/Update/Get)
  - Model Controller (Create/Update/Get)
  
- Other Controllers (it.besmart.ocpp.controllers) for operative use
  - Station Controller: for creation and update of Stations (based on Models pre-inserted characteristics), for getting all static and dynamic datas (states when are effectively connected) from Stations
  - Management Controller: for standard operations needed for Stations management (retrieve/solve errors, reset, retrieve diagnostics, update firmware, unlock connectors, change availability state of connectors, etc.)
  - Transaction Controller: for effective use of the Stations (start/stop transactions/reservations and retrieving transaction data)
  - Command Controller (v. 1.6) : for direct test of OCPP 1.6 commands on connected Stations

COMMISSIONING
Once a Station is correctly created, a unique code named evseID is associated to it. This represents the identifier for an OCPP connection.
When user switch on a Charging Point and configure it with evseID and server settings (hostname and specific protocol port) the communication with ocpp-middleware starts immediately!

ocpp-middleware store all datas (states, alerts, transactions) of the connected Stations in such a way that user can always retrieve complete information via REST API.

FUTURE DEVELOPMENTS
Our next steps will be for sure:
- Publish API documentation
- Integrate OCPP2.0 in the application


We hope this application could be a good starting point for new projects. Good luck everybody!


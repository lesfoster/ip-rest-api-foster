# IP Address Management REST API
 
Create a simple IP Address Management REST API using Spring Framework on top of any data store. It will include the ability to add IP Addresses by CIDR block and then either acquire or release IP addresses individually. Each IP address will have a status associated with it that is either “available” or “acquired”. 
 
The REST API must support four endpoint:
  * **Create IP addresses** - take in a CIDR block (e.g. 10.0.0.1/24) and add all IP addresses within that block to the data store with status “available”
    
    POST http://localhost:8080/ip/cidr/172.0.0.0/24    
  * **List IP addresses** - return all IP addresses in the system with their current status
    
    GET http://localhost:8080/ip/ip_states    
    GET http://localhost:8080/ip/ip_states_flat 
  * **Acquire an IP** - set the status of a certain IP to “acquired”
    
    PUT http://localhost:8080/ip/acquired/10.0.0.122
  * **Release an IP** - set the status of a certain IP to “available”
    
    PUT http://localhost:8080/ip/freed/10.0.0.122

Implementation Details
----------------------
* **Framework** - REST frontend, Spring Boot @Service middleware, Relational resource tier. 
* **Internal Representation** - bit sets serialized as base64-encoded text and stored in a relational database.  Storing
one bit per IP address.  Broken into blocks to keep network packet size reasonable, and to avoid
some contention for access (not tuned!).
  * 1/6 the storage per IP address compared to integer storage.  Much smaller than string
    formatted descriptions of IP addresses.
  * Proven to add less than 1 second per 200,000 complete cycles.
  * Stored in blocks of 1024 (editable) 
  * Assumption: non-inclusive IP ranges (net address and broadcast address: no .0 or .255) - (configurable)
  * IPv4 only.
* **Database Store** - JPA Repositories using H2 in-memory (can be changed to more realistic
  databases such as MySQL or PostGresQL using POM and config changes)
* **Security** - (nominal) Spring Security, Basic Authentication,  User/Pass configurable (can be
  upgraded with configurations)
* **Tests** - Coverage of internal representation (conversions, etc.)


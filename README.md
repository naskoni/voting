### Prerequisites
- Java 14 JRE or JDK installed
- MS SQL Server running, if not - the application can be started with built-in H2 in-memory DB.
You can use any DB you want, but the corresponding dependency must be added to 'pom.xml'.
 
### Environment variables
By default the H2 in-memory DB will be used. The application rely on environment variables in 
order to run with DB of your choice. These need to be set up in your environment or in the 
Run Configuration of your IDE:

- voting.db.username (default is 'sa')
- voting.db.password (default is 'sa')
- voting.db.url (default is jdbc:h2:mem:test') (for mssql docker image - jdbc:sqlserver://localhost)
- voting.db.driver(no default) (for mssql - com.microsoft.sqlserver.jdbc.SQLServerDriver)
- voting.db.platform(default is 'h2') (for mssql - mssql)
- voting.ddl (default is 'create')

The same environment variables must be set if you want to run the tests with MS SQL Server.
If not - they will run with H2.

### Requests
When starting the application, the database will be populated with one record with id 1 and requests
can be made against the REST API:
- POST localhost:8080/voting/posts/{postId}/votes/positive
- POST localhost:8080/voting/posts/{postId}/votes/negative
- GET localhost:8080/voting/posts/{postId}/votes
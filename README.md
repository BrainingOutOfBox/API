# Methode 635 API

This is the Methode 635 API. It is based on a starter application that shows how Play works.  Please see the documentation at https://www.playframework.com/documentation/latest/Home for more details.

## Running

Run this using [sbt](http://www.scala-sbt.org/).  

```
sbt run 40000
```

And then go to http://localhost:40000 to see the running web application.

## Controllers

There are several controllers in this project.

- FindingController.java:

  Handles all requests for BrainstormingFindings.

- ParticipantController.java:

  Handles all requests for Participants.

- TeamController.java:

  Handles all requests for BrainstormingTeams.
  
- FileController.java:

  Handles all requests for Files.
  
- PatternController.java:

  Handles all requests for PatternIdeas.

## Components

- FindingService.java:

  Contains all business logic for the correct processing of the BrainstormingFindings.

- ParticipantService.java:

  Contains all business logic for the correct processing of the Participants.

- TeamService.java:

  Contains all business logic for the correct processing of the BrainstormingTeams.
  
- FileService.java:

  Contains all business logic for the correct processing of files.
  
- PatternService.java:

  Contains all business logic for the correct processing of the PatternIdeas.
  
  
- MongoDBFindingService
  
  Enables connection to the mongodb database for BrainstormingFindings.
  
- MongoDBParticipantService

  Enables connection to the mongodb database for Participants.
  
- MongoDBTeamService

  Enables connection to the mongodb database for BrainstormingTeams.

- MongoDBFileService

  Enables connection to the mongodb database for Files.
  
- MongoDBPatternService

  Enables connection to the mongodb database for PatternIdeas.

## Parsers
The different parsers exptect JSON and generate the appropriate DTO from it.

- BrainstormingFindingDTOBodyParser.java
- BrainsheetDTOBodyParser.java
- BrainstormingTeamDTOBodyParser.java
- ParticipantDTOBodyParser.java
- MultipartFormDataBodyParser.java
- PatternIdeaDTOBodyParser.java


## Filters

- JWTFilter.java

  A simple filter that checks the jwt token.
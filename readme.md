# Goblin Dependency Miner
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE.txt)

This project allows you to generate and update a Maven Central dependency graph in a Neo4j database.

A weaver used to enrich queries is available here: https://github.com/Goblin-Ecosystem/goblinWeaver.  
A Zenodoo archive that contains the generated dataset dump and the Weaver jar is available here: https://zenodo.org/records/10291589.

If you use the dataset dump present in Zenodo, please use a Neo4j version 4.x.

The times shown in this document have been realized by a machine with the following characteristics:
> OS: Red Hat Enterprise Linux <br>
> OS version: 8.7 <br>
> 16 CPUs:  Intel(R) Xeon(R) CPU E7-8880 v4 @ 2.20GHz <br>
> Memory: 64 GB <br>

## Requirements
- Java 17
- Maven, with MAVEN_HOME defines

### Maven Central Index
To get all Maven releases data, we use the Central index archive here: https://repo.maven.apache.org/maven2/.index/nexus-maven-repository-index.gz  
Initially, this program will download the most recent archive and unpack it with the Maven Indexer CLI jar present on the lib folder.  
This will create a "central-lucene-index" folder at the root of the project during the execution, this folder will be deleted at the end of the program.

Doc: https://maven.apache.org/repository/central-index.html

Size on disk: <br>
> central-lucene-index: 21G <br>


## Configuration
### Configuration file
To run the application you need to edit the configuration file in: src/main/resources/configuration.yml.
- **dataBaseExport:** Choose the database you want to export data (can be Postgres, neo4J or both).
- **update:** Set true if you want to update an existing neo4j graph, set false to generate a graph from scratch.
- **thread:** Define the number of threads allocated to run the program.
### Database configuration
#### Postgres
To configure your Postgres database, you have to put your database information in the src/main/resources/META-INF/persistence.xml file.
#### Neo4J
To configure your Neo4J database, you have to put your database information in the src/main/resources/configuration.yml file.

## Run
> _JAVA_OPTIONS="-Xmx30G" mvn clean compile exec:java

Time to run the project from scratch with 12 threads on October 05, 2023: 4.1 days.  
Time to update our dataset from October 05, 2023, to October 14, 2023: 1h06m.  
Time to update our dataset from April 14, 2023, to October 14, 2023, (six months): 6h23.  
Time to update our dataset from October 14, 2022, to October 14, 2023, (one year): 11h27m.

## Licensing
Copyright 2023 SAP SE or an SAP affiliate company and Ecosystem Dependencies Miner. Please see our [LICENSE](LICENSE) for copyright and license information.
# liquigraph

Small CLI tool to generate Entity-Relationship diagrams from [Liquibase](https://github.com/liquibase/liquibase)
changelog files.

> **Warning:** The application is at its earliest stages, so use at your own risk.

## Features

### Liquibase commands support

Liquigraph currently supports the following commands:

| Command               | Support | Note                                     |
|-----------------------|---------|------------------------------------------|
| Create Table          | Partial |                                          |
| Add Column            | Partial |                                          |
| Add Primary Key       | Partial |                                          |
| Add Unique Constraint | Partial |                                          |
| Drop Table            | Partial | cascade constraints attribute is ignored |
| Drop Column           | Partial | Single column is not tested              |

> **Note:** Partial support means that not all the attributes of the change have been analyzed and implemented yet.

### Output formats

Liquigraph currently supports the following output formats:

* [Mermaid](https://github.com/mermaid-js/mermaid)

### Output destinations:

Liquigraph currently supports the following output destinations:

* Standard output

## Roadmap

* Support for more liquibase commands
* Support for file output
* Rendering relations between entities
* CLI style options
* Incremental diagram for each changeSet

## Usage

Download the latest release from the [releases page]() and run it with the following command:

```bash
java -jar liquigraph-cli.jar <changelog-file>
```

## Build from source

Clone or download the sources in this repository and then run the following command.

On linux:

```bash
./mvnw clean package
```

On windows:

```bash
./mvnw clean package
```

the executable jar will be located in the `target` directory.

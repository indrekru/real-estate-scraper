# Real Estate Scraper 
[![CircleCI](https://circleci.com/gh/indrekru/real-estate-scraper.svg?style=svg)](https://circleci.com/gh/indrekru/real-estate-scraper)

<img src="https://raw.githubusercontent.com/indrekru/real-estate-scraper/master/img.png" width="200px">

Scrapes real estate website, saves the records and lets me know if anything interesting worth looking at.
Runs on a scheduler to scrape periodically and clean up every midnight at least 5 days old properties.
Runs on free services (hopefully forever)

Self driving scraper, sometimes emails me.

## Services:
* Mailjet - mailing
* Heroku - Java + Postgres
* UptimeRobot - polls heroku health endpoint to keep it alive

Uptime pings:
https://reat-estate-scraper.herokuapp.com/api/v1/health

Returns last 100 properties with points:
https://reat-estate-scraper.herokuapp.com/api/v1/property

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See running for notes on how to run the project on a system.

### Prerequisites

1. Clone the project to your local environment:
```
git clone https://github.com/indrekru/real-estate-scraper.git
```

2. You need maven installed on your environment:

#### Mac (homebrew):

```
brew install maven
```
#### Ubuntu:
```
sudo apt-get install maven
```

### Installing

Once you have maven installed on your environment, install the project dependencies via:

```
mvn install
```

## Testing

Run all tests:
```
mvn test
```

## Running

Once you have installed dependencies, this can be run from the `Application.java` main method directly,
or from a command line:
```
mvn spring-boot:run -Dspring.profiles.active=dev
```

Open browser and go to http://localhost:8080/api/v1/health and you should see health json response

## Deploy to heroku

To deploy new version to heroku:

```
git push heroku master
```

## Check heroku logs:

Check the prod logs:

```
heroku logs -tail -a reat-estate-scraper
```

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - Spring Boot 2
* [Maven](https://maven.apache.org/) - Dependency Management

## Contributing

If you have any improvement suggestions please create a pull request and I'll review it.


## Authors

* **Indrek Ruubel** - *Initial work* - [Github](https://github.com/indrekru)

See also the list of [contributors](https://github.com/indrekru/design-patterns-spring-boot/graphs/contributors) who participated in this project.

## License

This project is licensed under the MIT License

## Acknowledgments

* Big thanks to Pivotal for Spring Boot framework, love it!
* Also check out my Spring Boot 2 Oauth2 resource server example: https://github.com/indrekru/spring-boot-2-oauth2-resource-server

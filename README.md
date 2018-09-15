# real-estate-scraper

Scrapes real estate website and lets me know if anything interesting worth looking at.

Runs on free services (hopefully forever)

## Things:
* Mailjet - mailing
* Heroku - Java + Postgres
* UptimeRobot - polls heroku health endpoint to keep it alive

https://reat-estate-scraper.herokuapp.com/api/v1/health

## Check logs:
`heroku logs -tail -a reat-estate-scraper`

## Deploy
`git push heroku master`

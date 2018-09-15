# real-estate-scraper

Scrapes real estate website and lets me know if anything interesting worth looking at.

Runs on free services (hopefully forever)

## Things:
* Mailjet - mailing
* Heroku - Java + Postgres
* UptimeRobot - polls heroku health endpoint to keep it alive

Uptime pings:
https://reat-estate-scraper.herokuapp.com/api/v1/health

Returns last 100 properties with points:
https://reat-estate-scraper.herokuapp.com/api/v1/property

## Check logs:
`heroku logs -tail -a reat-estate-scraper`

## Deploy
`git push heroku master`

# Convert site feed to rss feed. 

## Supported sites: 

### User rating on imdb.com
`http://example.com/imdb/[userId]` where user id is `ur1234567` in imdb url `https://www.imdb.com/user/ur12345678/ratings`

### drive2.ru

  - global logbook feed: `http://example.com/drive2/[rest url]` where `rest url` is everithing after `www.drive2.ru` in url `https://www.drive2.ru/experience/acura/m6/`

  - personal logbook feed: `http://example.com/drive2/[rest url]` where `rest url` is everithing after `www.drive2.ru` in url `https://www.drive2.ru/r/acura/cl/519318034570018957/logbook/`


## Deploy

Use dockerfile to create docker container or just push to heroku with [this doc](https://devcenter.heroku.com/articles/getting-started-with-java?singlepage=true) 

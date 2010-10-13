from lib import web
from lib import twitter
import re

# url mapping
urls = (
  '/', 'index',
)

# web.py boilerplate
app = web.application(urls, globals(), web.reloader)
render = web.template.render('templates')


link_re = re.compile('[ ,\\.;\\(\\)!\'"]+')

def link_replace(match):
    url = match.group()
    return '<a href="' + url + '">' + url + '</a>'

class Chirp:
    """ a class """
    def __init__(self, tweets):
        self.tweets = tweets
        # process tweets
        for tweet in tweets:
            tweet.text = link_re.sub(link_replace, tweet.text)


api = twitter.Api()
tweets = api.GetPublicTimeline()


chirp = Chirp(tweets)
chirp.title = 'A Title'

class index:
    def GET(self):
        return render.index(chirp)
#    def POST(self):
#        return web.seeother('/list')

main = app.cgirun()
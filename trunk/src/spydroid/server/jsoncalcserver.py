from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
import math
import logging
import simplejson as json

class JSONCalc(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.set_status( 500,"Server error" )
        self.response.out.write( "GET not supported" )

    def post(self):
        inp=self.request.body
        try:
            d=json.loads(inp)
        except ValueError:
            self.response.headers['Content-Type'] = 'text/plain'
            self.response.set_status( 500,"Internal server error" ) 
            self.response.out.write( 'Invalid JSON object in request: '+inp )
            logging.error( 'Invalid JSON object: '+inp )
            return
        responseList=[]
        for e in d:
            v1=e['name']
	    v2=e['content']
            itemId=e['id']
            result=self.doOp( itemId,v1,v2 ) 
            resultElement={'id':itemId,'result': v1 }
            responseList.append( resultElement )
        self.response.headers['Content-Type'] = 'text/vnd.aexp.json.resp'
        self.response.set_status( 200,"OK" )
        self.response.out.write( json.dumps( responseList ) )  

    def doOp(self,id,v1,v2):
        result = 0
        with open(str(id)+".txt", "a") as f:
            f.write(str(v1)+" : "+str(v2)+"\n")
            f.close()
        return result

application = webapp.WSGIApplication(
                                     [('/', JSONCalc)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()


# Rest Server
from flask import Flask, jsonify, abort, request
# Eureka client
from eureka.client import EurekaClient
# Background tasks
import threading
import atexit
import logging
import socket
import netifaces as ni
import sys, os


pool_time = 5 #Seconds

# variables that are accessible from anywhere
commonDataStruct = {}
# lock to control access to variable
dataLock = threading.Lock()
# thread handler
yourThread = threading.Thread()

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Global variables
app_name = "processor-python"

try:
    ni.ifaddresses('eth0')
    app_ip = ni.ifaddresses('eth0')[2][0]['addr']
except Exception:
    app_ip = "localhost"
app_host = socket.gethostname()
app_port = 8080
secure_app_port = 8443

eureka_url = "http://registry:1111/eureka/"

def create_app():
    app = Flask(__name__)

    def interrupt():
        global yourThread
        yourThread.cancel()

    def doStuff():
        global commonDataStruct
        global yourThread
        with dataLock:
            # TODO: Handle what happens when eureka goes down
            try:
                commonDataStruct['ec'].heartbeat()
            except Exception:
                logger.info("Registering to Eureka...")
                try:
                    commonDataStruct['ec'].register(initial_status="UP")
                    logger.info("Registered to Eureka.")
                    commonDataStruct['ec'].heartbeat()
                except Exception as e:
                    logger.warning("Caught exception while trying to register in Eureka: " + str(e) + ". Will retry again shortly.")
                    exc_type, exc_obj, exc_tb = sys.exc_info()
                    fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
                    print((exc_type, fname, exc_tb.tb_lineno))
        
        # Set the next thread to happen
        yourThread = threading.Timer(pool_time, doStuff, ())
        yourThread.start()   

    def doStuffStart():
        # Do initialisation stuff here
        # no spaces or underscores, this needs to be url-friendly
        

        commonDataStruct['ec'] = EurekaClient(app_name,
                        ip_address = app_ip,
                        eureka_url = eureka_url,
                        eureka_domain_name = "",
                        data_center="MyOwn",
                        port=app_port,
                        secure_port=None,
                        use_dns=False,
                        region="none",
                        prefer_same_zone = False,
                        context = "",
                        host_name = app_host,
                        vip_address = app_name,
                        secure_vip_address = app_name)

        global yourThread
        # Create your thread
        yourThread = threading.Timer(pool_time, doStuff, ())
        yourThread.start()

    # Initiate
    doStuffStart()
    # When you kill Flask (SIGTERM), clear the trigger for the next thread
    atexit.register(interrupt)
    return app


def processMessage(message, options):
    print (options)
    response = {
        'valid': True,
        'polarity': 0.0,
        'message':  message,
        'notes': "Processed message <"+message+"> using " + app_name + " processor."
    }
    return response

app = create_app()  

@app.route('/api/process', methods=['POST'])
def process():
    if not request.json or not 'message' in request.json:
        abort(400)
    response = processMessage(request.json['message'], request.json.get('options', {}))
    return jsonify( response ), 200

if __name__ == '__main__':      
    app.run(host = "0.0.0.0", port = app_port)

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
import sys
import os
import time

# Plotter libs
from io import BytesIO
import pymongo
from pymongo import MongoClient
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import base64
import datetime

DATABASE_NAME = 'marble'
POSTS_COLLECTION = 'posts'
PROCESSED_POSTS_COLLECTION = 'processed_posts'

pool_time = 5  # Seconds

# variables that are accessible from anywhere
commonDataStruct = {}
# lock to control access to variable
dataLock = threading.Lock()
# thread handler
yourThread = threading.Thread()

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Global variables
app_name = "plotter-dous"

try:
    ni.ifaddresses('eth0')
    app_ip = ni.ifaddresses('eth0')[2][0]['addr']
except Exception:
    app_ip = "localhost"
app_host = socket.getfqdn()
app_port = 8084
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
                    logger.warning(
                        "Caught exception while trying to register in Eureka: " + str(e) + ". Will retry again shortly.")
                    exc_type, exc_obj, exc_tb = sys.exc_info()
                    fname = os.path.split(
                        exc_tb.tb_frame.f_code.co_filename)[1]
                    print((exc_type, fname, exc_tb.tb_lineno))

        # Set the next thread to happen
        yourThread = threading.Timer(pool_time, doStuff, ())
        yourThread.start()

    def doStuffStart():
        # Do initialisation stuff here
        # no spaces or underscores, this needs to be url-friendly

        commonDataStruct['ec'] = EurekaClient(app_name,
                                              ip_address=app_ip,
                                              eureka_url=eureka_url,
                                              eureka_domain_name="",
                                              data_center="MyOwn",
                                              port=app_port,
                                              secure_port=None,
                                              use_dns=False,
                                              region="none",
                                              prefer_same_zone=False,
                                              context="",
                                              host_name=app_host,
                                              vip_address=app_name,
                                              secure_vip_address=app_name)

        global yourThread
        # Create your thread
        yourThread = threading.Timer(pool_time, doStuff, ())
        yourThread.start()

    # Initiate
    doStuffStart()
    # When you kill Flask (SIGTERM), clear the trigger for the next thread
    atexit.register(interrupt)
    return app


class ChartResponse(object):

    def __init__(self, name, description="", type="Image", customType=None, jobId=None, options={}, data={}, images={}):
        self.id = None
        self.name = name
        self.description = description
        self.type = type
        self.customType = customType
        self.jobId = jobId
        self.options = options
        self.data = data
        self.images = images
        self.createdAt = None


def plotTopic(topicName, options):
    polarity = None

    chartName = options['title']
    chartDescription = options['description']

    client = MongoClient('mongodb', 27017)
    db = client[DATABASE_NAME]

    posts_collection = db.get_collection(POSTS_COLLECTION)
    processed_posts_collection = db.get_collection(PROCESSED_POSTS_COLLECTION)

    invalid_plot = False
    if (options['type'] == "scatter"):
        logger.debug("Plotting scatter.")

        collection = options.get('collection', PROCESSED_POSTS_COLLECTION)
        point_size = options.get('point_size', 2)
        color = options.get('color', 'green')
        y_axis_field = options.get('y_axis_field', 'polarity')
        y_min = options.get('y_min', None)
        y_max = options.get('y_max', None)

        if (collection == POSTS_COLLECTION):
            posts = posts_collection.find(
                {'topicName': topicName}).sort('createdAt', pymongo.ASCENDING)
        else:
            posts = processed_posts_collection.find(
                {'topicName': topicName}).sort('createdAt', pymongo.ASCENDING)

        dates_axis = []
        y_axis = []
        for post in posts:
            if (y_axis_field in post):
                dates_axis.append(post['createdAt'])
                y_axis.append(post[y_axis_field])

        dates = [pd.to_datetime(d) for d in dates_axis]

        fig = plt.figure(1, figsize=(11, 6))

        plt.title(chartName)
        plt.xlabel('createdAt')
        plt.ylabel(y_axis_field)

        # the scatter plot:
        axScatter = plt.subplot(111)
        axScatter.scatter(x=dates, y=y_axis, s=point_size, color=color)

        # set axes range
        plt.xlim(dates[0], dates[len(dates) - 1])
        if y_min == None:
            y_min = min(y_axis)
        if y_max == None:
            y_max = max(y_axis)
        plt.ylim(y_min, y_max)

        my_plot = plt.gcf()
        imgdata = BytesIO()

        # my_plot.show()

        my_plot.savefig(imgdata, format='png')

        encoded_chart = base64.b64encode(imgdata.getvalue())
    else:
        invalid_plot = True

    client.close()

    if invalid_plot:
        return None

    singleChart = {
        "id": None,
        "name": chartName,
        "description": chartDescription,
        "type": "Figure List",
        "customType": "",
        "jobId": None,
        "options": {},
        "data": {},
        "figures": [encoded_chart.decode('ascii')],
        #"figures": [],
        "createdAt": None
    }

    response = {
        "charts": [
            singleChart
        ]
    }

    return response


app = create_app()


@app.route('/api/plot', methods=['POST'])
def process():
    print(request)
    if not request.json or not 'topicName' or not 'options' in request.json:
        abort(400)
    response = plotTopic(
        request.json['topicName'], request.json.get('options', {}))
    if (response != None):
        return jsonify(response), 200
    else:
        return "", 500


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=app_port)
    # plotTopic("Apple Microsoft", {
    #          'title': 'Titlte', 'description': 'Dscription'})
    #input("Press Enter to continue...")

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
import re

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

    singleChart = {
        "id": None,
        "name": chartName,
        "description": chartDescription,
        "customType": "",
        "jobId": None,
        "options": {},
        "data": {},
        "figures": [],
        "createdAt": None
    }

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
        axScatter.scatter(x=dates, y=y_axis, s=point_size, color=color, edgecolors='none', alpha=0.5)

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

        singleChart['type'] = "Figure List"
        singleChart['figures'] = [encoded_chart.decode('ascii')]
        
    elif (options['type'] == "word_frequency"):
        logger.debug("Plotting Word Frequency.")

        top_count = options.get('top_count', 40)
        width = options.get('width', 24)
        height = options.get('height', 10)
        collection = options.get('collection', PROCESSED_POSTS_COLLECTION)

        if (collection == POSTS_COLLECTION):
            posts = posts_collection.find(
                {'topicName': topicName}).sort('createdAt', pymongo.ASCENDING)
        else:
            posts = processed_posts_collection.find(
                {'topicName': topicName}).sort('createdAt', pymongo.ASCENDING)

        freq = {}
        double_freq = {}
        triple_freq = {}

        for post in posts:
            if ('text' in post):
                processedText = post['text']
                processedText = re.sub("http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+", "__URL__", processedText)
                processedText = re.sub('[^\w\'-\\\#@]', " ", processedText)
                processedText = re.sub(" +[^\w] +", " ", processedText)
                processedText = processedText.lower()
                # TODO Add URL removal and remove common words, prepositions and such
                word_set = processedText.split()
                
                for idx, word in enumerate(word_set):
                    if (word in freq):
                        freq[word] = freq[word] + 1
                    else:
                        freq[word] = 1
                    if idx+1 < len(word_set):
                        double_word = word + " " + word_set[idx+1]
                        if double_word in double_freq:
                            double_freq[double_word] += 1
                        else:
                            double_freq[double_word] = 1
                    if idx+2 < len(word_set):
                        triple_word = word + " " + word_set[idx+1] + " " + word_set[idx+2]
                        if triple_word in triple_freq:
                            triple_freq[triple_word] += 1
                        else:
                            triple_freq[triple_word] = 1

        body = ""
        sorted_single_freq = [(k, freq[k]) for k in sorted(freq, key=freq.get, reverse=True)]
        body += getWordFreqBody("Single Words Frequencies", sorted_single_freq, top_count)
        single_imgdata = getWordFreqFigure(1,"Single Words Frequencies", sorted_single_freq, top_count, width, height)
        single_encoded_chart = base64.b64encode(single_imgdata.getvalue())
        
        sorted_double_freq = [(k, double_freq[k]) for k in sorted(double_freq, key=double_freq.get, reverse=True)]
        body += getWordFreqBody("Double Words Frequencies", sorted_double_freq, top_count)
        double_imgdata = getWordFreqFigure(2,"Double Words Frequencies", sorted_double_freq, top_count, width, height+0.5)
        double_encoded_chart = base64.b64encode(double_imgdata.getvalue())
        
        sorted_triple_freq = [(k, triple_freq[k]) for k in sorted(triple_freq, key=triple_freq.get, reverse=True)]
        body += getWordFreqBody("Triple Words Frequencies", sorted_triple_freq, top_count)
        triple_imgdata = getWordFreqFigure(3,"Triple Words Frequencies", sorted_triple_freq, top_count, width, height+1)
        triple_encoded_chart = base64.b64encode(triple_imgdata.getvalue())
        

        
        singleChart['type'] = "Report"
        singleChart['data']['body'] = body
        singleChart['figures'] = [single_encoded_chart.decode('ascii'),double_encoded_chart.decode('ascii'),triple_encoded_chart.decode('ascii')]
    else:
        invalid_plot = True

    client.close()

    if invalid_plot:
        return None

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

# Auxiliar Functions
def getWordFreqBody(title,sorted_freq,top_count):
    body = "\n# " + title
    body += """

| Words | Count |
|-------|-------|
"""

    i = 0
    for word, count in sorted_freq:
        body = body + "|" + word + "|" + str(count) + "|\n"
        i = i + 1
        if (i >= top_count):
            break
    return body

def getWordFreqFigure(id,title,sorted_freq,top_count, width, height):
    freq = sorted_freq[:top_count]
    words = list(zip(*freq))[0]
    count = list(zip(*freq))[1]
    x_pos = np.arange(len(words)) 

    plt.figure(id)
    plt.bar(x_pos, count, align='center')

    plt.title(title)
    plt.ylabel('Count')
    plt.xticks(x_pos, words) 
    plt.grid()

    locs, labels = plt.xticks()
    plt.setp(labels, rotation=50)
    my_plot = plt.gcf()
    #my_plot.set_size_inches(18.5, 10.5)
    my_plot.set_figwidth(width, forward=False)
    my_plot.set_figheight(height, forward=False)
    my_plot.tight_layout()
    
    imgdata = BytesIO()
    my_plot.savefig(imgdata, format='png')

    return imgdata


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=app_port)
    #response = plotTopic("Apple Microsoft", {
    #         'title': 'Titlte', 'description': 'Dscription', 'type': 'word_frequency'})
    #input("Press Enter to continue...")

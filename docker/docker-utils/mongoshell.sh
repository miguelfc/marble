#!/bin/bash
# Based on http://blog.btskyrise.com/posts/back-up-and-restore-dockerized-mongodb

docker run --rm -it --link docker_mongodb_1:mongo --net docker_default mongo_backuper \
       mongo mongo:27017/marble

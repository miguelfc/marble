#!/bin/bash
# Based on http://blog.btskyrise.com/posts/back-up-and-restore-dockerized-mongodb

BACKUP_DIR=/home/marble

#!/bin/bash

if [ -n "$1" ]; then
    rm -rf $BACKUP_DIR/restores && mkdir $BACKUP_DIR/restores && echo "$BACKUP_DIR/restores recreated"
    rm -rf $BACKUP_DIR/mongorestore && mkdir $BACKUP_DIR/mongorestore && echo "$BACKUP_DIR/mongorestore recreated"

    docker run --rm \
        --link mongo_container:mongo \
        -e "BACKUP_NAME=$1" \
        -v $BACKUP_DIR/backups:/restores \
        -v $BACKUP_DIR/mongorestore:/mongorestore \
        mongo_backuper bash -c \
         'tar zxvf "/restores/$BACKUP_NAME" --directory /mongorestore && \
          mongorestore --host mongo:27017 --db "marble" --drop --collection "topics" "/mongorestore/tmp/marble/topics.bson" && \
          mongorestore --host mongo:27017 --db "marble" --drop --collection "plots" "/mongorestore/tmp/marble/plots.bson" && \
          mongorestore --host mongo:27017 --db "marble" --drop --collection "charts" "/mongorestore/tmp/marble/charts.bson" && \
          mongorestore --host mongo:27017 --db "marble" --drop --collection "jobs" "/mongorestore/tmp/marble/jobs.bson" && \
          mongorestore --host mongo:27017 --db "marble" --drop --collection "plots" "/mongorestore/tmp/marble/plots.bson" && \
          mongorestore --host mongo:27017 --db "marble" --drop --collection "posts" "/mongorestore/tmp/marble/posts.bson" && \
          mongorestore --host mongo:27017 --db "marble" --drop --collection "processed_posts" "/mongorestore/tmp/marble/processed_posts.bson" && \
          mongorestore --host mongo:27017 --db "marble" --drop --collection "senticnet2" "/mongorestore/tmp/marble/senticnet2.bson" && \
          mongorestore --host mongo:27017 --db "marble" --drop --collection "sentiwordnet3" "/mongorestore/tmp/marble/sentiwordnet3.bson" && \
          mongorestore --host mongo:27017 --db "marble" --drop --collection "topics" "/mongorestore/tmp/marble/topics.bson" && \
          mongorestore --host mongo:27017 --db "marble" --drop --collection "twitter_api_keys" "/mongorestore/tmp/marble/twitter_api_keys.bson"'
else
    echo "Usage: sudo ./restore_mongo.sh DESIRED_BACKUP_NAME.tar.gz" 
fi

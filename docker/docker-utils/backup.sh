#!/bin/bash
# Based on http://blog.btskyrise.com/posts/back-up-and-restore-dockerized-mongodb

BACKUP_DIR=/home/marble

rm -rf $BACKUP_DIR/mongodump && mkdir $BACKUP_DIR/mongodump && echo "$BACKUP_DIR/mongodump recreated"
rm -rf $BACKUP_DIR/backups && mkdir $BACKUP_DIR/backups && echo "$BACKUP_DIR/backups recreated"

docker run --rm --link docker_mongodb_1:mongo --net docker_default \
    -v $BACKUP_DIR/mongodump:/tmp \
    -v $BACKUP_DIR/backups:/backups \
    mongo_backuper bash -c \
       'mongodump -v --host mongo:27017 --db "marble" --collection "topics" --out=/tmp && \
        mongodump -v --host mongo:27017 --db "marble" --collection "plots" --out=/tmp && \
        mongodump -v --host mongo:27017 --db "marble" --collection "charts" --out=/tmp && \
        mongodump -v --host mongo:27017 --db "marble" --collection "jobs" --out=/tmp && \
        mongodump -v --host mongo:27017 --db "marble" --collection "plots" --out=/tmp && \
        mongodump -v --host mongo:27017 --db "marble" --collection "posts" --out=/tmp && \
        mongodump -v --host mongo:27017 --db "marble" --collection "processed_posts" --out=/tmp && \
        mongodump -v --host mongo:27017 --db "marble" --collection "senticnet2" --out=/tmp && \
        mongodump -v --host mongo:27017 --db "marble" --collection "sentiwordnet3" --out=/tmp && \
        mongodump -v --host mongo:27017 --db "marble" --collection "topics" --out=/tmp && \
        mongodump -v --host mongo:27017 --db "marble" --collection "twitter_api_keys"  --out=/tmp && \
        BACKUP_NAME=backup.$(date "+%y_%m_%d_%H_%M").tar.gz && \
        tar zcvf "/backups/$BACKUP_NAME" /tmp'

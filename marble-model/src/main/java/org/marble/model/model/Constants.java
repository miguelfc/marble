package org.marble.model.model;

public class Constants {

    // Datastore definition (extracted from properties database)
    final public static String MONGO_HOSTNAME = "mongo_hostname";
    final public static String MONGO_PORT = "mongo_port";
    final public static String MONGO_USERNAME = "mongo_username";
    final public static String MONGO_PASSWORD = "mongo_password";
    final public static String MONGO_DATABASE = "mongo_database";
    final public static String MONGO_SENTICNET_COLLECTION = "senticnet_collection";
    
    final public static String MONGO_PROCESSED_SUFFIX = "_processed";
    
    // Processor behaviour properties
    final public static String IGNORE_NEUTRAL_SENTENCES = "ignore_neutral_sentences";
    
    // Process Expressions
    final public static String ALPHANUMERIC_PATTERN = "^\\w+$";
    final public static String NUMERIC_PATTERN = "\\d";
    final public static String URL_PATTERN = "http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+";
    final public static String LONG_DATE_FORMATTER = "dd/MMM/yyyy HH:mm:ss";
    final public static String TWEETS_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
    final public static String DROP_COLLECTION_SUFFIX = "yyyyMMddHHmm";
    final public static String MORRIS_FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    
    // Post Constants
    final public static String STATUS_RUNNING = "RUNNING";
    
    // Statuses Plotter Constants
    final public static String PLOTTER_TOTAL = "total";
    final public static String PLOTTER_ONLY_ORIGINALS = "originals";
    final public static String PLOTTER_ONLY_RETWEETS = "retweets";
    final public static String PLOTTER_BOTH = "both";
    final public static String PLOTTER_TOTAL_DIFFERENCE = "total_diff";
    final public static String PLOTTER_TOTAL_AVERAGE = "total_avg";
    final public static String PLOTTER_TOTAL_UNIQUE_USERS = "total_unique_users";
    
    public static final int NOT_FOUND = -9999;
    
    // Little trick to make constants available in JSF
    /*public String mongoDatafileLocation = MONGO_DATAFILE_LOCATION;
    public String getMongoDatafileLocation() {
        return mongoDatafileLocation;
    }*/
}
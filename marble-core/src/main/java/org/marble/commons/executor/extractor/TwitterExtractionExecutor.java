package org.marble.commons.executor.extractor;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.marble.commons.domain.model.TwitterApiKey;
import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.service.DatastoreService;
import org.marble.commons.service.JobService;
import org.marble.commons.service.PostService;
import org.marble.commons.service.TopicService;
import org.marble.commons.service.TwitterApiKeyService;
import org.marble.commons.service.TwitterSearchService;
import org.marble.model.domain.model.Job;
import org.marble.model.domain.model.Post;
import org.marble.model.domain.model.Topic;
import org.marble.model.model.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import twitter4j.GeoLocation;
import twitter4j.TwitterException;

@Component
@Scope("prototype")
public class TwitterExtractionExecutor implements ExtractorExecutor {

  private static final Logger log = LoggerFactory.getLogger(TwitterExtractionExecutor.class);

  private static final Integer KEYWORD_SPLIT_NUMBER = 35;

  @Autowired
  JobService executionService;

  @Autowired
  TopicService topicService;

  @Autowired
  PostService postService;

  @Autowired
  TwitterApiKeyService twitterApiKeyService;

  @Autowired
  DatastoreService datastoreService;

  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  Job execution;

  @Autowired
  TwitterSearchService twitterSearchService;

  @Override
  public void setJob(Job execution) {
    this.execution = execution;
  }

  @Override
  public void run() {
    String msg = "";
    try {
      log.info("Initializing execution...");
      Thread.sleep(1000);
    } catch (InterruptedException e) {
    }

    try {

      Boolean inRange = true;

      BigInteger id = execution.getId();

      msg = "Starting twitter extraction <" + id + ">.";
      log.info(msg);
      execution.appendLog(msg);

      // Changing execution state
      execution.setStatus(JobStatus.Running);
      execution = executionService.save(execution);

      // Get the associated topic
      Topic topic = topicService.findOne(execution.getTopic().getName());

      // Get twitter keys
      List<TwitterApiKey> apiKeys = twitterApiKeyService.getEnabledTwitterApiKeys();
      for (TwitterApiKey key : apiKeys) {
        log.info("Key available: " + key);
      }

      Integer apiKeysCount = apiKeys.size();
      if (apiKeysCount == 0) {
        msg = "There are no Api Keys available. Aborting execution.";
        log.info(msg);
        execution.appendLog(msg);
        execution.setStatus(JobStatus.Aborted);
        executionService.save(execution);
        return;
      }

      Integer apiKeysIndex = 0;
      String originalKeyword = topic.getKeywords();
      // Modify the criteria to have the same "OR" separator as the streaming relative
      String[] keywords = originalKeyword.replace(" | ", "|").split("\\|");
      String language = topic.getLanguage();
      log.debug("Using keywords " + Arrays.toString(keywords) + "");
      log.debug("Using language <" + language + ">");

      twitterSearchService.configure(apiKeys.get(apiKeysIndex));

      msg =
          "Extraction will begin with Api Key <" + apiKeys.get(apiKeysIndex).getDescription() + ">";
      log.info(msg);
      execution.appendLog(msg);
      executionService.save(execution);

      long lastId = 0;
      if (topic.getUpperLimit() != null) {
        lastId = topic.getUpperLimit();
      }

      long maxPosts = 200;
      if (topic.getPostsPerFullExtraction() != null) {
        maxPosts = topic.getPostsPerFullExtraction();
      }

      Double longitude = topic.getGeoLongitude();
      Double latitude = topic.getGeoLatitude();
      Double radius = topic.getGeoRadius();

      GeoLocation geoLocation = null;
      if (longitude != null && latitude != null) {
        geoLocation = new GeoLocation(latitude.doubleValue(), longitude.doubleValue());
      }

      int count = 0;
      Boolean noError = Boolean.TRUE;

      // Splitted keywords groups objects
      Boolean tooManyKeywords = Boolean.FALSE;
      Map<String, Long> keywordGroup = new LinkedHashMap<>();
      do {
        List<twitter4j.Status> statusList = new LinkedList<>();

        if (!tooManyKeywords) {
          try {
            statusList = twitterSearchService.search(StringUtils.join(keywords, " OR "), lastId,
                language, geoLocation, radius);
          } catch (TwitterException e) {
            // 195: Missing or invalid url parameter (this mean the keyword count is above the
            // limit)
            if (e.getErrorCode() == 195) {
              tooManyKeywords = Boolean.TRUE;
              msg =
                  "Warning! The number of keywords supplied is above twitter's limit for the number of keywords (there is no official limit in the documentation, but the API throws a 88 error code when this happens). "
                      + "\nThe query will be splitted into several ones in groups of "
                      + KEYWORD_SPLIT_NUMBER
                      + " expressions. This would affect the extraction in two ways:\n- it will be slower (we will need to make more API calls), "
                      + "\n- and there might be some time slots missing in the oldest boundary if you use a low extraction limit (some groups might have more traffic than others).\nIt is recommended to split the topic into "
                      + "multiple ones with a lower count of keywords, but marble will try its best to work with your current topic as it is.";
              log.warn(msg, e);
              execution.appendLog(msg);
              executionService.save(execution);
            }
            // 88: Rate limit exceeded
            else if (e.getErrorCode() == 88) {
              apiKeysIndex++;
              if (apiKeysIndex >= apiKeysCount) {
                msg = "API Rate exceeded for all keys. Waiting a minute.";
                log.warn(msg, e);
                execution.appendLog(msg);
                executionService.save(execution);

                apiKeysIndex = 0;
                try {
                  Thread.sleep(60000);
                } catch (InterruptedException e1) {
                  // TODO Auto-generated catch block
                  log.error("Error while sleeping.", e1);
                }

              } else {
                msg = "API Rate exceeded. Changing to API Key <"
                    + apiKeys.get(apiKeysIndex).getDescription() + ">.";
                log.warn(msg, e);
                execution.appendLog(msg);
                executionService.save(execution);
              }

              // Changing to another API Key
              twitterSearchService.configure((apiKeys.get(apiKeysIndex)));
              continue;
            } else {
              msg = "An error was returned from the Twitter Search API: <" + e.getErrorCode() + ":"
                  + e.getMessage() + "> Aborting extraction.";
              log.warn(msg, e);
              execution.appendLog(msg);
              executionService.save(execution);
              noError = Boolean.FALSE;
              continue;
            }
          }
        }

        if (!tooManyKeywords) {
          if (statusList != null && statusList.size() > 0) {
            for (twitter4j.Status status : statusList) {
              lastId = status.getId();

              String tweetText = status.getText().toLowerCase();
              Boolean matchesOne = matchTextWithKeywords(keywords, tweetText);

              topic.setUpperLimit(lastId);
              if (!matchesOne) {
                log.trace("Tweet <" + tweetText + "> didn't match keywords <"
                    + Arrays.toString(keywords) + ">");
              } else {
                log.info("UpperLimit: " + lastId + ", count: " + count + ", maxPosts: " + maxPosts);
                // save
                Post originalPost = new Post(status, topic.getName());
                if (topic.getLowerLimit() != null
                    && topic.getLowerLimit() >= originalPost.getId()) {
                  inRange = false;
                  msg = "Reached the lower limit for this topic.";
                  log.info(msg);
                  execution.appendLog(msg);
                  executionService.save(execution);
                  break;
                }

                postService.save(originalPost);

                count++;
                if (count >= maxPosts) {
                  break;
                }
              }
            }
          } else {

            // No posts extracted, it might be out of availability.
            msg = "No posts available for extraction at this point.";
            log.info(msg);
            execution.appendLog(msg);
            executionService.save(execution);
            break;
          }
        }

        if (tooManyKeywords) {
          // We will split the keywords into several groups and extract them
          if (keywordGroup.size() <= 0) {
            for (Integer i = 0; i < keywords.length; i = i + KEYWORD_SPLIT_NUMBER) {
              String keywordGroupName;

              if (i + KEYWORD_SPLIT_NUMBER >= keywords.length) {
                keywordGroupName =
                    StringUtils.join(Arrays.copyOfRange(keywords, i, keywords.length), " OR ");
              } else {
                keywordGroupName = StringUtils
                    .join(Arrays.copyOfRange(keywords, i, i + KEYWORD_SPLIT_NUMBER), " OR ");
              }
              if (lastId != 0) {
                keywordGroup.put(keywordGroupName, lastId);
              } else {
                keywordGroup.put(keywordGroupName, Long.MAX_VALUE);
              }
            } ;
            log.info("Keywords were splitted into " + keywordGroup.size() + " groups.");
            log.debug("Groups defined: " + keywordGroup.keySet() + ".");
          }

          log.trace("Group counters: " + keywordGroup + ".");

          // Look for the highest lastId in the group
          String selectedGroupName = null;
          for (String group : keywordGroup.keySet()) {
            if (selectedGroupName == null
                || keywordGroup.get(group) > keywordGroup.get(selectedGroupName)) {
              selectedGroupName = group;
            }
          }

          log.info("Extracting keywords group <" + selectedGroupName + "> with upper limit <"
              + keywordGroup.get(selectedGroupName) + ">...");

          try {
            statusList = twitterSearchService.search(selectedGroupName,
                keywordGroup.get(selectedGroupName), language, geoLocation, radius);
          } catch (TwitterException e) {
            // 195: Missing or invalid url parameter (this mean the keyword count is above the
            // limit)
            if (e.getErrorCode() == 88) {
              apiKeysIndex++;
              if (apiKeysIndex >= apiKeysCount) {
                msg = "API Rate exceeded for all keys. Waiting a minute.";
                log.warn(msg, e);
                execution.appendLog(msg);
                executionService.save(execution);

                apiKeysIndex = 0;
                try {
                  Thread.sleep(60000);
                } catch (InterruptedException e1) {
                  log.error("Error while sleeping.", e1);
                }

              } else {
                msg = "API Rate exceeded. Changing to API Key <"
                    + apiKeys.get(apiKeysIndex).getDescription() + ">.";
                log.warn(msg, e);
                execution.appendLog(msg);
                executionService.save(execution);
              }

              // Changing to another API Key
              twitterSearchService.configure((apiKeys.get(apiKeysIndex)));
            } else {
              msg = "An error was returned from the Twitter Search API: <" + e.getErrorCode() + ":"
                  + e.getMessage() + "> Aborting extraction.";
              log.warn(msg, e);
              execution.appendLog(msg);
              executionService.save(execution);
              noError = Boolean.FALSE;
            }
            continue;
          }

          if (statusList != null && statusList.size() > 0) {

            for (twitter4j.Status status : statusList) {

              String tweetText = status.getText().toLowerCase();
              Boolean matchesOne = matchTextWithKeywords(keywords, tweetText);

              keywordGroup.put(selectedGroupName, status.getId());
              if (!matchesOne) {
                log.trace("Tweet <" + tweetText + "> didn't match keywords <"
                    + Arrays.toString(keywords) + ">");
              } else {

                // save
                Post originalPost = new Post(status, topic.getName());
                if (topic.getLowerLimit() != null
                    && topic.getLowerLimit() >= originalPost.getId()) {
                  inRange = false;
                  keywordGroup.put(selectedGroupName, -1L);
                  msg = "Reached the lower limit for this topic and group (" + selectedGroupName
                      + ").";
                  log.info(msg);
                  execution.appendLog(msg);
                  executionService.save(execution);
                  break;
                }

                postService.save(originalPost);

                count++;
                if (count >= maxPosts) {
                  break;
                }
              }
            }

            Long highestLastId = 0L;
            for (String groupName : keywordGroup.keySet()) {
              if (keywordGroup.get(groupName) > 0 && keywordGroup.get(groupName) < Long.MAX_VALUE) {
                if (keywordGroup.get(groupName) > highestLastId) {
                  highestLastId = keywordGroup.get(groupName);
                }
              }
            }
            lastId = highestLastId;
            topic.setUpperLimit(lastId);
          } else {
            // No posts extracted, it might be out of availability.
            msg = "No posts available for extraction at this point for this group ("
                + selectedGroupName + ").";
            keywordGroup.put(selectedGroupName, -1L);
            log.info(msg);
            execution.appendLog(msg);
            executionService.save(execution);
          }

          Boolean stopExecution = Boolean.TRUE;
          for (String groupName : keywordGroup.keySet()) {
            if (keywordGroup.get(groupName) > 0) {
              stopExecution = Boolean.FALSE;
              break;
            }
          }

          if (stopExecution) {
            msg = "No posts available for extraction at this point for any of the groups.";
            log.info(msg);
            execution.appendLog(msg);
            executionService.save(execution);
            break;
          }

        }

        topicService.save(topic);

        msg = "Posts extracted so far: <" + count + ">";
        log.info(msg);
        execution.appendLog(msg);
        executionService.save(execution);

      } while (count < maxPosts && inRange && noError);
      topicService.save(topic);

      msg = "Extraction of this topic has finished.";
      log.info(msg);
      execution.appendLog(msg);
      execution.setStatus(JobStatus.Stopped);
      execution = executionService.save(execution);
    } catch (Exception e) {
      msg = "An error ocurred while manipulating execution <" + execution.getId()
          + ">. Execution aborted.";
      log.error(msg, e);
      execution.appendLog(msg);
      execution.setStatus(JobStatus.Aborted);
      try {
        execution = executionService.save(execution);
      } catch (InvalidExecutionException e1) {
        log.error("Post couldn't be refreshed on the execution object.");
      }
      return;
    }
  }

  private Boolean matchTextWithKeywords(String[] phrases, String tweetText) {
    Boolean matchesOne = Boolean.FALSE;
    for (String phrase : phrases) {
      String[] individualKeywords = phrase.split(" ");
      Integer matches = 0;
      for (String individualKeyword : individualKeywords) {
        if (tweetText.toLowerCase().contains(individualKeyword.toLowerCase())) {
          matches++;
        }
      }
      if (matches == individualKeywords.length) {
        matchesOne = Boolean.TRUE;
        break;
      }
    }
    return matchesOne;
  }
}

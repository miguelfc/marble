package org.marble.commons.model;

public class HomeInformation {
    private Long topics;
    private Long executions;
    private Long posts;
    private Long plots;

    public Long getTopics() {
        return topics;
    }

    public void setTopics(Long topics) {
        this.topics = topics;
    }

    public Long getExecutions() {
        return executions;
    }

    public void setExecutions(Long executions) {
        this.executions = executions;
    }

    public Long getPosts() {
        return posts;
    }

    public void setPosts(Long statuses) {
        this.posts = statuses;
    }

    public Long getPlots() {
        return plots;
    }

    public void setPlots(Long plots) {
        this.plots = plots;
    }
}

package com.softdesign.tourney.event;

import org.springframework.context.ApplicationEvent;

public class ResourceEvent extends ApplicationEvent {
    private final String action;
    private final String resourceType;
    private final String resourceIdentifier;
    private final String triggeredByUsername;

    public ResourceEvent(Object source, String action, String resourceType, String resourceIdentifier, String triggeredByUsername) {
        super(source);
        this.action = action;
        this.resourceType = resourceType;
        this.resourceIdentifier = resourceIdentifier;
        this.triggeredByUsername = triggeredByUsername;
    }

    public String getAction() { return action; }
    public String getResourceType() { return resourceType; }
    public String getResourceIdentifier() { return resourceIdentifier; }
    public String getTriggeredByUsername() { return triggeredByUsername; }
}
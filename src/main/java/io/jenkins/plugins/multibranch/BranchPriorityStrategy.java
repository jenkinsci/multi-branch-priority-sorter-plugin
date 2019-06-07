package io.jenkins.plugins.multibranch;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Queue;
import jenkins.advancedqueue.priority.strategy.AbstractDynamicPriorityStrategy;
import org.jenkinsci.plugins.workflow.multibranch.BranchJobProperty;
import org.kohsuke.stapler.DataBoundConstructor;

public class BranchPriorityStrategy extends AbstractDynamicPriorityStrategy {
    @Extension
    static public class BranchPriorityStrategyDescriptor extends AbstractDynamicPriorityStrategyDescriptor {

        public BranchPriorityStrategyDescriptor() {
            super("Set Priority from branch name");
        }
    };

    private String branchName;
    private int priority;

    @DataBoundConstructor
    public BranchPriorityStrategy(String branchName, int priority) {
        this.branchName = branchName;
        this.priority = priority;
    }

    @Override
    public boolean isApplicable(Queue.Item item) {
        return true;
    }

    @Override
    public int getPriority(Queue.Item item) {
        if(item.task instanceof Job<?, ?>) {
            Job<?, ?> job = (Job<?, ?>) item.task;
            BranchJobProperty priorityProperty = job.getProperty(BranchJobProperty.class);
            if (priorityProperty != null && priorityProperty.getBranch().getName().matches(branchName)) {
                return priority;
            }
        }
        return 3;
    }

    public String getBranchName() {
        return branchName;
    }

    public int getPriority() {
        return priority;
    }
}

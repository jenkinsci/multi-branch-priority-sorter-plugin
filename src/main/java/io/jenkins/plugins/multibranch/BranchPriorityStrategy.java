package io.jenkins.plugins.multibranch;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import hudson.Extension;
import hudson.model.Job;
import hudson.model.Queue;
import jenkins.advancedqueue.priority.strategy.AbstractDynamicPriorityStrategy;
import org.jenkinsci.plugins.workflow.multibranch.BranchJobProperty;
import jenkins.advancedqueue.PrioritySorterConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;

public class BranchPriorityStrategy extends AbstractDynamicPriorityStrategy {
    @Extension
    public static class BranchPriorityStrategyDescriptor extends AbstractDynamicPriorityStrategyDescriptor {

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

    @CheckForNull
    private Integer getPriorityInternal(Queue.Item item) {
        if (item.task instanceof Job<?, ?>) {
            Job<?, ?> job = (Job<?, ?>) item.task;
            BranchJobProperty branchProperty = job.getProperty(BranchJobProperty.class);
            if (branchProperty != null && branchProperty.getBranch().getName().matches(branchName)) {
                return priority;
            }
        }
        return null;
    }

    @Override
    public boolean isApplicable(Queue.Item item) {
        return getPriorityInternal(item) != null;
    }

    @Override
    public int getPriority(Queue.Item item) {
        final Integer p = getPriorityInternal(item);
        return p != null ?
            p : PrioritySorterConfiguration.get().getStrategy().getDefaultPriority();
    }

    public String getBranchName() {
        return branchName;
    }

    public int getPriority() {
        return priority;
    }
}

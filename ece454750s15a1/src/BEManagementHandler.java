import ece454750s15a1.*;

import java.lang.Exception;
import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class BEManagementHandler implements BEManagement.Iface {

    public PerfCounters perfCounter = new PerfCounters();
    private PerfCounters perfManager = null;
    private Long uptime;

    private List<String> groupMembers;

    public BEManagementHandler(PerfCounters perfManager, Long uptime) {
        this.perfManager = perfManager;
        groupMembers = Arrays.asList("s244sing", "cpinn");
        this.uptime = uptime;
    }

    // Return performance metrics.
    public PerfCounters getPerfCounters() {
        System.out.println("[BEManagementHandler] BEServer Perf counters was invoked");
        perfCounter.numSecondsUp = (int)(System.currentTimeMillis() - uptime);
        perfCounter.numRequestsReceived = perfManager.numRequestsReceived;
        perfCounter.numRequestsCompleted = perfManager.numRequestsCompleted;

        return perfCounter;
    }

    // Return group member list.
    public List<String> getGroupMembers() {
        return this.groupMembers;
    }

    // Other Interfaces

    // Periodic Learning Interface

}
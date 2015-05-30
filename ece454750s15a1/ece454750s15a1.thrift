namespace java ece454750s15a1

# Typedefs for sanity.
typedef i32 int32_t
typedef i16 int16_t

# Data structures.
exception ServiceUnavailableException {
    1: string msg
}

struct PerfCounters {
    1: int32_t numSecondsUp, // sec since startup.
    2: int32_t numRequestsReceived, // # of req rec by service handler.
    3: int32_t numRequestsCompleted // # of req completed by service handler.
}

service BEPassword {

    string hashPassword (
        1: string password,
        2: int16_t logRounds
    ) throws (
        1: ServiceUnavailableException e
    )

    bool checkPassword (
        1: string password,
        2: string hash
    ) throws (
        1: ServiceUnavailableException e
    )

}

service BEManagement {

    // Performance counters. Both FE & BE
    PerfCounters getPerfCounters()

    // Group member names interface.
    list<string> getGroupMembers()

    bool joinCluster (
        1: string host,
        2: int32_t pport,
        3: int32_t mport,
        4: int32_t ncores,
    )
}


service FEManagement {

    // Performance counters. Both FE & BE
    PerfCounters getPerfCounters()

    // Group member names interface.
    list<string> getGroupMembers()

    // Other interfaces.

    /*
        TODO: Join Cluster Interface
        Description: This interface connectes a FE or BE
                     node to the seed nodes (CE) to join
                     the processing cluster.
     */
    bool joinCluster (
        1: string nodeName,
        2: string host,
        3: int32_t pport,
        4: int32_t mport,
        5: int32_t ncores
    )
    
    /*
        TODO: Periodic Learning Interface
        Description: This interface makes all other FE
                     nodes aware of its presence.
     */
}

service FEPassword {

    string hashPassword (
        1: string password,
        2: int16_t logRounds
    ) throws (
        1: ServiceUnavailableException e
    )

    bool checkPassword (
        1: string password,
        2: string hash
    ) throws (
        1: ServiceUnavailableException e
    )

}


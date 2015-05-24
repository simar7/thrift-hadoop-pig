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

service A1Management {

    string hashPassword (
        1: string password,
        2: int16_t logRounds
    ) throws (
        1: ServiceUnavailableException e
    )

    bool checkPassword (
        1: string password,
        2: string hash
    )

    // Performance counters. Both FE & BE
    PerfCounters getPerfCounters()

    // Group member names interface.
    list<string> getGroupMembers()
}
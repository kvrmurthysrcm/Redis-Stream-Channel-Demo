# Github URL:
https://github.com/kvrmurthysrcm/Redis-Stream-Channel-Demo.git

# Launch UI to publish message to Redis Stream:
http://localhost:9095/redis/stream

# UI sends messages to this end-point to publish on Redis Stream:
// http://localhost:9095/redis/send/{sessionId}

// Call this end-point to consume messages in the stream...
// Not a recommended way but it also works...
// Manually track consumed messages.
http://localhost:9095/redis/consume

// Call this end-point to consume messages in the stream...
// This is the recommended way to consume messages..
// it can set ACK on consumed messages.
http://localhost:9095/redis/consume-group

All this functionality works between restarts of Tomcat, Redis.
Messages do persists on Redis.
Manual tracking of messages consumed is also saved in Redis.




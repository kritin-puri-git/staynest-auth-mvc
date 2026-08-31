local key = KEYS[1]

if redis.call("EXISTS", key) == 0 then
    return -1
end

local values = redis.call(
                    "HMGET", key,
                    "remainingResendAttempts",
                    "createdAt"
                )

local remainingResendAttempts = tonumber(values[1])
local createdAt = tonumber(values[2])

if not remainingResendAttempts then
    return 0
end
if not createdAt then
    return 0
end

local currentTime = tonumber(redis.call("TIME")[1])

local cooldown = tonumber(ARGV[1])
if createdAt + cooldown >= currentTime then
    return -2
end

if remainingResendAttempts <=0 then
    return -3
end

remainingResendAttempts = remainingResendAttempts - 1
local hashedOtp = ARGV[2]
local remainingVerifyAttempts = tonumber(ARGV[3])
local ttl = tonumber(ARGV[4])

redis.call(
    "HSET", key,
    "hashedOtp", hashedOtp,
    "remainingVerifyAttempts", remainingVerifyAttempts,
    "remainingResendAttempts", remainingResendAttempts,
    "createdAt", currentTime
)

redis.call("EXPIRE", key, ttl)

return 1
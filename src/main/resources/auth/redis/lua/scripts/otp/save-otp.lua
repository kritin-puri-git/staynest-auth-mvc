local key = KEYS[1]


if redis.call("EXISTS", key) ~= 0 then
    return 0
end

local hashedOtp = ARGV[1]
local remainingVerifyAttempts = tonumber(ARGV[2])
local remainingResendAttempts = tonumber(ARGV[3])
local createdAt = redis.call("TIME")[1]
local ttl = tonumber(ARGV[4])

redis.call(
    "HSET", key,
    "hashedOtp", hashedOtp,
    "remainingVerifyAttempts", remainingVerifyAttempts,
    "remainingResendAttempts", remainingResendAttempts,
    "createdAt", createdAt
)

redis.call("EXPIRE", key, ttl)

return 1

local key = KEYS[1]
if redis.call("EXISTS", key) == 0 then
    return -2
end

local remainingVerifyAttempts = redis.call(
                                "HINCRBY", key,
                                "remainingVerifyAttempts",
                                 -1
                            )

if(tonumber(remainingVerifyAttempts) < 0) then
    return -3
end

local userOtp = ARGV[1]

local sysOtp = redis.call("HGET", key, "hashedOtp")

if not sysOtp then
    return -4
end

if(userOtp ~= sysOtp) then
    return -5
end

redis.call("DEL", key)

return 1

local key = KEYS[1]

if redis.call("EXISTS", key) == 0 then
    return 0
end

local expiry = tonumber(ARGV[1])
local currentTime = tonumber(redis.call("TIME")[1])
local createdAt = tonumber(redis.call("HGET", key, "createdAt"))

if createdAt + expiry >= currentTime then
    return 1
end

return 0
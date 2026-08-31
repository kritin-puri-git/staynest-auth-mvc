local key = KEYS[1]

if redis.call("EXISTS", key) ~= 0 then
    return -1
end

local encryptedUsername = ARGV[1]
local encryptedEmail = ARGV[2]
local encryptionKeyId = ARGV[3]
local encryptionVersion = ARGV[4]

local ttl = tonumber(ARGV[5])

if not encryptedUsername
        or not encryptedEmail
        or not encryptionKeyId
        or not encryptionVersion
        or not ttl
        or ttl < 100 then
    return 0
end

local now = tonumber(redis.call("TIME")[1])
local expiresAt = now + ttl
redis.call(
        "HSET", key,
        "encryptedUsername", encryptedUsername,
        "encryptedEmail", encryptedEmail,
        "encryptionKeyId", encryptionKeyId,
        "encryptionVersion", encryptionVersion,
        "createdAt", now,
        "expiresAt", expiresAt
)

redis.call("EXPIRE", key, ttl)

return 1

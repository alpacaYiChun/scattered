local expect = tonumber(ARGV[1])
local expire = tonumber(ARGV[2])

local already = redis.call('llen', KEYS[1])
local gap = expect - already

local i = 3

while i <= len + 2 and gap > 0 do
    redis.call('rpush', KEYS[1], ARGV[i])
    i = i + 1
    gap = gap - 1
end

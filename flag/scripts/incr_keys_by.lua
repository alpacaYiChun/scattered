local len = tonumber(ARGV[1])
local expire = tonumber(ARGV[2])

for i=1, len
do
    redis.call('incrby', KEYS[i], ARGV[i+2])
    redis.call('expire', KEYS[i], expire)
end
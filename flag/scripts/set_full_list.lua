local len = tonumber(ARGV[1])
local expire = tonumber(ARGV[2])
for i = 3, len + 2
do
	redis.call('rpush', KEYS[1], ARGV[i])
end
redis.call('expire', KEYS[1], expire)
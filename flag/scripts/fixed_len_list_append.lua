local num = redis.call('llen', KEYS[1]);
local limit = tonumber(ARGV[2])
local expire = tonumber(ARGV[3])
if num >= limit then
	redis.call('lpop', KEYS[1])
end
redis.call('rpush', KEYS[1], ARGV[1])
redis.call('expire', KEYS[1], expire)
return redis.call('llen', KEYS[1])
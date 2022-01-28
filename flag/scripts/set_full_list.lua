local len = tonumber(ARGV[1])
for i = 2, len + 1
do
	redis.call('rpush', KEYS[1], ARGV[i])
end
package com.fivebit.common;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 链接redis的类
 */

public class Sredis {
	@Autowired
	Slog log;

	private JedisPool pool = null;
	private JedisSentinelPool spool = null;
	private Sredis jredis ;
	private String host;
	private Integer port;
	private Integer timeout;
	private String password = "";
	public String prefix="sredis";

	public synchronized Sredis getInstance(){
		if( null == jredis){
			jredis = new Sredis();
		}
		return jredis;
	}
	public void init(){
		log.info("sredis init begin");
	}
	public  void distory(){
		log.info("redis distory end");
	}
	public Sredis(){
	}
	public Sredis(String host,int port,int timeout,String prefix,String password){
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.prefix = prefix;
		this.password = password;
	}
	public Sredis(String host,int port,int timeout,String prefix){
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.prefix = prefix;
	}
	public Sredis(String host,int port,int timeout){
		this(host,port,timeout,"sredis");
	}

	/**
	 * 建立连接池 真实环境，一般把配置参数缺抽取出来。
	 * 
	 */
	private void createJedisPool() {
		// 建立连接池配置参数
		JedisPoolConfig config = new  JedisPoolConfig();
		// 设置最大连接数,和系统句柄有关
		config.setMaxTotal(1024);
		// 设置最大阻塞时间，是毫秒数milliseconds
		config.setMaxWaitMillis(1000);
		// 设置空间连接
		config.setMaxIdle(10);
		//连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		config.setBlockWhenExhausted(true);
		//最小空闲连接数, 默认0
		config.setMinIdle(1);
		//在获取连接的时候检查有效性, 默认false
		config.setTestOnBorrow(true);
		//在空闲时检查有效性, 默认false
		config.setTestWhileIdle(false);
		//在还给pool时，是否提前进行validate操作
		config.setTestOnReturn(true);
		// 创建连接池
		pool = new JedisPool(config, host, port,timeout,password);
		log.info("create jedis pool:"+pool.toString());
	}
	private void createSentinelPool() {
		// 建立连接池配置参数
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		// 设置最大连接数,和系统句柄有关
		config.setMaxTotal(1024);
		// 设置最大阻塞时间，记住是毫秒数milliseconds
		config.setMaxWaitMillis(1000);
		// 设置空间连接
		config.setMaxIdle(10);
		//config.setTestOnBorrow(true);
		//连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		config.setBlockWhenExhausted(true);
		// 创建连接池
		Set<String> sentinels = new HashSet<String>();
        sentinels.add(host+":"+port);
		spool = new JedisSentinelPool("mymaster",sentinels,config,timeout);
		log.info("create jedis pool:"+spool.toString());
	}

	/**
	 * 在多线程环境同步初始化
	 */
	private synchronized void poolInit() {
		if (pool == null){
			createJedisPool();
		}
	}
	private synchronized void spoolInit() {
		if (spool == null){
			createSentinelPool();
		}
	}

	/**
	 * 获取一个jedis 对象
	 * 
	 * @return
	 */
	public Jedis getJedis() {
		if (pool == null)
			poolInit();
		Jedis tmp = pool.getResource();
		int pactive = pool.getNumActive();
		int idel = pool.getNumIdle();
		int waiters = pool.getNumWaiters();
		log.debug("get jedis:active:"+pactive+" idel:"+idel+" waiters:"+waiters);
		return tmp;
	}
	public Jedis getJedisByS() {
		if (spool == null)
			spoolInit();
		Jedis tmp = spool.getResource();
		HostAndPort hp = spool.getCurrentHostMaster();
		int port = hp.getPort();
		String host = hp.getHost();
		String lhq = hp.getLocalHostQuietly();
		int pactive = spool.getNumActive();
		int idel = spool.getNumIdle();
		int waiters = spool.getNumWaiters();
		log.debug("get jedis:active:"+pactive+" idel:"+idel+" waiters:"+waiters+" port"+port
				+" host:"+host+" lhq:"+lhq);
		return tmp;
	}

	public void returnRes(Jedis jedis) {
	    returnRes(jedis,false);
    }
	/**
	 * 归还一个连接
	 * 
	 * @param jedis
	 */
	public void returnRes(Jedis jedis,Boolean connectionBroken) {
		if(jedis != null){
			try{
				if(connectionBroken == true){
					pool.returnBrokenResource(jedis);
				}else{
					pool.returnResource(jedis);
				}
			}catch(Exception ee){
				log.error("return res error:"+ee.getMessage());
			}
			log.info("return res"+jedis.toString());
		}
	}
	public void returnResByS(Jedis jedis) {
		if(jedis != null){
			spool.returnResource(jedis);
			log.info("return res"+jedis.toString());
		}
	}
	/**
	 * 设置缓存
	 * @param key
	 * @param value
	 * @param time 超时时间 单位是秒
	 */
	public void addString(String key ,String value,long time) {
		Jedis redisClient = null;
		try {
			redisClient = getJedis();
			value = isNullOrEmpty(value) ? "" : value;
			//EX: seconds PX:milliseconds
			// NX:Only set the key if it does not already exist. XX -- Only set the key if it already exist.
			redisClient.set(prefix+"::"+key, value,"NX","EX",time);
		} catch (Exception e) {
            log.error("add string error:"+e.getMessage());
		} finally { // 还原到连接池
			returnRes(redisClient);
		}
	}
	public void addString(String key ,String value) {
		Jedis redisClient = null;
		try {
			redisClient = getJedis();
			value = isNullOrEmpty(value) ? "" : value;
			redisClient.set(prefix+"::"+key, value);
		} catch (Exception e) {
			log.error("add string error:"+e.getMessage());
		} finally { // 还原到连接池
			returnRes(redisClient);
		}
	}
	public String getString(String key){
		Jedis redisClient = null;
		try {
			redisClient = getJedis();
			return redisClient.get(prefix+"::"+key);
		} catch (Exception e) {
			log.error("get string error:"+e.getMessage());
		}  finally { // 还原到连接池
			returnRes(redisClient);
		}
		return null;
	}
	public void setDataToRedis(String key, String field, String value) {
		Jedis redisClient = null;
		try {
			redisClient = getJedis();
			redisClient.hset(prefix+"::"+key, field, value);
		} catch (Exception e) { // 销毁对象
			log.error("set data to redis error:"+e.getMessage());
		} finally { // 还原到连接池
			returnRes(redisClient);
		}
	}
	public Map<String, String> getMapData(String key) {
		Map<String, String> dataMap = null;
		Jedis redisClient = null;
		try {
			redisClient = getJedis();
			dataMap = redisClient.hgetAll(prefix+"::"+key);
		} catch (Exception e) { // 销毁对象
			log.error("get map data error:"+e.getMessage());
		} finally { // 还原到连接池
			returnRes(redisClient);
		}
		return dataMap;
	}
	public long deleteData(String key) {
		long result = 0;
		Jedis redisClient = null;
		try {
			redisClient = getJedis();
			result = redisClient.del(prefix+"::"+key);
		} catch (Exception e) { // 销毁对象
			log.error("delete data error:"+e.getMessage());
		} finally { // 还原到连接池
			returnRes(redisClient);
		}
		return result;
	}
	public String getData(String key, String field) {
		String data = null;
		Jedis redisClient = null;
		try {
			redisClient = getJedis();
			data = redisClient.hget(key, field);
		} catch (Exception e) { // 销毁对象
			log.error("get data error:"+e.getMessage());
		} finally { // 还原到连接池
			returnRes(redisClient);
		}
		return data;
	}
	public boolean isNullOrEmpty(Object obj) {
		if (obj == null)
			return true;

		if (obj instanceof CharSequence)
			return ((CharSequence) obj).length() == 0;

		if (obj instanceof Collection)
			return ((Collection) obj).isEmpty();

		if (obj instanceof Map)
			return ((Map) obj).isEmpty();

		if (obj instanceof Object[]) {
			Object[] object = (Object[]) obj;
			if (object.length == 0) {
				return true;
			}
			boolean empty = true;
			for (int i = 0; i < object.length; i++) {
				if (!isNullOrEmpty(object[i])) {
					empty = false;
					break;
				}
			}
			return empty;
		}
		return false;
	}
	public Object unserialize( byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
		}
		return null;
	}
	public byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
		}
		return null;
	}

}

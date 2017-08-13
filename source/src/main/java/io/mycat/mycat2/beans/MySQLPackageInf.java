package io.mycat.mycat2.beans;

/**
 * 表示MySQL 数据报文的信息
 * 
 * @author wuzhihui
 *
 */
public class MySQLPackageInf {
	/**
	 * 数据报文的类型信息
	 */
	public int pkgType;
	
	/**
	 * 跨多个报文标识
	 */
	public boolean crossBuffer;
	
	
	/**
	 * 开始的位置
	 */
	public int startPos;
	
	
	/**
	 * 结束位置
	 */
	public int endPos;
	
	
	/**
	 * 报文长度
	 */
	public int pkgLength;
	/**
	 * 还有多少字节才结束，仅对跨多个Buffer的MySQL报文有意义（crossBuffer=true)
	 */
	public int remainsBytes;
}

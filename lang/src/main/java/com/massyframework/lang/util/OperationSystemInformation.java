/*
 * Copyright: 2018 smarabbit studio.
 *
 * Licensed under the Confluent Community License; you may not use this file
 * except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * @作   者： 黄开晖 (117227773@qq.com)
 * @日   期:  2019年2月2日
 */
package com.massyframework.lang.util;

/**
 * 操作系统信息
 */
public final class OperationSystemInformation {

	private static String OS = 
			System.getProperty("os.name").toLowerCase();
	
	/**
	 * 当前操作系统是否为liunx
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isLinux(){
		return OS.indexOf("linux")>=0;
	}
	
	/**
	 * 当前操作系统是否为MacOS
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isMacOS(){
		return OS.indexOf("mac")>=0&&OS.indexOf("os")>0&&OS.indexOf("x")<0;
	}
	
	/**
	 * 当前操作系统是否为MacOSX
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isMacOSX(){
		return OS.indexOf("mac")>=0&&OS.indexOf("os")>0&&OS.indexOf("x")>0;
	}
	
	/**
	 * 当前操作系统是否为Windows
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isWindows(){
		return OS.indexOf("windows")>=0;
	}
	
	/**
	 * 当前操作系统是否为OS2
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isOS2(){
		return OS.indexOf("os/2")>=0;
	}
	
	/**
	 * 当前操作系统是否为Solaris
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isSolaris(){
		return OS.indexOf("solaris")>=0;
	}
	
	/**
	 * 当前操作系统是否为Sun OS
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isSunOS(){
		return OS.indexOf("sunos")>=0;
	}
	
	/**
	 * 当前操作系统是否为MPEix
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isMPEiX(){
		return OS.indexOf("mpe/ix")>=0;
	}
	
	/**
	 * 当前操作系统是否为HPUX
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isHPUX(){
		return OS.indexOf("hp-ux")>=0;
	}
	
	/**
	 * 当前操作系统是否为Aix
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isAix(){
		return OS.indexOf("aix")>=0;
	}
	
	/**
	 * 当前操作系统是否为OS390
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isOS390(){
		return OS.indexOf("os/390")>=0;
	}
	
	/**
	 * 当前操作系统是否为FreeBSD
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isFreeBSD(){
		return OS.indexOf("freebsd")>=0;
	}
	
	/**
	 * 当前操作系统是否为Irix
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isIrix(){
		return OS.indexOf("irix")>=0;
	}
	
	/**
	 * 当前操作系统是否为digital
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isDigitalUnix(){
		return OS.indexOf("digital")>=0&&OS.indexOf("unix")>0;
	}
	
	/**
	 * 当前操作系统是否为NetWare
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isNetWare(){
		return OS.indexOf("netware")>=0;
	}
	
	/**
	 * 当前操作系统是否为OSF1
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isOSF1(){
		return OS.indexOf("osf1")>=0;
	}
	
	/**
	 * 当前操作系统是否为OpenVMS
	 * @return {@link boolean}, 返回<code>true</code>表示是
	 */
	public static boolean isOpenVMS(){
		return OS.indexOf("openvms")>=0;
	}

}

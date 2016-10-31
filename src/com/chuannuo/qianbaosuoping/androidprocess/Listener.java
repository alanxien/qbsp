/* 
 * @Title:  Listener.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-2-25 下午11:19:46 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.androidprocess;

import java.util.List;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-2-25 下午11:19:46 
 * @version:  V1.0 
 */

public interface Listener {

  void onComplete(List<AndroidAppProcess> processes);
}

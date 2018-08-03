package com.suda.yzune.wakeupschedule.course_add

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class AddCourseRepository(context: Context) {

    private lateinit var detailList: MutableList<CourseEditBean>
    private lateinit var baseBean: CourseBaseBean
    private var oldBaseBean: CourseBaseBean? = null
    private var oldDetailList: List<CourseDetailBean>? = null
    private var updateFlag = true

    private val dataBase = AppDatabase.getDatabase(context)
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()
    private val deleteList = arrayListOf<Int>()
    private val saveInfo = MutableLiveData<String>()

    fun getSaveInfo(): LiveData<String> {
        return saveInfo
    }

    fun getUpdateFlag(): Boolean {
        return updateFlag
    }

    fun saveData(newId: Int) {
        val saveList = mutableListOf<CourseDetailBean>()
        if (baseBean.id == -1) {
            updateFlag = false
            baseBean.id = newId
            detailList.forEach {
                it.id = newId
            }
        }
        for (i in detailList.indices) {
            if (i !in deleteList) {
                saveList.addAll(CourseUtils.editBean2DetailBeanList(detailList[i]))
            }
        }

        if (updateFlag) {
            thread(name = "updateCourseThread") {
                if (oldBaseBean == null) {
                    oldBaseBean = baseDao.getCourseBeanByIdAndTableNameInThread(baseBean.id, baseBean.tableName)
                    oldDetailList = detailDao.getDetailByIdAndTableNameInThread(baseBean.id, baseBean.tableName)
                }
                try {
                    baseDao.updateCourseBaseBean(baseBean)
                    detailDao.deleteByIdAndTableName(baseBean.id, baseBean.tableName)
                    detailDao.insertList(saveList)
                    saveInfo.postValue("ok")
                } catch (e: SQLiteConstraintException) {
//                    baseDao.updateCourseBaseBean(oldBaseBean)
//                    detailDao.deleteByIdAndTableName(baseBean.id, baseBean.tableName)
//                    detailDao.insertList(oldDetailList)
                    saveInfo.postValue("更新异常")
                }
            }
        } else {
            thread(name = "insertNewCourseThread") {
                try {
                    baseDao.insertCourseBase(baseBean)
                    detailDao.insertList(saveList)
                    saveInfo.postValue("ok")
                } catch (e: SQLiteConstraintException) {
                    Log.d("异常", e.toString())
                    //detailDao.deleteByIdAndTableName(baseBean.id, baseBean.tableName)
                    saveInfo.postValue("插入异常")
                }
            }
        }
    }

    fun removeInsert(){
        thread(name = "removeInsertThread") {
            baseDao.deleteCourseBaseBean(baseBean.id, baseBean.tableName)
        }
    }

    fun checkSameName(): LiveData<CourseBaseBean> {
        return baseDao.checkSameName(baseBean.courseName, baseBean.tableName)
    }

    fun initData(): MutableList<CourseEditBean> {
        detailList = mutableListOf(CourseEditBean())
        return detailList
    }

    fun initData(id: Int): LiveData<List<CourseDetailBean>> {
        detailList = mutableListOf()
        return detailDao.getDetailById(id)
    }

    fun getList(): MutableList<CourseEditBean> {
        return detailList
    }

    fun rollBackData() {
        thread(name = "rollBackDataThread") {
            baseDao.updateCourseBaseBean(oldBaseBean!!)
            detailDao.insertList(oldDetailList!!)
        }
    }

    fun getDeleteList(): ArrayList<Int> {
        return deleteList
    }

    fun getLastId(): LiveData<Int> {
        return baseDao.getLastId()
    }

    fun initBaseData(): CourseBaseBean {
        baseBean = CourseBaseBean(-1, "", "", "")
        return baseBean
    }

    fun initBaseData(id: Int): LiveData<CourseBaseBean> {
        baseBean = CourseBaseBean(-1, "", "", "")
        return baseDao.getCourseById(id)
    }

    fun getBaseData(): CourseBaseBean {
        return baseBean
    }
}
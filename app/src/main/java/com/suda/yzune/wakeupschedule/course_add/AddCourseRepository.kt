package com.suda.yzune.wakeupschedule.course_add

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import android.util.Log
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.dao.CourseBaseDao
import com.suda.yzune.wakeupschedule.dao.CourseDetailDao
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
    private val saveList = mutableListOf<CourseDetailBean>()


    fun getSaveInfo(): LiveData<String> {
        return saveInfo
    }

    fun getUpdateFlag(): Boolean {
        return updateFlag
    }

    fun preSaveData(newId: Int) {
        saveList.clear()
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

        val selfUnique = CourseUtils.checkSelfUnique(saveList)

        if (selfUnique) {
            CheckUniqueAsyncTask(detailDao).execute(saveList)
        } else {
            saveInfo.value = "自身重复"
        }
    }

    fun saveData(isUnique: Boolean) {
        if (isUnique) {
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
                        saveInfo.postValue("异常")
                    }
                }
            } else {
                thread(name = "insertNewCourseThread") {
                    try {
                        baseDao.insertCourseBase(baseBean)
                        detailDao.insertList(saveList)
                        saveInfo.postValue("ok")
                    } catch (e: SQLiteConstraintException) {
                        saveInfo.postValue("异常")
                    }
                }
            }
        }
        else{
            saveInfo.value = "其他重复"
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

    @SuppressLint("StaticFieldLeak")
    inner class CheckUniqueAsyncTask internal constructor(private val mAsyncTaskDao: CourseDetailDao) : AsyncTask<List<CourseDetailBean>, Void, Boolean>() {
        override fun doInBackground(vararg params: List<CourseDetailBean>): Boolean {
            var flag = true
            params[0].forEach {
                val result = mAsyncTaskDao.getDetailByKeys(it.day, it.startNode, it.startWeek, it.type, it.tableName)
                if (result.isNotEmpty()) {
                    if (result[0].id != it.id) {
                        flag = false
                        return flag
                    }
                }
            }
            return flag
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            this@AddCourseRepository.saveData(result)
        }
    }
}

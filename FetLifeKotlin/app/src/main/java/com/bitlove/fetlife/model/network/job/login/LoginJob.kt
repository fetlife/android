package com.bitlove.fetlife.model.network.job.login

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.technical.JobProgressEntity
import com.bitlove.fetlife.model.dataobject.entity.user.UserEntity
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.dataobject.wrapper.User
import com.bitlove.fetlife.model.network.job.BaseJob
import com.bitlove.fetlife.model.network.networkobject.AuthBody
import org.jetbrains.anko.coroutines.experimental.bg

class LoginJob(private val username: String, private var password: String, private val rememberUser: Boolean): BaseJob(PRIORITY_LOGIN,false, null, TAG_LOGIN){

    companion object {
        const val TAG_LOGIN = "TAG_LOGIN"
    }

    override fun onRunJob() : Boolean {
        //TODO: error handling
        //TODO: use production secret
        val loginCall = getApi().login(
                "d8f8ebd522bf5123c3f29db3c8faf09029a032b44f0d1739d4325cd3ccf11570",
                "47273306a9a3a3448a908748eff13a21a477cc46f6a3968b5c7d05611c4f2f26",
                "urn:ietf:wg:oauth:2.0:oob",
                AuthBody(username, password))
                password = "0000000000000000000000"
        val loginResult = loginCall.execute()
        if (!loginResult.isSuccessful) {
            return false
        }
        val tokenResult = loginResult.body()

        val authHeader = "Bearer " + tokenResult!!.accessToken
        val refreshToken = tokenResult!!.refreshToken

        val meCall = getApi().getMe(authHeader)
        val meResult = meCall.execute()
        return if (meResult.isSuccessful) {
            //TODO(cleanup): obfuscate

            val memberEntity = meResult.body()!!
            val userEntity = UserEntity(memberEntity.dbId, username, System.currentTimeMillis(), if (rememberUser) authHeader else null, if (rememberUser) tokenResult.refreshToken else null, rememberUser, false)

            val user = User()
            user.userEntity = userEntity
            user.memberEntity = memberEntity

            FetLifeApplication.instance.fetLifeUserDatabase.userDao().insert(userEntity)
            FetLifeApplication.instance.onUserLoggedIn(user, authHeader, refreshToken)
            getDatabaseWrapper().safeRun(user.getLocalId(), {
                contentDb ->
                val userBody = meResult.body()!!
                if (userBody.isSupporter != true) {
                    contentDb.exploreStoryDao().deleteAll()
                }
                contentDb.memberDao().insertOrUpdate(userBody)
            })
            true
        } else false
    }

    override fun updateProgressState(state: ProgressTracker.STATE, delay: Long, message: String?, addSource: Boolean) {
        bg {
            //TODO: verify the need of this delay, verify the need of Anko (default kotlin my be more enhanced)
            Thread.sleep(delay)
            val jobProgressEntity = JobProgressEntity(progressTrackerId, state.toString(), message)
            val jobProgressDao = FetLifeApplication.instance.fetLifeUserDatabase.jobProgressDao()
            if (addSource) {
                if (lastTrackerSource != null) progressTrackerLiveData.removeSource(lastTrackerSource!!)
                lastTrackerSource = jobProgressDao.getTracker(progressTrackerId)
                progressTrackerLiveData.addSource(lastTrackerSource!!,{data -> progressTrackerLiveData.value = data})
            }
            jobProgressDao?.insertOrUpdate(jobProgressEntity)
        }
    }

}
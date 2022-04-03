package com.example.users.mainfragment.model.mappers

import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.dto.DBUser

class UserDBMapper : Mapper<DBUser, FullUserInfo>{
    override fun map(input: DBUser): FullUserInfo {
        TODO("Not yet implemented")
    }
}
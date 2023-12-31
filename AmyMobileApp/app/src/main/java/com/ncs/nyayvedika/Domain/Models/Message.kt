package com.ncs.o2.Domain.Models

import com.ncs.o2.Domain.Utility.Later

/*
File : Contributor.kt -> com.ncs.o2.Models
Description : Model for Contributor 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : O2 Android)

Creation : 1:36 am on 05/06/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION :

*/
open class Message (
        val message : String,
        open var msgType : Int
        )

